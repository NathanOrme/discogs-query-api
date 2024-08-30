package org.discogs.query.service;

import lombok.extern.slf4j.Slf4j;
import org.discogs.query.enums.DiscogFormats;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
public class DefaultDiscogsQueryService implements DiscogsQueryService {

    @Value("${discogs.url}")
    private String discogsBaseUrl;

    @Value("${discogs.search}")
    private String discogsSearchEndpoint;

    @Value("${discogs.agent}")
    private String discogsAgent;

    @Value("${discogs.page-size}")
    private int pageSize;

    @Value("${discogs.token}")
    private String token;

    @Override
    public DiscogsResultDTO searchBasedOnQuery(final DiscogsQueryDTO discogsQueryDTO) {
        var searchUrl = buildSearchUrl(discogsBaseUrl.concat(discogsSearchEndpoint), discogsQueryDTO);
        var headers = buildHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        var restTemplate = new RestTemplate();
        var response = restTemplate.exchange(searchUrl, HttpMethod.GET, entity, DiscogsResultDTO.class);
        log.info(String.valueOf(response.getBody()));
        return response.getBody();
    }

    private String buildSearchUrl(final String searchUrl, final DiscogsQueryDTO discogsQueryDTO) {
        var urlBuilder = searchUrl;
        var artist = discogsQueryDTO.getArtist();
        artist = artist.replace(" ", "+");
        urlBuilder = urlBuilder.concat("?artist=".concat(artist));
        var track = discogsQueryDTO.getTrack();
        track = track.replace(" ", "+");
        urlBuilder = appendUrlAsAppropriate(urlBuilder, "track", track);
        if (discogsQueryDTO.getFormat() != null && !discogsQueryDTO.getFormat().isBlank()) {
            urlBuilder = appendUrlAsAppropriate(urlBuilder, "format", discogsQueryDTO.getFormat());
        } else {
            urlBuilder = appendUrlAsAppropriate(urlBuilder, "format", DiscogFormats.COMP.getFormat());
        }
        urlBuilder = urlBuilder.concat("&per_page=").concat(String.valueOf(pageSize)).concat("&page=1");
        urlBuilder = urlBuilder.concat("&token=").concat(token);
        return urlBuilder;

    }

    private HttpHeaders buildHeaders() {
        var headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Agent", discogsAgent);
        return headers;
    }

    private String appendUrlAsAppropriate(final String urlBuilder, final String field, final String stirngToAppend) {
        String builtString;
        if (urlBuilder.contains("?")) {
            builtString = urlBuilder.concat("&".concat(field).concat("=").concat(stirngToAppend));
        } else {
            builtString = urlBuilder.concat("?".concat(field).concat("=").concat(stirngToAppend));
        }
        return builtString;
    }
}
