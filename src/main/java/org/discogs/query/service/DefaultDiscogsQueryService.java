package org.discogs.query.service;

import lombok.extern.slf4j.Slf4j;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @Override
    public DiscogsResultDTO searchBasedOnQuery(DiscogsQueryDTO discogsQueryDTO) {
        String searchUrl = buildSearchUrl(discogsBaseUrl.concat(discogsSearchEndpoint), discogsQueryDTO);
        HttpHeaders headers = buildHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(searchUrl, HttpMethod.GET, entity, String.class);
        log.info(response.getBody());

        return null;
    }

    private String buildSearchUrl(String searchUrl, DiscogsQueryDTO discogsQueryDTO) {
        String urlBuilder = searchUrl;
        String artist = discogsQueryDTO.getArtist();
        if(artist != null && !discogsQueryDTO.getArtist().isBlank()){
            artist = artist.replace(" ", "+");
            urlBuilder = urlBuilder.concat("?artist=".concat(artist));
        }
        String track = discogsQueryDTO.getTrack();
        if(track != null &&!track.isBlank()){
            track = track.replace(" ", "+");
            urlBuilder = appendUrlAsAppropriate(urlBuilder, "track", track);
        }
        if(discogsQueryDTO.getFormat() != null && !discogsQueryDTO.getFormat().isBlank()){
            urlBuilder = appendUrlAsAppropriate(urlBuilder, "format", discogsQueryDTO.getFormat());
        }
        return urlBuilder;

    }

    private HttpHeaders buildHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Agent", discogsAgent);
        return headers;
    }

    private String appendUrlAsAppropriate(String urlBuilder, String field, String stirngToAppend) {
        String builtString;
        if(urlBuilder.contains("?")){
            builtString = urlBuilder.concat("&".concat(field).concat("=").concat(stirngToAppend));
        } else {
            builtString = urlBuilder.concat("?".concat(field).concat("=").concat(stirngToAppend));
        }
        builtString = builtString.concat("&per_page=").concat(String.valueOf(pageSize)).concat("&page=1");
        return builtString;
    }
}
