package org.discogs.query.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.domain.api.DiscogsEntry;
import org.discogs.query.helpers.LogHelper;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.enums.DiscogQueryParams;
import org.discogs.query.model.enums.DiscogsTypes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/** Utility class to build URLs for Discogs API requests. */
@Getter
@Slf4j
@Component
@RequiredArgsConstructor
public class DiscogsUrlBuilder {

  public static final String PER_PAGE = "per_page";
  public static final String PAGE = "page";
  public static final String TOKEN = "token";
  private final UriBuilderHelper uriBuilderHelper;
  private final StringHelper stringHelper;

  @Value("${discogs.url}")
  String discogsBaseUrl;

  @Value("${discogs.search}")
  String discogsSearchEndpoint;

  @Value("${discogs.collection}")
  String discogsCollectionEndpoint;

  @Value("${discogs.release}")
  String releaseEndpoint;

  @Value("${discogs.page-size}")
  int pageSize;

  @Value("${discogs.token}")
  String token;

  @Value("${discogs.baseUrl}")
  String discogsWebsiteBaseUrl;

  @Value("${discogs.marketplaceCheck}")
  String marketplaceUrl;

  /**
   * Builds URL string by encoding it, then replacing the space encoding.
   *
   * @param uriBuilder Builder to use
   * @return Built URL String
   */
  private static String getUrlString(final UriComponentsBuilder uriBuilder) {
    return uriBuilder.encode().toUriString().replace("%20", "+").replace(" ", "+");
  }

  private static DiscogsQueryDTO generateDTOForSearching(final DiscogsQueryDTO discogsQueryDTO) {
    String track = discogsQueryDTO.track().replace(" ", "+");
    String artist = discogsQueryDTO.artist().replace(" ", "+");
    return new DiscogsQueryDTO(
        null,
        discogsQueryDTO.album(),
        null,
        artist.concat("+-+").concat(track),
        discogsQueryDTO.format().replace(" ", "+"),
        discogsQueryDTO.country(),
        discogsQueryDTO.types(),
        discogsQueryDTO.barcode());
  }

  /**
   * Builds the search URL based on the provided query parameters.
   *
   * @param discogsQueryDTO the search query data transfer object containing the search criteria
   * @return the fully constructed search URL with query parameters
   */
  public String buildSearchUrl(final DiscogsQueryDTO discogsQueryDTO) {
    LogHelper.debug(() -> "Building search URL with parameters: {}", discogsQueryDTO);

    UriComponentsBuilder uriBuilder =
        UriComponentsBuilder.fromHttpUrl(discogsBaseUrl.concat(discogsSearchEndpoint))
            .queryParam(PER_PAGE, pageSize)
            .queryParam(PAGE, 1)
            .queryParam(TOKEN, token);

    addQueryParams(uriBuilder, discogsQueryDTO);
    String searchUrl = getUrlString(uriBuilder);
    LogHelper.debug(() -> "Generated search URL: {}", searchUrl);
    return searchUrl;
  }

  /**
   * Builds the release URL for the given DiscogsEntry.
   *
   * @param discogsEntry the Discogs entry containing the release ID
   * @return the fully constructed release URL
   */
  public String buildReleaseUrl(final DiscogsEntry discogsEntry) {
    LogHelper.debug(() -> "Building release URL for DiscogsEntry ID: {}", discogsEntry.getId());

    String releaseUrl =
        UriComponentsBuilder.fromHttpUrl(
                discogsBaseUrl.concat(releaseEndpoint).concat(String.valueOf(discogsEntry.getId())))
            .queryParam(TOKEN, token)
            .queryParam("curr_abbr", "GBP")
            .toUriString();
    LogHelper.debug(() -> "Generated release URL: {}", releaseUrl);
    return releaseUrl;
  }

  /**
   * Generates a URL to search against a user's discogs collection
   *
   * @param username Username to use
   * @param releaseId Release to use
   * @return A built URL
   */
  public String buildCollectionSearchUrl(final String username, final String releaseId) {
    LogHelper.debug(() -> "Building collection URL for Release ID: {}", releaseId);
    String collectionsUrl = discogsCollectionEndpoint.formatted(username, releaseId);
    collectionsUrl =
        UriComponentsBuilder.fromHttpUrl(discogsBaseUrl.concat(collectionsUrl)).toUriString();
    LogHelper.debug(() -> "Generated collection URL: {}", collectionsUrl);
    return collectionsUrl;
  }

  /**
   * Builds the marketplace URL for the given DiscogsEntry.
   *
   * @param discogsEntry the Discogs entry containing the release ID
   * @return the fully constructed release URL
   */
  public String buildMarketplaceUrl(final DiscogsEntry discogsEntry) {
    LogHelper.debug(() -> "Building marketplace URL for DiscogsEntry ID: {}", discogsEntry.getId());

    String releaseUrl =
        UriComponentsBuilder.fromHttpUrl(
                discogsBaseUrl.concat(marketplaceUrl).concat(String.valueOf(discogsEntry.getId())))
            .queryParam(TOKEN, token)
            .queryParam("curr_abbr", "GBP")
            .toUriString();
    LogHelper.debug(() -> "Generated marketplace URL: {}", releaseUrl);
    return releaseUrl;
  }

  /**
   * Adds query parameters to the URI builder based on the DiscogsQueryDTO object. If a barcode is
   * supplied, only the barcode is used as a query parameter. Otherwise, other query parameters such
   * as artist, album, track, format, country, and type are added.
   *
   * @param uriBuilder the URI builder to which query parameters will be added
   * @param discogsQueryDTO the data transfer object containing query parameters
   */
  private void addQueryParams(
      final UriComponentsBuilder uriBuilder, final DiscogsQueryDTO discogsQueryDTO) {
    LogHelper.debug(() -> "Adding query parameters to URL: {}", discogsQueryDTO);

    if (handleBarcodeParam(uriBuilder, discogsQueryDTO)) {
      return;
    }

    handleOtherParams(uriBuilder, discogsQueryDTO);
  }

  /**
   * Handles adding the barcode parameter to the URI builder if it is present in the
   * DiscogsQueryDTO. If the barcode is supplied, only the barcode is used.
   *
   * @param uriBuilder the URI builder to which the barcode parameter will be added
   * @param discogsQueryDTO the data transfer object containing the barcode
   * @return true if the barcode parameter was added, false otherwise
   */
  private boolean handleBarcodeParam(
      final UriComponentsBuilder uriBuilder, final DiscogsQueryDTO discogsQueryDTO) {
    if (stringHelper.isNotNullOrBlank(discogsQueryDTO.barcode())) {
      LogHelper.info(() -> "Barcode supplied, only using that for entry");
      uriBuilderHelper.addIfNotNullOrBlank(
          uriBuilder, DiscogQueryParams.BARCODE.getQueryType(), discogsQueryDTO.barcode());
      return true; // Barcode handled
    }
    return false; // No barcode handled
  }

  /**
   * Handles adding other query parameters (artist, album, track, format, country, and type) to the
   * URI builder if the barcode is not supplied in the DiscogsQueryDTO.
   *
   * @param uriBuilder the URI builder to which other query parameters will be added
   * @param discogsQueryDTO the data transfer object containing the other query parameters
   */
  private void handleOtherParams(
      final UriComponentsBuilder uriBuilder, final DiscogsQueryDTO discogsQueryDTO) {
    uriBuilderHelper.addIfNotNullOrBlank(
        uriBuilder, DiscogQueryParams.ARTIST.getQueryType(), discogsQueryDTO.artist());

    uriBuilderHelper.addIfNotNullOrBlank(
        uriBuilder, DiscogQueryParams.ALBUM.getQueryType(), discogsQueryDTO.album());

    uriBuilderHelper.addIfNotNullOrBlank(
        uriBuilder, DiscogQueryParams.TRACK.getQueryType(), discogsQueryDTO.track());

    uriBuilderHelper.addIfNotNullOrBlank(
        uriBuilder, DiscogQueryParams.FORMAT.getQueryType(), discogsQueryDTO.format());

    if (discogsQueryDTO.country() != null) {
      uriBuilderHelper.addIfNotNullOrBlank(
          uriBuilder,
          DiscogQueryParams.COUNTRY.getQueryType(),
          discogsQueryDTO.country().getCountryName());
    }

    DiscogsTypes types = discogsQueryDTO.types();
    if (types == null || DiscogsTypes.UNKNOWN == types) {
      types = DiscogsTypes.RELEASE;
    }
    uriBuilderHelper.addIfNotNull(
        uriBuilder, DiscogQueryParams.TYPE.getQueryType(), types.getType());
  }

  /**
   * Builds the search URL for a compilation album based on the provided query parameters.
   *
   * @param discogsQueryDTO the search query data transfer object containing the search criteria
   * @return the fully constructed search URL with query parameters
   */
  public String generateCompilationSearchUrl(final DiscogsQueryDTO discogsQueryDTO) {
    LogHelper.debug(() -> "Building compilation search URL with parameters: {}", discogsQueryDTO);
    DiscogsQueryDTO dtoForUrl = generateDTOForSearching(discogsQueryDTO);

    UriComponentsBuilder uriBuilder =
        UriComponentsBuilder.fromHttpUrl(discogsBaseUrl.concat(discogsSearchEndpoint))
            .queryParam(PER_PAGE, pageSize)
            .queryParam(PAGE, 1)
            .queryParam(TOKEN, token);
    addQueryParams(uriBuilder, dtoForUrl);
    uriBuilderHelper.addIfNotNullOrBlank(
        uriBuilder, DiscogQueryParams.Q.getQueryType(), dtoForUrl.title());
    String compilationSearchUrl = getUrlString(uriBuilder);
    compilationSearchUrl = compilationSearchUrl.replace(" ", "+");
    LogHelper.debug(() -> "Generated compilation search URL: {}", compilationSearchUrl);
    return compilationSearchUrl;
  }
}
