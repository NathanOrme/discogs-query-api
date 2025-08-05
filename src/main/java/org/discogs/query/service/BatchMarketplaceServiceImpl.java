package org.discogs.query.service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.helpers.LogHelper;
import org.discogs.query.interfaces.BatchMarketplaceService;
import org.discogs.query.interfaces.DiscogsWebScraperClient;
import org.discogs.query.model.DiscogsEntryDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Implementation of BatchMarketplaceService that optimizes marketplace checking
 * by batching API calls and deduplicating requests.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BatchMarketplaceServiceImpl implements BatchMarketplaceService {

  private final DiscogsWebScraperClient discogsWebScraperClient;
  
  @Qualifier("discogsScraperExecutor")
  private final Executor scraperExecutor;

  @Override
  public List<DiscogsEntryDTO> filterEntriesWithUKMarketplace(final List<DiscogsEntryDTO> entries) {
    if (entries.isEmpty()) {
      return entries;
    }

    // Extract unique release IDs to avoid duplicate API calls
    Set<Integer> uniqueReleaseIds = entries.stream()
        .map(DiscogsEntryDTO::id)
        .collect(Collectors.toSet());

    LogHelper.info(() -> "Batch checking marketplace availability for {} unique releases (from {} total entries)", 
        uniqueReleaseIds.size(), entries.size());

    // Batch check marketplace availability
    Set<Integer> availableReleaseIds = batchCheckMarketplaceAvailability(uniqueReleaseIds);

    // Filter entries based on marketplace availability
    List<DiscogsEntryDTO> filteredEntries = entries.stream()
        .filter(entry -> availableReleaseIds.contains(entry.id()))
        .toList();

    LogHelper.info(() -> "Filtered {} entries with UK marketplace listings from {} original entries", 
        filteredEntries.size(), entries.size());

    return filteredEntries;
  }

  @Override
  @Cacheable(value = "batchMarketplaceCheck", key = "#releaseIds.hashCode()")
  public Set<Integer> batchCheckMarketplaceAvailability(final Set<Integer> releaseIds) {
    LogHelper.debug(() -> "Starting batch marketplace check for {} release IDs", releaseIds.size());

    // Use concurrent processing with proper thread safety
    Set<Integer> availableReleases = ConcurrentHashMap.newKeySet();

    // Process with dedicated thread pool for better resource control
    List<CompletableFuture<Void>> futures = releaseIds.stream()
        .map(releaseId -> CompletableFuture.runAsync(() -> {
          try {
            boolean hasMarketplaceListings = !discogsWebScraperClient
                .getMarketplaceResultsForRelease(String.valueOf(releaseId))
                .isEmpty();
            
            if (hasMarketplaceListings) {
              availableReleases.add(releaseId);
              LogHelper.debug(() -> "Release {} has UK marketplace listings", releaseId);
            }
          } catch (final Exception e) {
            LogHelper.warn(() -> "Failed to check marketplace for release {}: {}", releaseId, e.getMessage());
            // Don't include releases that fail the marketplace check
          }
        }, scraperExecutor))
        .toList();

    // Wait for all marketplace checks to complete
    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

    LogHelper.info(() -> "Batch marketplace check completed: {}/{} releases have UK listings", 
        availableReleases.size(), releaseIds.size());

    return availableReleases;
  }
}