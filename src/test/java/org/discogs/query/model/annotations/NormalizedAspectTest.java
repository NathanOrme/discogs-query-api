package org.discogs.query.model.annotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.discogs.query.interfaces.NormalizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(properties = {
    "discogs.agent=test-agent",
    "discogs.token=test-token",
    "spring.security.allowed-origins="
})
@ContextConfiguration(classes = {NormalizedAspect.class})
class NormalizedAspectTest {

  @MockBean private NormalizationService normalizationService;

  private NormalizedAspect normalizedAspect;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    normalizedAspect = new NormalizedAspect(normalizationService);
  }

  @Test
  void testNormalizeFields() throws IllegalAccessException, NoSuchFieldException {
    // Create a test object with a field annotated with @Normalized
    TestClass testObject = new TestClass("cafe & test");

    // Set up the mock behavior for normalizationService
    when(normalizationService.normalizeString("cafe & test")).thenReturn("cafe and test");

    // Invoke the normalizeFields method
    normalizedAspect.normalizeFields(testObject);

    // Verify the field was normalized
    assertEquals("cafe and test", testObject.getNormalizedField());
  }

  // Sample class with a field annotated with @Normalized
  @Getter
  @RequiredArgsConstructor
  public static class TestClass {

    @Normalized private final String normalizedField;
  }
}
