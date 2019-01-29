/*
 * Copyright 2018-2019 Karakun AG.
 * Copyright 2015-2018 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.rico.internal.logging.bridges;

import dev.rico.internal.core.ansi.AnsiOut;
import dev.rico.internal.logging.LoggerConfiguration;
import dev.rico.internal.logging.spi.LoggerBridge;
import dev.rico.internal.logging.spi.LogMessage;
import org.slf4j.event.Level;

import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

public class SimpleRicoLogger implements LoggerBridge {

    private final DateFormat dateFormat;

    public SimpleRicoLogger(final LoggerConfiguration configuration) {
        this.dateFormat = Objects.requireNonNull(configuration.getDateFormat());
    }

    @Override
    public void log(final LogMessage logMessage) {
        final String textColor = Optional.ofNullable(logMessage.getLevel()).
                map(l -> {
                    if (l.equals(Level.ERROR)) {
                        return AnsiOut.ANSI_RED;
                    }
                    if (l.equals(Level.WARN)) {
                        return AnsiOut.ANSI_YELLOW;
                    }
                    if (l.equals(Level.INFO)) {
                        return AnsiOut.ANSI_BLUE;
                    }
                    return AnsiOut.ANSI_CYAN;
                }).orElse(AnsiOut.ANSI_CYAN);

        final StringBuilder buf = new StringBuilder();
        buf.append(AnsiOut.ANSI_WHITE);
        final Date timestamp = Date.from(logMessage.getTimestamp().toInstant());
        buf.append(dateFormat.format(timestamp));
        buf.append(AnsiOut.ANSI_RESET);

        buf.append(" ");

        buf.append(AnsiOut.ANSI_BOLD);
        buf.append(textColor);
        buf.append(logMessage.getLevel());

        buf.append(" - ");

        buf.append(logMessage.getMessage());
        buf.append(AnsiOut.ANSI_RESET);

        buf.append(AnsiOut.ANSI_WHITE);
        buf.append(" - ");

        buf.append(logMessage.getLoggerName());


        if (!logMessage.getMarker().isEmpty()) {
            buf.append(" - [");
            for (String marker : logMessage.getMarker()) {
                buf.append(marker);
                if (logMessage.getMarker().indexOf(marker) < logMessage.getMarker().size() - 1) {
                    buf.append(", ");
                }
            }
            buf.append("]");
        }
        buf.append(" - ");
        buf.append(logMessage.getThreadName());
        buf.append(AnsiOut.ANSI_RESET);

        if (logMessage.getThrowable() != null) {
            buf.append(AnsiOut.ANSI_RED);
            buf.append(System.lineSeparator());
            buf.append(logMessage.getExceptionDetail());
            buf.append(AnsiOut.ANSI_RESET);
        }
        print(buf.toString());
    }


    private synchronized void print(final String message) {
        System.out.println(message);
    }
}
