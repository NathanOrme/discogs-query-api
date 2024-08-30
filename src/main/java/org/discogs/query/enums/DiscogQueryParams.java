package org.discogs.query.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DiscogQueryParams {

    TRACK("track"),
    FORMAT("format"),
    ARTIST("artist"),
    TYPE("type");

    private final String queryType;

}
