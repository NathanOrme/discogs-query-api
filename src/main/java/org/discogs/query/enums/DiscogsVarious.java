package org.discogs.query.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DiscogsVarious {

    VARIOUS("various"),
    VARIOUS_ARTIST("various artists");

    private final String variousName;

}
