package org.discogs.query.helpers;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

class LogHelperTest {

  private Logger mockLogger;

  @BeforeEach
  void setUp() {
    mockLogger = mock(Logger.class);
  }

  @Test
  void testDebugLogWhenEnabled() {
    when(mockLogger.isDebugEnabled()).thenReturn(true);
    Supplier<String> messageSupplier = () -> "Debug message with args: {} and {}";

    assertDoesNotThrow(() -> LogHelper.debug(messageSupplier, "arg1", "arg2"));
  }

  @Test
  void testInfoLogWhenEnabled() {
    when(mockLogger.isInfoEnabled()).thenReturn(true);
    Supplier<String> messageSupplier = () -> "Info message with args: {}";

    assertDoesNotThrow(() -> LogHelper.info(messageSupplier, "arg1"));
  }

  @Test
  void testWarnLogWhenEnabled() {
    when(mockLogger.isWarnEnabled()).thenReturn(true);
    Supplier<String> messageSupplier = () -> "Warn message";

    assertDoesNotThrow(() -> LogHelper.warn(messageSupplier));
  }

  @Test
  void testErrorLogWhenEnabled() {
    when(mockLogger.isErrorEnabled()).thenReturn(true);
    Supplier<String> messageSupplier = () -> "Error message";

    assertDoesNotThrow(() -> LogHelper.error(messageSupplier));
  }
}
