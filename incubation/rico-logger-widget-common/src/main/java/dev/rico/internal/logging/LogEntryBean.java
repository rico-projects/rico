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
package dev.rico.internal.logging;

import dev.rico.remoting.RemotingBean;
import dev.rico.remoting.ObservableList;
import dev.rico.remoting.Property;
import org.slf4j.event.Level;

import java.time.ZonedDateTime;

@RemotingBean
public class LogEntryBean {

    private Property<String> message;

    private Property<String> loggerName;

    private Property<Level> logLevel;

    private Property<ZonedDateTime> logTimestamp;

    private Property<String> threadName;

    private Property<String> exceptionMessage;

    private Property<String> exceptionClass;

    private ObservableList<String> marker;

    public String getMessage() {
        return message.get();
    }

    public Property<String> messageProperty() {
        return message;
    }

    public void setMessage(final String message) {
        this.message.set(message);
    }

    public String getLoggerName() {
        return loggerName.get();
    }

    public Property<String> loggerNameProperty() {
        return loggerName;
    }

    public void setLoggerName(final String loggerName) {
        this.loggerName.set(loggerName);
    }

    public ZonedDateTime getLogTimestamp() {
        return logTimestamp.get();
    }

    public Property<ZonedDateTime> logTimestampProperty() {
        return logTimestamp;
    }

    public void setLogTimestamp(final ZonedDateTime logTimestamp) {
        this.logTimestamp.set(logTimestamp);
    }

    public String getThreadName() {
        return threadName.get();
    }

    public Property<String> threadNameProperty() {
        return threadName;
    }

    public void setThreadName(final String threadName) {
        this.threadName.set(threadName);
    }

    public String getExceptionMessage() {
        return exceptionMessage.get();
    }

    public Property<String> exceptionMessageProperty() {
        return exceptionMessage;
    }

    public void setExceptionMessage(final String exceptionMessage) {
        this.exceptionMessage.set(exceptionMessage);
    }

    public String getExceptionClass() {
        return exceptionClass.get();
    }

    public Property<String> exceptionClassProperty() {
        return exceptionClass;
    }

    public void setExceptionClass(final String exceptionClass) {
        this.exceptionClass.set(exceptionClass);
    }

    public ObservableList<String> getMarker() {
        return marker;
    }

    public Level getLogLevel() {
        return logLevel.get();
    }

    public Property<Level> logLevelProperty() {
        return logLevel;
    }

    public void setLogLevel(final Level logLevel) {
        this.logLevel.set(logLevel);
    }
}
