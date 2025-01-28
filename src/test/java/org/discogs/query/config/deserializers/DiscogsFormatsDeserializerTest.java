package org.discogs.query.config.deserializers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import org.discogs.query.model.enums.DiscogsFormats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/** Test class for {@link DiscogsFormatsDeserializer}. */
class DiscogsFormatsDeserializerTest {

  private ObjectMapper objectMapper;
  private DiscogsFormatsDeserializer deserializer;

  @BeforeEach
  public void setUp() {
    deserializer = new DiscogsFormatsDeserializer();
    objectMapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addDeserializer(DiscogsFormats.class, deserializer);
    objectMapper.registerModule(module);
  }

  @Test
  void testDeserializeValidEnum() throws IOException {
    JsonParser parser = Mockito.mock(JsonParser.class);
    DeserializationContext context = Mockito.mock(DeserializationContext.class);

    Mockito.when(parser.getText()).thenReturn("VINYL");

    DiscogsFormats result = deserializer.deserialize(parser, context);

    assertEquals(
        DiscogsFormats.VINYL,
        result,
        "Deserialization should " + "convert 'VINYL' to DiscogFormats.VINYL");
  }

  @Test
  void testDeserializeNull() throws IOException {
    JsonParser parser = Mockito.mock(JsonParser.class);
    DeserializationContext context = Mockito.mock(DeserializationContext.class);

    Mockito.when(parser.getText()).thenReturn(null);

    DiscogsFormats result = deserializer.deserialize(parser, context);

    assertNull(result, "Deserialization of null should return null");
  }

  @Test
  void testDeserializeEmptyString() throws IOException {
    JsonParser parser = Mockito.mock(JsonParser.class);
    DeserializationContext context = Mockito.mock(DeserializationContext.class);

    Mockito.when(parser.getText()).thenReturn("");

    DiscogsFormats result = deserializer.deserialize(parser, context);

    assertNull(result, "Deserialization of an empty string should return " + "null");
  }

  @Test
  void testDeserializeInvalidValue() throws IOException {
    JsonParser parser = Mockito.mock(JsonParser.class);
    DeserializationContext context = Mockito.mock(DeserializationContext.class);

    Mockito.when(parser.getText()).thenReturn("UNKNOWN_FORMAT");

    DiscogsFormats result = deserializer.deserialize(parser, context);

    assertNull(result, "Deserialization of an invalid value should return" + " null");
  }
}
