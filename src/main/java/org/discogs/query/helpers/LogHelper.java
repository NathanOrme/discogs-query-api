package org.discogs.query.helpers;

import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for logging with level checks. This class provides static methods to log messages
 * at different levels while ensuring that the log level is enabled before evaluating the log
 * message.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LogHelper {

  /**
   * Logs a debug message if the debug level is enabled.
   *
   * @param messageSupplier a Supplier that provides the log message
   * @param args arguments to be included in the log message
   */
  public static void debug(final Supplier<String> messageSupplier, final Object... args) {
    if (log.isDebugEnabled()) {
      String message = messageSupplier.get();
      log.debug(message, args);
    }
  }

  /**
   * Logs an info message if the info level is enabled.
   *
   * @param messageSupplier a Supplier that provides the log message
   * @param args arguments to be included in the log message
   */
  public static void info(final Supplier<String> messageSupplier, final Object... args) {
    if (log.isInfoEnabled()) {
      String message = messageSupplier.get();
      log.info(message, args);
    }
  }

  /**
   * Logs a warning message if the warn level is enabled.
   *
   * @param messageSupplier a Supplier that provides the log message
   * @param args arguments to be included in the log message
   */
  public static void warn(final Supplier<String> messageSupplier, final Object... args) {
    if (log.isWarnEnabled()) {
      String message = messageSupplier.get();
      log.warn(message, args);
    }
  }

  /**
   * Logs an error message if the error level is enabled.
   *
   * @param messageSupplier a Supplier that provides the log message
   * @param args arguments to be included in the log message
   */
  public static void error(final Supplier<String> messageSupplier, final Object... args) {
    if (log.isErrorEnabled()) {
      String message = messageSupplier.get();
      log.error(message, args);
    }
  }
}
