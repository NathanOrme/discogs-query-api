package org.discogs.query.service;

import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;

public interface DiscogsQueryService {

    DiscogsResultDTO searchBasedOnQuery(DiscogsQueryDTO discogsQueryDTO);

}
