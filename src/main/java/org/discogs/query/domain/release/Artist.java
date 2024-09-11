package org.discogs.query.domain.release;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Artist {
    private String anv;
    private int id;
    private String join;
    private String name;
    @JsonProperty("resource_url")
    private String resourceUrl;
    private String role;
    private String tracks;
}
