package org.discogs.query.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DiscogFormats {

    VINYL("vinyl"),
    COMP("compilation"),
    VINYL_COMPILATION("compilation vinyl");

    private final String format;
}
