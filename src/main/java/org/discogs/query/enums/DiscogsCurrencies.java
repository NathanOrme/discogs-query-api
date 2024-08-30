package org.discogs.query.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DiscogsCurrencies {

    GBP("gbp"),
    USD("usd");

    private final String currency;
}
