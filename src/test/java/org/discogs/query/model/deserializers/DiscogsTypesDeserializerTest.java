package org.discogs.query.model.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.discogs.query.enums.DiscogsTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test class for {@link DiscogsTypesDeserializer}.
 */
class DiscogsTypesDeserializerTest {

    private ObjectMapper objectMapper;
    private DiscogsTypesDeserializer deserializer;

    @BeforeEach
    public void setUp() {
        deserializer = new DiscogsTypesDeserializer();
        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(DiscogsTypes.class, deserializer);
        objectMapper.registerModule(module);
    }

    @Test
    void testDeserializeValidEnum() throws IOException {
        JsonParser parser = Mockito.mock(JsonParser.class);
        DeserializationContext context = Mockito.mock(DeserializationContext.class);

        Mockito.when(parser.getText()).thenReturn("RELEASE");

        DiscogsTypes result = deserializer.deserialize(parser, context);

        assertEquals(DiscogsTypes.RELEASE, result, "Deserialization should convert 'RELEASE' to DiscogsTypes.RELEASE");
    }

    @Test
    void testDeserializeNull() throws IOException {
        JsonParser parser = Mockito.mock(JsonParser.class);
        DeserializationContext context = Mockito.mock(DeserializationContext.class);

        Mockito.when(parser.getText()).thenReturn(null);

        DiscogsTypes result = deserializer.deserialize(parser, context);

        assertNull(result, "Deserialization of null should return null");
    }

    @Test
    void testDeserializeEmptyString() throws IOException {
        JsonParser parser = Mockito.mock(JsonParser.class);
        DeserializationContext context = Mockito.mock(DeserializationContext.class);

        Mockito.when(parser.getText()).thenReturn("");

        DiscogsTypes result = deserializer.deserialize(parser, context);

        assertNull(result, "Deserialization of an empty string should return null");
    }

    @Test
    void testDeserializeInvalidValue() throws IOException {
        JsonParser parser = Mockito.mock(JsonParser.class);
        DeserializationContext context = Mockito.mock(DeserializationContext.class);

        Mockito.when(parser.getText()).thenReturn("UNKNOWN_TYPE");

        DiscogsTypes result = deserializer.deserialize(parser, context);

        assertNull(result, "Deserialization of an invalid value should return null");
    }
}
