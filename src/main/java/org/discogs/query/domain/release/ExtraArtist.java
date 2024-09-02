package org.discogs.query.domain.release;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonDeserialize(as = ExtraArtist.class)
public class ExtraArtist {
    private String anv;
    private int id;
    private String join;
    private String name;
    @JsonProperty("resource_url")
    private String resourceUrl;
    private String role;
    private String tracks;
}
