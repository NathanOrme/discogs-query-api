package org.discogs.query.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DiscogFormats {

    VINYL("vinyl"),
    COMP("compilation");

    private final String format;
}
