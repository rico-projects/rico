package dev.rico.core.logging;

import dev.rico.internal.core.Assert;

import java.util.ResourceBundle;

public interface LoggerFactory {

    static Logger getLogger(final Class<?> cls) {
        final String name = Assert.requireNonNull(cls, "cls").getName();
        final System.Logger logger = System.getLogger(name);
        return new Logger() {
            @Override
            public String getName() {
                return logger.getName();
            }

            @Override
            public boolean isLoggable(final Level level) {
                return logger.isLoggable(level);
            }

            @Override
            public void log(final Level level, final ResourceBundle bundle, final String msg, final Throwable thrown) {
                logger.log(level, bundle, msg, thrown);
            }

            @Override
            public void log(final Level level, final ResourceBundle bundle, final String format, final Object... params) {
                logger.log(level, bundle, format, params);
            }
        };
    }
}
