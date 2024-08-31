package org.discogs.query.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.discogs.query.enums.DiscogsTypes;

import java.io.IOException;

public class DiscogsTypesDeserializer extends JsonDeserializer<DiscogsTypes> {
    @Override
    public DiscogsTypes deserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException {
        String value = jp.getText();
        if (value == null || value.isEmpty()) {
            return null; // Or handle the default case, e.g., DiscogsTypes.UNKNOWN
        }
        try {
            return DiscogsTypes.valueOf(value.toUpperCase());
        } catch (final IllegalArgumentException e) {
            return null; // Or handle unknown values
        }
    }
}
