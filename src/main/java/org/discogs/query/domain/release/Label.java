package org.discogs.query.domain.release;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonDeserialize(as = Label.class)
public class Label {
    private String catno;
    @JsonProperty("entity_type")
    private String entityType;
    private int id;
    private String name;
    @JsonProperty("resource_url")
    private String resourceUrl;
}
