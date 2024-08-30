package org.discogs.query.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.discogs.query.service.DiscogsQueryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@SuppressWarnings("unused")
@RequestMapping("discogs-query")
public class DiscogsQueryController {

    private final DiscogsQueryService discogsQueryService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DiscogsResultDTO> getCustomerFromReference(@RequestBody final DiscogsQueryDTO discogsQueryDTO) {
        log.info("Received request, attempting to retrieve customer");
        var personalDetailsDTO = discogsQueryService.searchBasedOnQuery(discogsQueryDTO);
        log.info("Customer received");
        return ResponseEntity.status(HttpStatus.OK).body(personalDetailsDTO);
    }


}
