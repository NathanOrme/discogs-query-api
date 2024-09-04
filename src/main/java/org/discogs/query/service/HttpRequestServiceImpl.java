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

@Slf4j
@Component
@RequiredArgsConstructor
public class HttpRequestServiceImpl implements HttpRequestService {

    private final RestTemplate restTemplate;

    @Value("${discogs.agent}")
    private String userAgent;

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

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Agent", userAgent);
        return headers;
    }

    private void logApiResponse(final ResponseEntity<?> response) {
        if (response.getBody() != null) {
            log.info("Discogs API response body: {}", response.getBody());
        } else {
            log.warn("Discogs API response body is empty.");
        }
        log.info("Discogs API response status: {}", response.getStatusCode());
    }
}
