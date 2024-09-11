package org.discogs.query.domain.release;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a label involved in the release. Labels are companies or
 * entities that publish or distribute the release.
 */
@Data
@NoArgsConstructor
public class Label {

    /**
     * The catalog number assigned to the release by the label.
     */
    private String catno;

    /**
     * The type of entity the label represents (e.g., "Label", "Company").
     */
    @JsonProperty("entity_type")
    private String entityType;

    /**
     * The unique identifier of the label.
     */
    private int id;

    /**
     * The name of the label.
     */
    private String name;

    /**
     * The resource URL pointing to the label's page on Discogs.
     */
    @JsonProperty("resource_url")
    private String resourceUrl;
}
