package org.discogs.query;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    properties = {
      "discogs.agent=test-agent",
      "discogs.token=test-token",
      "spring.security.allowed-origins="
    })
class DiscogsApplicationTest {

  private String originalDiscogsAgent;
  private String originalDiscogsToken;
  private String originalAllowedOrigins;

  @BeforeEach
  void setUp() {
    // Save original system properties
    originalDiscogsAgent = System.getProperty("DISCOGS_AGENT");
    originalDiscogsToken = System.getProperty("DISCOGS_TOKEN");
    originalAllowedOrigins = System.getProperty("ALLOWED_ORIGINS");

    // Set test values
    System.setProperty("DISCOGS_AGENT", "test-agent");
    System.setProperty("DISCOGS_TOKEN", "test-token");
    System.setProperty("ALLOWED_ORIGINS", "");
  }

  @AfterEach
  void tearDown() {
    // Restore original system properties
    if (originalDiscogsAgent != null) {
      System.setProperty("DISCOGS_AGENT", originalDiscogsAgent);
    } else {
      System.clearProperty("DISCOGS_AGENT");
    }

    if (originalDiscogsToken != null) {
      System.setProperty("DISCOGS_TOKEN", originalDiscogsToken);
    } else {
      System.clearProperty("DISCOGS_TOKEN");
    }

    if (originalAllowedOrigins != null) {
      System.setProperty("ALLOWED_ORIGINS", originalAllowedOrigins);
    } else {
      System.clearProperty("ALLOWED_ORIGINS");
    }
  }

  @Test
  void main_WithDefaultArgs_ThrowsNoException() {
    assertDoesNotThrow(() -> DiscogsApplication.main(new String[0]));
  }
}
