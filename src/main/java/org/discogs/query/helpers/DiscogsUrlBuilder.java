package org.discogs.query.helpers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.domain.api.DiscogsEntry;
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

  /**
   * Builds the search URL based on the provided query parameters.
   *
   * @param discogsQueryDTO the search query DTO containing the search criteria
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
        getUrlString(
            UriComponentsBuilder.fromHttpUrl(
                    discogsBaseUrl
                        .concat(releaseEndpoint)
                        .concat(String.valueOf(discogsEntry.getId())))
                .queryParam(TOKEN, token)
                .queryParam("curr_abbr", "GBP"));
    LogHelper.debug(() -> "Generated release URL: {}", releaseUrl);
    return releaseUrl;
  }

  /**
   * Generates a URL to search against a user's Discogs collection.
   *
   * @param username Username to use.
   * @param releaseId Release ID to use.
   * @return A built URL.
   */
  public String buildCollectionSearchUrl(final String username, final String releaseId) {
    LogHelper.debug(() -> "Building collection URL for Release ID: {}", releaseId);
    String collectionsUrl = discogsCollectionEndpoint.formatted(username, releaseId);
    String fullUrl =
        getUrlString(UriComponentsBuilder.fromHttpUrl(discogsBaseUrl.concat(collectionsUrl)));
    LogHelper.debug(() -> "Generated collection URL: {}", fullUrl);
    return fullUrl;
  }

  /**
   * Builds the marketplace URL for the given DiscogsEntry.
   *
   * @param discogsEntry the Discogs entry containing the release ID
   * @return the fully constructed marketplace URL
   */
  public String buildMarketplaceUrl(final DiscogsEntry discogsEntry) {
    LogHelper.debug(() -> "Building marketplace URL for DiscogsEntry ID: {}", discogsEntry.getId());
    String releaseUrl =
        getUrlString(
            UriComponentsBuilder.fromHttpUrl(
                    discogsBaseUrl
                        .concat(marketplaceUrl)
                        .concat(String.valueOf(discogsEntry.getId())))
                .queryParam(TOKEN, token)
                .queryParam("curr_abbr", "GBP"));
    LogHelper.debug(() -> "Generated marketplace URL: {}", releaseUrl);
    return releaseUrl;
  }

  /**
   * Adds query parameters to the URI builder based on the DiscogsQueryDTO. If a barcode is
   * supplied, only the barcode parameter is used.
   *
   * @param uriBuilder the URI builder to which query parameters will be added
   * @param discogsQueryDTO the DTO containing query parameters
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
   * Handles adding the barcode parameter to the URI builder if present.
   *
   * @param uriBuilder the URI builder
   * @param discogsQueryDTO the DTO containing the barcode
   * @return true if the barcode parameter was added, false otherwise
   */
  private boolean handleBarcodeParam(
      final UriComponentsBuilder uriBuilder, final DiscogsQueryDTO discogsQueryDTO) {
    if (stringHelper.isNotNullOrBlank(discogsQueryDTO.barcode())) {
      LogHelper.info(() -> "Barcode supplied, only using that for entry");
      uriBuilderHelper.addIfNotNullOrBlank(
          uriBuilder, DiscogQueryParams.BARCODE.getQueryType(), discogsQueryDTO.barcode());
      return true;
    }
    return false;
  }

  /**
   * Handles adding other query parameters (artist, album, track, format, country, type) to the URI
   * builder.
   *
   * @param uriBuilder the URI builder
   * @param discogsQueryDTO the DTO containing query parameters
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
      String countryName = discogsQueryDTO.country().getCountryName();
      uriBuilderHelper.addIfNotNullOrBlank(
          uriBuilder, DiscogQueryParams.COUNTRY.getQueryType(), countryName);
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
   * @param discogsQueryDTO the search query DTO containing the criteria
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
    LogHelper.debug(() -> "Generated compilation search URL: {}", compilationSearchUrl);
    return compilationSearchUrl;
  }

  private static DiscogsQueryDTO generateDTOForSearching(final DiscogsQueryDTO discogsQueryDTO) {
    // Precompute replacements for artist, track, and format
    String artist = discogsQueryDTO.artist().replace(" ", "+");
    String track = discogsQueryDTO.track().replace(" ", "+");
    String format = discogsQueryDTO.format().replace(" ", "+");
    return new DiscogsQueryDTO(
        null,
        discogsQueryDTO.album(),
        null,
        artist.concat("+-+").concat(track),
        format,
        discogsQueryDTO.country(),
        discogsQueryDTO.types(),
        discogsQueryDTO.barcode());
  }
}
