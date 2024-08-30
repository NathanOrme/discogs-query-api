package org.discogs.query.service;

import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.springframework.stereotype.Service;

@Service
public class DefaultDiscogsQueryService implements DiscogsQueryService {
    @Override
    public DiscogsResultDTO searchBasedOnQuery(DiscogsQueryDTO discogsQueryDTO) {
        return null;
    }
}
