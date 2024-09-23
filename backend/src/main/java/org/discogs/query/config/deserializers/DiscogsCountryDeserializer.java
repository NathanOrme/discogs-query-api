package org.discogs.query.config.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.discogs.query.model.enums.DiscogCountries;

import java.io.IOException;

/**
 * Custom deserializer for {@link DiscogCountries} enum using Jackson.
 * <p>
 * This class is responsible for deserializing JSON representation of {@link DiscogCountries} enum values
 * during the deserialization process. It converts a JSON string into the corresponding {@link DiscogCountries} enum
 * constant.
 * <p>
 * The deserializer handles cases where the JSON string may be null, empty, or does not match any known enum constants.
 */
public class DiscogsCountryDeserializer extends JsonDeserializer<DiscogCountries> {

    /**
     * Deserializes a JSON string into a {@link DiscogCountries} enum constant.
     * <p>
     * This method reads the JSON string value from the parser, converts it to uppercase, and attempts to
     * match it with one of the {@link DiscogCountries} enum constants using the {@link DiscogCountries#valueOf(String)}
     * method. If the JSON string is null, empty, or does not match any known enum constants, the method will return
     * {@code null}. This behavior can be adjusted based on application needs, for example by returning a default
     * value like {@link DiscogCountries#UNKNOWN}.
     *
     * @param jp   the JSON parser used to read the JSON content
     * @param ctxt the deserialization context
     * @return the corresponding {@link DiscogCountries} enum constant, or {@code null} if no match is found
     * @throws IOException if there is an error reading the JSON content
     */
    @Override
    public DiscogCountries deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        String value = jp.getText();
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return DiscogCountries.valueOf(value.toUpperCase());
        } catch (final IllegalArgumentException e) {
            return null;
        }
    }
}
