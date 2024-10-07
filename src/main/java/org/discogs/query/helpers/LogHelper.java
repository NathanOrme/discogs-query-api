package org.discogs.query.helpers;

import org.slf4j.Logger;

import java.util.function.Supplier;

/**
 * Utility class for logging with level checks.
 * This class provides static methods to log messages at different levels
 * while ensuring that the log level is enabled before evaluating the log message.
 */
public class LogHelper {

    /**
     * Logs a debug message if the debug level is enabled.
     *
     * @param logger          the logger to log with
     * @param messageSupplier a Supplier that provides the log message
     * @param args            arguments to be included in the log message
     */
    public static void debug(Logger logger, Supplier<String> messageSupplier, Object... args) {
        if (logger.isDebugEnabled()) {
            logger.debug(messageSupplier.get(), args);
        }
    }

    /**
     * Logs an info message if the info level is enabled.
     *
     * @param logger          the logger to log with
     * @param messageSupplier a Supplier that provides the log message
     * @param args            arguments to be included in the log message
     */
    public static void info(Logger logger, Supplier<String> messageSupplier, Object... args) {
        if (logger.isInfoEnabled()) {
            logger.info(messageSupplier.get(), args);
        }
    }

    /**
     * Logs a warning message if the warn level is enabled.
     *
     * @param logger          the logger to log with
     * @param messageSupplier a Supplier that provides the log message
     * @param args            arguments to be included in the log message
     */
    public static void warn(Logger logger, Supplier<String> messageSupplier, Object... args) {
        if (logger.isWarnEnabled()) {
            logger.warn(messageSupplier.get(), args);
        }
    }

    /**
     * Logs an error message if the error level is enabled.
     *
     * @param logger          the logger to log with
     * @param messageSupplier a Supplier that provides the log message
     * @param args            arguments to be included in the log message
     */
    public static void error(Logger logger, Supplier<String> messageSupplier, Object... args) {
        if (logger.isErrorEnabled()) {
            logger.error(messageSupplier.get(), args);
        }
    }
}
