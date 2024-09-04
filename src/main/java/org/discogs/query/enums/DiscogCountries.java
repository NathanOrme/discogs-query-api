package org.discogs.query.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Enum representing a list of countries in the Discogs database.
 * Each enum constant corresponds to a country and is associated with a
 * string value
 * that represents the country's name.
 */
@Getter
@ToString
@AllArgsConstructor
public enum DiscogCountries {

    /**
     * United Kingdom
     */
    UK("UK"),

    /**
     * United States
     */
    US("US"),

    /**
     * Europe (region)
     */
    EUROPE("Europe"),

    /**
     * Costa Rica
     */
    COSTA_RICA("Costa Rica"),

    /**
     * Cuba
     */
    CUBA("Cuba"),

    /**
     * Israel
     */
    ISRAEL("Israel"),

    /**
     * Brazil
     */
    BRAZIL("Brazil"),

    /**
     * Greece
     */
    GREECE("Greece"),

    /**
     * Uruguay
     */
    URUGUAY("Uruguay"),

    /**
     * Latvia
     */
    LATVIA("Latvia"),

    /**
     * Austria
     */
    AUSTRIA("Austria"),

    /**
     * Albania
     */
    ALBANIA("Albania"),

    /**
     * Australia
     */
    AUSTRALIA("Australia"),

    /**
     * New Zealand
     */
    NEW_ZEALAND("New Zealand"),

    /**
     * Paraguay
     */
    PARAGUAY("Paraguay"),

    /**
     * Peru
     */
    PERU("Peru"),

    /**
     * Hungary
     */
    HUNGARY("Hungary"),

    /**
     * Netherlands
     */
    NETHERLANDS("Netherlands"),

    /**
     * Canada
     */
    CANADA("Canada"),

    /**
     * Hong Kong
     */
    HONG_KONG("Hong Kong"),

    /**
     * Trinidad
     */
    TRINIDAD("Trinidad"),

    /**
     * Tobago
     */
    TOBAGO("Tobago"),

    /**
     * Estonia
     */
    ESTONIA("Estonia"),

    /**
     * Fiji
     */
    FIJI("Fiji"),

    /**
     * Philippines
     */
    PHILIPPINES("Philippines"),

    /**
     * Germany
     */
    GERMANY("Germany"),

    /**
     * Georgia
     */
    GEORGIA("Georgia"),

    /**
     * Norway
     */
    NORWAY("Norway"),

    /**
     * Croatia
     */
    CROATIA("Croatia"),

    /**
     * Ireland
     */
    IRELAND("Ireland"),

    /**
     * India
     */
    INDIA("India"),

    /**
     * Japan
     */
    JAPAN("Japan"),

    /**
     * Indonesia
     */
    INDONESIA("Indonesia"),

    /**
     * Denmark
     */
    DENMARK("Denmark"),

    /**
     * South Korea
     */
    SOUTH_KOREA("South Korea"),

    /**
     * Lithuania
     */
    LITHUANIA("Lithuania"),

    /**
     * Malaysia
     */
    MALAYSIA("Malaysia"),

    /**
     * China
     */
    CHINA("China"),

    /**
     * Kenya
     */
    KENYA("Kenya"),

    /**
     * Turkey
     */
    TURKEY("Turkey"),

    /**
     * Bulgaria
     */
    BULGARIA("Bulgaria"),

    /**
     * Taiwan
     */
    TAIWAN("Taiwan"),

    /**
     * Sweden
     */
    SWEDEN("Sweden"),

    /**
     * Finland
     */
    FINLAND("Finland"),

    /**
     * Iceland
     */
    ICELAND("Iceland"),

    /**
     * Czech Republic
     */
    CZECH_REPUBLIC("Czech Republic"),

    /**
     * Russian Federation
     */
    RUSSIAN_FEDERATION("Russian Federation"),

    /**
     * Belgium
     */
    BELGIUM("Belgium"),

    /**
     * France
     */
    FRANCE("France"),

    /**
     * Luxembourg
     */
    LUXEMBOURG("Luxembourg"),

    /**
     * Mexico
     */
    MEXICO("Mexico"),

    /**
     * Venezuela
     */
    VENEZUELA("Venezuela"),

    /**
     * Argentina
     */
    ARGENTINA("Argentina"),

    /**
     * South Africa
     */
    SOUTH_AFRICA("South Africa"),

    /**
     * Colombia
     */
    COLOMBIA("Colombia"),

    /**
     * Slovenia
     */
    SLOVENIA("Slovenia"),

    /**
     * Chile
     */
    CHILE("Chile"),

    /**
     * Italy
     */
    ITALY("Italy"),

    /**
     * The Vatican
     */
    THE_VATICAN("The Vatican"),

    /**
     * San Marino
     */
    SAN_MARINO("San Marino"),

    /**
     * Serbia
     */
    SERBIA("Serbia"),

    /**
     * Montenegro
     */
    MONTENEGRO("Montenegro"),

    /**
     * Slovakia
     */
    SLOVAKIA("Slovakia"),

    /**
     * Portugal
     */
    PORTUGAL("Portugal"),

    /**
     * Switzerland
     */
    SWITZERLAND("Switzerland"),

    /**
     * Liechtenstein
     */
    LIECHTENSTEIN("Liechtenstein"),

    /**
     * Ukraine
     */
    UKRAINE("Ukraine"),

    /**
     * Romania
     */
    ROMANIA("Romania"),

    /**
     * Poland
     */
    POLAND("Poland"),

    /**
     * Unknown country
     */
    UNKNOWN("");

    /**
     * The name of the country associated with the enum constant.
     */
    private final String countryName;

    /**
     * Returns the {@link DiscogCountries} constant associated with the given
     * country string.
     * <p>
     * If the country string does not match any defined constant,
     * {@link #UNKNOWN} is returned.
     *
     * @param country the country string to match
     * @return the {@link DiscogCountries} constant corresponding to the
     * country string,
     * or {@link #UNKNOWN} if no match is found
     */
    public static DiscogCountries fromString(final String country) {
        for (final DiscogCountries t : DiscogCountries.values()) {
            if (t.getCountryName().equalsIgnoreCase(country)) {
                return t;
            }
        }
        return UNKNOWN;
    }

}
