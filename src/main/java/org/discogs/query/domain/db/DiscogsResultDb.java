package org.discogs.query.domain.db;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing the results of a Discogs search
 * query.
 * This class encapsulates a list of {@link DiscogsEntryDb} objects that match
 * the search criteria.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscogsResultDb {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * A list of {@link DiscogsEntryDb} objects that represent the search results.
     */
    @OneToMany(mappedBy = "discogsResultDb", cascade = CascadeType.MERGE, orphanRemoval = true)
    private List<DiscogsEntryDb> results;

    private String searchUrl;
}
