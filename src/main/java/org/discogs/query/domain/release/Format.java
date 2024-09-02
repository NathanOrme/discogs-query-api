package org.discogs.query.domain.release;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonDeserialize(as = Format.class)
public class Format {
    private List<String> descriptions;
    private String name;
    private String qty;
}
