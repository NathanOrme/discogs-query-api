package org.discogs.query.domain.release;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonDeserialize(as = Track.class)
public class Track {
    private String duration;
    private String position;
    private String title;
    @JsonProperty("type_")
    private String type;
}
