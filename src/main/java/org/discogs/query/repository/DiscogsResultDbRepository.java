package org.discogs.query.repository;

import org.discogs.query.domain.db.DiscogsResultDb;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiscogsResultDbRepository extends JpaRepository<DiscogsResultDb, Long> {

    Optional<DiscogsResultDb> findBySearchUrl(String url);
}
