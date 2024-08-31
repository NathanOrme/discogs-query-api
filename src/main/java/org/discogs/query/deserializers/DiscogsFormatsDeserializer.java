package org.discogs.query.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.discogs.query.enums.DiscogFormats;

import java.io.IOException;

/**
 * Custom deserializer for {@link DiscogFormats} enum using Jackson.
 * <p>
 * This class is responsible for deserializing JSON representation of {@link DiscogFormats} enum values
 * during the deserialization process. It converts a JSON string into the corresponding {@link DiscogFormats}
 * enum constant.
 * <p>
 * The deserializer handles cases where the JSON string may be null,
 * empty, or does not match any known enum constants.
 */
public class DiscogsFormatsDeserializer extends JsonDeserializer<DiscogFormats> {

    /**
     * Deserializes a JSON string into a {@link DiscogFormats} enum constant.
     * <p>
     * This method reads the JSON string value from the parser,
     * converts it to uppercase, and attempts to
     * match it with one of the {@link DiscogFormats} enum
     * constants using the {@link DiscogFormats#valueOf(String)}
     * method. If the JSON string is null, empty, or does
     * not match any known enum constants, the method will return
     * {@code null}. This behavior can be adjusted based on application needs,
     * for example by returning a default value like {@link DiscogFormats#UNKNOWN}.
     *
     * @param jp   the JSON parser used to read the JSON content
     * @param ctxt the deserialization context
     * @return the corresponding {@link DiscogFormats} enum constant,
     * or {@code null} if no match is found
     * @throws IOException if there is an error reading the JSON content
     */
    @Override
    public DiscogFormats deserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException {
        String value = jp.getText();
        if (value == null || value.isEmpty()) {
            return null; // Or handle the default case, e.g., DiscogFormats.UNKNOWN
        }
        try {
            return DiscogFormats.valueOf(value.toUpperCase());
        } catch (final IllegalArgumentException e) {
            return null; // Or handle unknown values
        }
    }
}
