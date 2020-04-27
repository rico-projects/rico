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

import dev.rico.internal.core.http.DefaultHttpURLConnectionFactory;
import dev.rico.core.http.HttpURLConnectionFactory;
import org.slf4j.event.Level;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LoggerConfiguration {

    private final ConcurrentMap<String, Level> loggerLevelMap = new ConcurrentHashMap<>();

    private URI remoteUrl;

    private int parallelRequests = Runtime.getRuntime().availableProcessors() / 2 - 1;

    private int maxMessagesPerRequest = 100;

    private int remotingErrorWaitTime = 5_000;

    private int maxRemotingQueueSize = 1_000;

    private int remotingQueueCheckSleepTime = 100;

    private Level globalLevel = Level.INFO;

    private Executor remoteLoggingExecutor = new ThreadPoolExecutor(parallelRequests + 1, parallelRequests + 1,
            Long.MAX_VALUE, TimeUnit.DAYS,
            new LinkedBlockingDeque<>(),
            new LoggerThreadFactory());

    private DateFormat dateFormat = new SimpleDateFormat();

    private HttpURLConnectionFactory connectionFactory = new DefaultHttpURLConnectionFactory();

    public void setLevel(final String name, final Level level) {
        loggerLevelMap.put(name, level);
    }

    public Level getLevelFor(final String loggerName) {
        return loggerLevelMap.keySet().stream().
                filter(v -> loggerName.startsWith(v)).
                min((a, b) -> a.compareTo(b)).
                map(v -> loggerLevelMap.get(v)).
                orElse(getGlobalLevel());
    }

    public URI getRemoteUrl() {
        return remoteUrl;
    }

    public void setRemoteUrl(final URI remoteUrl) {
        this.remoteUrl = remoteUrl;
    }

    public Level getGlobalLevel() {
        return globalLevel;
    }

    public void setGlobalLevel(final Level globalLevel) {
        this.globalLevel = globalLevel;
    }

    public Executor getRemoteLoggingExecutor() {
        return remoteLoggingExecutor;
    }

    public void setRemoteLoggingExecutor(final Executor remoteLoggingExecutor) {
        this.remoteLoggingExecutor = remoteLoggingExecutor;
    }

    public DateFormat getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(final DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    public HttpURLConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(final HttpURLConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public int getParallelRequests() {
        return parallelRequests;
    }

    public void setParallelRequests(final int parallelRequests) {
        this.parallelRequests = parallelRequests;
    }

    public int getMaxMessagesPerRequest() {
        return maxMessagesPerRequest;
    }

    public void setMaxMessagesPerRequest(final int maxMessagesPerRequest) {
        this.maxMessagesPerRequest = maxMessagesPerRequest;
    }

    public int getRemotingErrorWaitTime() {
        return remotingErrorWaitTime;
    }

    public void setRemotingErrorWaitTime(final int remotingErrorWaitTime) {
        this.remotingErrorWaitTime = remotingErrorWaitTime;
    }

    public int getMaxRemotingQueueSize() {
        return maxRemotingQueueSize;
    }

    public void setMaxRemotingQueueSize(final int maxRemotingQueueSize) {
        this.maxRemotingQueueSize = maxRemotingQueueSize;
    }

    public int getRemotingQueueCheckSleepTime() {
        return remotingQueueCheckSleepTime;
    }

    public void setRemotingQueueCheckSleepTime(final int remotingQueueCheckSleepTime) {
        this.remotingQueueCheckSleepTime = remotingQueueCheckSleepTime;
    }
}
