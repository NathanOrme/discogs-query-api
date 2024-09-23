package org.discogs.query.config.deserializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.discogs.query.model.enums.DiscogCountries;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DiscogsCountryDeserializerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testDeserializeValidCountry() throws Exception {
        String json = "\"US\"";
        DiscogCountries country = objectMapper.readValue(json,
                DiscogCountries.class);
        assertEquals(DiscogCountries.US, country);
    }

    @Test
    void testDeserializeNullCountry() throws Exception {
        String json = "null";
        DiscogCountries country = objectMapper.readValue(json,
                DiscogCountries.class);
        assertNull(country);
    }
}
