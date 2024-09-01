package org.discogs.query.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum DiscogCountries {


    UK("UK"),
    US("US"),
    EUROPE("Europe"),
    COSTA_RICA("Costa Rica"),
    CUBA("Cuba"),
    ISRAEL("Israel"),
    BRAZIL("Brazil"),
    GREECE("Greece"),
    URUGUAY("Uruguay"),
    LATVIA("Latvia"),
    AUSTRIA("Austria"),
    ALBANIA("Albania"),
    AUSTRALIA("Australia"),
    NEW_ZEALAND("New Zealand"),
    PARAGUAY("Paraguay"),
    PERU("Peru"),
    HUNGARY("Hungary"),
    NETHERLANDS("Netherlands"),
    CANADA("Canada"),
    HONG_KONG("Hong Kong"),
    TRINIDAD("Trinidad"),
    TOBAGO("Tobago"),
    ESTONIA("Estonia"),
    FIJI("Fiji"),
    PHILIPPINES("Philippines"),
    GERMANY("Germany"),
    GEORGIA("Georgia"),
    NORWAY("Norway"),
    CROATIA("Croatia"),
    IRELAND("Ireland"),
    INDIA("India"),
    JAPAN("Japan"),
    INDONESIA("Indonesia"),
    DENMARK("Denmark"),
    SOUTH_KOREA("South Korea"),
    LITHUANIA("Lithuania"),
    MALAYSIA("Malaysia"),
    CHINA("China"),
    KENYA("Kenya"),
    TURKEY("Turkey"),
    BULGARIA("Bulgaria"),
    TAIWAN("Taiwan"),
    SWEDEN("Sweden"),
    FINLAND("Finland"),
    ICELAND("Iceland"),
    CZECH_REPUBLIC("Czech Republic"),
    RUSSIAN_FEDERATION("Russian Federation"),
    BELGIUM("Belgium"),
    FRANCE("France"),
    LUXEMBOURG("Luxembourg"),
    MEXICO("Mexico"),
    VENEZUELA("Venezuela"),
    ARGENTINA("Argentina"),
    SOUTH_AFRICA("South Africa"),
    COLOMBIA("Colombia"),
    SLOVENIA("Slovenia"),
    CHILE("Chile"),
    ITALY("Italy"),
    THE_VATICAN("The Vatican"),
    SAN_MARINO("San Marino"),
    SERBIA("Serbia"),
    MONTENEGRO("Montenegro"),
    SLOVAKIA("Slovakia"),
    PORTUGAL("Portugal"),
    SWITZERLAND("Switzerland"),
    LIECHTENSTEIN("Liechtenstein"),
    UKRAINE("Ukraine"),
    ROMANIA("Romania"),
    POLAND("Poland"),
    UNKNOWN("");

    private final String countryName;

    /**
     * Returns the {@link DiscogCountries} constant associated with the given country string.
     * <p>
     * If the country string does not match any defined constant, {@link #UNKNOWN} is returned.
     *
     * @param country the country string to match
     * @return the {@link DiscogCountries} constant corresponding to the country string,
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

