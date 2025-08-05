package org.discogs.query.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.helpers.LogHelper;
import org.discogs.query.interfaces.DiscogsQueryService;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Optimized async service that uses dedicated thread pools instead of
 * parallelStream() to provide better resource control and performance.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncQueryService {

  private final DiscogsQueryService discogsQueryService;
  
  @Qualifier("discogsApiExecutor")
  private final Executor discogsApiExecutor;

  /**
   * Creates optimized asynchronous tasks for Discogs API queries using
   * dedicated thread pools instead of the common ForkJoinPool.
   *
   * @param queries the list of queries to process asynchronously
   * @return a list of CompletableFuture objects for the queries
   */
  public List<CompletableFuture<DiscogsResultDTO>> createOptimizedFutures(
      final List<DiscogsQueryDTO> queries) {
    
    LogHelper.info(() -> "Creating {} async tasks using dedicated thread pool", queries.size());
    
    return queries.stream()
        .map(query -> CompletableFuture
            .supplyAsync(() -> {
              LogHelper.debug(() -> "Processing query on thread: {} - Query: {}", 
                  Thread.currentThread().getName(), query);
              return discogsQueryService.searchBasedOnQuery(query);
            }, discogsApiExecutor)
            .whenComplete((result, throwable) -> {
              if (throwable != null) {
                LogHelper.error(() -> "Query failed on thread: {} - Error: {}", 
                    Thread.currentThread().getName(), throwable.getMessage());
              } else {
                LogHelper.debug(() -> "Query completed successfully on thread: {} - Results: {}", 
                    Thread.currentThread().getName(), result.results().size());
              }
            }))
        .toList();
  }

  /**
   * Creates a CompletableFuture for a single query with proper error handling.
   *
   * @param query the query to process
   * @return a CompletableFuture for the query result
   */
  public CompletableFuture<DiscogsResultDTO> createSingleQueryFuture(final DiscogsQueryDTO query) {
    return CompletableFuture
        .supplyAsync(() -> {
          LogHelper.debug(() -> "Processing single query: {}", query);
          return discogsQueryService.searchBasedOnQuery(query);
        }, discogsApiExecutor)
        .exceptionally(throwable -> {
          LogHelper.error(() -> "Single query failed: {}", throwable.getMessage());
          // Return empty result instead of null to maintain consistency
          return new DiscogsResultDTO(query, List.of());
        });
  }
}