package org.discogs.query.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.exceptions.DiscogsSearchException;
import org.discogs.query.interfaces.HttpRequestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the {@link HttpRequestService} interface for executing
 * HTTP requests.
 * This class uses {@link RestTemplate} to perform HTTP operations and
 * includes error handling
 * and logging for request execution. It also constructs HTTP headers
 * including a user agent.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpRequestServiceImpl implements HttpRequestService {

    private final RestTemplate restTemplate;

    /**
     * User agent string for HTTP requests.
     */
    @Value("${discogs.agent}")
    private String userAgent;

    /**
     * Executes an HTTP GET request to the specified URL and returns the
     * response body of the specified type.
     * <p>
     * This method constructs the necessary HTTP headers, performs the
     * request using {@link RestTemplate},
     * and logs the request and response details. It throws a
     * {@link DiscogsSearchException} if the request fails
     * or if the response body is {@code null}.
     *
     * @param url          the URL to send the request to.
     * @param responseType the type of the response body to be returned.
     * @param <T>          the type of the response body.
     * @return the response body of type {@code T}.
     * @throws DiscogsSearchException if an error occurs during the request
     * or if the response body is {@code null}.
     */
    @Override
    public <T> T executeRequest(final String url, final Class<T> responseType) {
        log.info("Executing HTTP request to URL: {}", url);

        HttpHeaders headers = buildHeaders();
        log.debug("HTTP headers built: {}", headers);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        log.debug("HTTP entity created with headers");

        try {
            ResponseEntity<T> response = restTemplate.exchange(url,
                    HttpMethod.GET, entity, responseType);
            log.info("Received HTTP response with status code: {}",
                    response.getStatusCode());
            logApiResponse(response);

            return Optional.ofNullable(response.getBody())
                    .orElseThrow(() -> new DiscogsSearchException("Failed to " +
                            "fetch data from Discogs API"));
        } catch (final Exception e) {
            log.error("Error executing HTTP request to URL: {}", url, e);
            throw new DiscogsSearchException("HTTP request failed", e);
        }
    }

    /**
     * Builds HTTP headers for the request.
     * <p>
     * This method sets the `Accept` and `Content-Type` headers to
     * `application/json` and includes
     * a `User-Agent` header.
     *
     * @return the constructed {@link HttpHeaders} object.
     */
    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Agent", userAgent);
        return headers;
    }

    /**
     * Logs details of the HTTP response received from the Discogs API.
     * <p>
     * This method logs the body of the response if it is not {@code null}
     * and also logs the status code.
     *
     * @param response the {@link ResponseEntity} object containing the HTTP
     *                 response details.
     */
    private void logApiResponse(final ResponseEntity<?> response) {
        if (response.getBody() != null) {
            log.info("Discogs API response body: {}", response.getBody());
        } else {
            log.warn("Discogs API response body is empty.");
        }
        log.info("Discogs API response status: {}", response.getStatusCode());
    }
}
