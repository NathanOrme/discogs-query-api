package org.discogs.query.service.requests;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.exceptions.DiscogsSearchException;
import org.discogs.query.helpers.LogHelper;
import org.discogs.query.interfaces.HttpRequestService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class HttpRequestServiceImpl implements HttpRequestService {

  private final RestTemplate restTemplate;
  private final HttpHeaders headers;

  @Override
  public <T> T executeRequest(final String url, final Class<T> responseType) {
    LogHelper.info(() -> "Executing HTTP request to URL: {}", url);
    LogHelper.debug(() -> "HTTP headers built: {}", headers);

    HttpEntity<Void> entity = new HttpEntity<>(headers);
    LogHelper.debug(() -> "HTTP entity created with headers");

    try {
      return processRequestExchange(url, responseType, entity);
    } catch (final Exception e) {
      LogHelper.error(() -> "Error executing HTTP request to URL: {}", url, e);
      throw new DiscogsSearchException("HTTP request failed", e);
    }
  }

  private <T> T processRequestExchange(
      final String url, final Class<T> responseType, final HttpEntity<Void> entity) {
    ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
    LogHelper.info(() -> "Received HTTP response with status code: {}", response.getStatusCode());
    logApiResponse(response);

    return Optional.ofNullable(response.getBody())
        .orElseThrow(() -> new DiscogsSearchException("Failed to fetch data from Discogs API"));
  }

  private void logApiResponse(final ResponseEntity<?> response) {
    if (response.getBody() != null) {
      LogHelper.info(() -> "Discogs API response body: {}", response.getBody());
    } else {
      LogHelper.warn(() -> "Discogs API response body is empty.");
    }
    LogHelper.debug(() -> "Discogs API response status: {}", response.getStatusCode());
  }
}
