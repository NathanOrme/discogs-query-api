package org.discogs.query.repository;

import org.discogs.query.domain.db.DiscogsResultDb;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiscogsResultDbRepository extends JpaRepository<DiscogsResultDb, Long> {

    /**
     * Searches the repository for a result entry based on the search url
     *
     * @param url Url used for query
     * @return Optional Result Object
     */
    Optional<DiscogsResultDb> findBySearchUrl(String url);
}
