package org.discogs.query.helpers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StringHelperTest {

  private final StringHelper stringHelper = new StringHelper();

  @Test
  void testIsNotNullOrBlank_withNonNullNonBlankString() {
    assertTrue(
        stringHelper.isNotNullOrBlank("Hello World"),
        "Expected " + "true for a non-null, non-blank string");
  }

  @Test
  void testIsNotNullOrBlank_withNullString() {
    assertFalse(stringHelper.isNotNullOrBlank(null), "Expected false for " + "a null string");
  }

  @Test
  void testIsNotNullOrBlank_withBlankString() {
    assertFalse(stringHelper.isNotNullOrBlank("   "), "Expected false for" + " a blank string");
  }

  @Test
  void testIsNotNullOrBlank_withEmptyString() {
    assertFalse(stringHelper.isNotNullOrBlank(""), "Expected false for an" + " empty string");
  }

  @Test
  void testIsNotNullOrBlank_withStringContainingWhitespace() {
    assertTrue(
        stringHelper.isNotNullOrBlank("  abc  "),
        "Expected true "
            + "for a string containing non-whitespace characters surrounded"
            + " by whitespace");
  }
}
