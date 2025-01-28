package org.discogs.query.config.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.Locale;
import org.discogs.query.model.enums.DiscogsTypes;

/**
 * Custom deserializer for {@link DiscogsTypes} enum using Jackson.
 *
 * <p>This class is responsible for deserializing JSON representation of {@link DiscogsTypes} enum
 * values during the deserialization process. It converts a JSON string into the corresponding
 * {@link DiscogsTypes} enum constant.
 *
 * <p>The deserializer handles cases where the JSON string may be null, empty, or does not match any
 * known enum constants.
 */
public class DiscogsTypesDeserializer extends JsonDeserializer<DiscogsTypes> {

  /**
   * Deserializes a JSON string into a {@link DiscogsTypes} enum constant.
   *
   * <p>This method reads the JSON string value from the parser, converts it to uppercase, and
   * attempts to match it with one of the {@link DiscogsTypes} enum constants using the {@link
   * DiscogsTypes#valueOf(String)} method. If the JSON string is null, empty, or does not match any
   * known enum constants, the method will return {@code null}. This behavior can be adjusted based
   * on application needs, for example by returning a default value like {@link
   * DiscogsTypes#UNKNOWN}.
   *
   * @param jp the JSON parser used to read the JSON content
   * @param ctxt the deserialization context
   * @return the corresponding {@link DiscogsTypes} enum constant, or {@code null} if no match is
   *     found
   * @throws IOException if there is an error reading the JSON content
   */
  @Override
  public DiscogsTypes deserialize(final JsonParser jp, final DeserializationContext ctxt)
      throws IOException {
    String value = jp.getText();
    if (value == null || value.isEmpty()) {
      return null;
    }
    try {
      return DiscogsTypes.valueOf(value.toUpperCase(Locale.ENGLISH));
    } catch (final IllegalArgumentException e) {
      return null;
    }
  }
}
