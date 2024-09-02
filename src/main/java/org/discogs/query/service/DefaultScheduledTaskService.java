package org.discogs.query.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.enums.DiscogCountries;
import org.discogs.query.enums.DiscogsFormats;
import org.discogs.query.enums.DiscogsTypes;
import org.discogs.query.interfaces.ScheduledTaskService;
import org.discogs.query.model.DiscogsQueryDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of {@link ScheduledTaskService} that periodically
 * sends HTTP POST requests to a specified URL
 * with a predefined payload. This service is scheduled to run every 45 minutes.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultScheduledTaskService implements ScheduledTaskService {

    private final RestTemplate restTemplate;

    /**
     * The payload to be sent in the POST request.
     * Each entry represents a {@link DiscogsQueryDTO} object with track, artist,
     * and format information.
     */
    private static final List<DiscogsQueryDTO> payload = List.of(
            DiscogsQueryDTO.builder()
                    .track("Love Train")
                    .artist("The O'Jays")
                    .format("Compilation Vinyl")
                    .build(),
            DiscogsQueryDTO.builder()
                    .track("A fifth of beethoven")
                    .artist("Walter Murphy")
                    .format("Compilation Vinyl")
                    .build(),
            DiscogsQueryDTO.builder()
                    .track("In the summertime")
                    .artist("Mungo Jerry")
                    .build(),
            DiscogsQueryDTO.builder()
                    .artist("Enya")
                    .build(),
            DiscogsQueryDTO.builder()
                    .artist("War")
                    .track("Why can't we be friends")
                    .types(DiscogsTypes.RELEASE)
                    .format(DiscogsFormats.VINYL.getFormat())
                    .country(DiscogCountries.UK)
                    .build()
    );

    /**
     * The URL to which the POST request will be sent.
     */
    private static final String URL =
            "https://discogs-query-api.onrender.com/discogs-query/search";

    /**
     * Sends an HTTP POST request to the {@link #URL} with the predefined {@link #payload}. The method is scheduled
     * to run every 14 minutes, as specified by the {@link Scheduled} annotation.
     *
     * <p>This method sets the content type of the request to {@code application/json} and handles the response
     * by logging the status and body of the response. In case of an error during the request, the error message
     * is logged.
     */
    @Override
    @Scheduled(fixedRate = 14, timeUnit = TimeUnit.MINUTES)
    public void sendRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        HttpEntity<List<DiscogsQueryDTO>> requestEntity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate
                    .exchange(URL, HttpMethod.POST, requestEntity, String.class);
            log.info("Response Status: {}", response.getStatusCode());
            log.info("Response Body: {}", response.getBody());
        } catch (final Exception e) {
            log.error("Error sending request: {}", e.getMessage());
        }
    }
}
