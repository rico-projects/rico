package dev.rico.core.logging;

public interface Logger extends System.Logger {

    default void error(final String message, final Throwable t) {
        log(Level.ERROR, message, t);
    }

    default void warn(final String message, final Throwable t) {
        log(Level.WARNING, message, t);
    }

    default void info(final String message, final Throwable t) {
        log(Level.INFO, message, t);
    }

    default void debug(final String message, final Throwable t) {
        log(Level.DEBUG, message, t);
    }

    default void trace(final String message, final Throwable t) {
        log(Level.TRACE, message, t);
    }

    default void error(final String message, final Object... params) {
        log(Level.ERROR, message, params);
    }

    default void warn(final String message, final Object... params) {
        log(Level.WARNING, message, params);
    }

    default void info(final String message, final Object... params) {
        log(Level.INFO, message, params);
    }

    default void debug(final String message, final Object... params) {
        log(Level.DEBUG, message, params);
    }

    default void trace(final String message, final Object... params) {
        log(Level.TRACE, message, params);
    }

    default boolean isErrorEnabled() {
        return isLoggable(Level.ERROR);
    }

    default boolean isWarnEnabled() {
        return isLoggable(Level.WARNING);
    }

    default boolean isInfoEnabled() {
        return isLoggable(Level.INFO);
    }

    default boolean isDebugEnabled() {
        return isLoggable(Level.DEBUG);
    }

    default boolean isTraceEnabled() {
        return isLoggable(Level.TRACE);
    }
}
