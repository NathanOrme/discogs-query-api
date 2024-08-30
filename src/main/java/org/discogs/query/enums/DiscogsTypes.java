package org.discogs.query.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DiscogsTypes {

    RELEASE("release"),
    MASTER("master"),
    ARTIST("artist"),
    LABEL("label");

    private final String type;
}
