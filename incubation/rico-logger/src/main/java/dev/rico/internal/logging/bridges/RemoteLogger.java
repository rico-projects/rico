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

import dev.rico.internal.logging.LoggerConfiguration;
import dev.rico.internal.logging.LoggerUtils;
import dev.rico.internal.logging.spi.LogMessage;
import dev.rico.internal.logging.spi.LoggerBridge;
import dev.rico.core.http.HttpURLConnectionFactory;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static dev.rico.internal.core.http.HttpHeaderConstants.CHARSET;
import static dev.rico.internal.core.http.HttpHeaderConstants.CONTENT_TYPE_HEADER;
import static dev.rico.internal.core.http.HttpHeaderConstants.JSON_MIME_TYPE;
import static dev.rico.internal.core.http.HttpStatus.ACCEPTED;
import static dev.rico.internal.core.http.HttpStatus.HTTP_OK;
import static dev.rico.core.http.RequestMethod.POST;

public class RemoteLogger implements LoggerBridge {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteLogger.class);

    private final URI remoteUrl;

    private final Executor executor;

    private final Gson gson = new Gson();

    private final HttpURLConnectionFactory connectionFactory;

    private final Level level;

    private final AtomicBoolean remotingError = new AtomicBoolean(false);

    private final AtomicLong remotingErrorTime = new AtomicLong(0);

    private final BlockingQueue<LogMessage> messageBlockingQueue = new LinkedBlockingQueue<>();

    public RemoteLogger(final LoggerConfiguration configuration) {
        Objects.requireNonNull(configuration);
        this.remoteUrl = Objects.requireNonNull(configuration.getRemoteUrl());
        this.executor = Objects.requireNonNull(configuration.getRemoteLoggingExecutor());
        this.level = Objects.requireNonNull(configuration.getGlobalLevel());
        this.connectionFactory = Objects.requireNonNull(configuration.getConnectionFactory());

        executor.execute(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        LOG.debug("{} messages in remote logging queue", messageBlockingQueue.size());
                        if (messageBlockingQueue.size() > configuration.getMaxRemotingQueueSize()) {
                            LOG.error("Overflow in remote logger message queue. Max elements: {} - Current: {}", configuration.getMaxRemotingQueueSize(), messageBlockingQueue.size());
                            while (messageBlockingQueue.size() > configuration.getMaxRemotingQueueSize() * (3.0 / 4.0)) {
                                messageBlockingQueue.take();
                            }
                        }
                        Thread.sleep(configuration.getRemotingQueueCheckSleepTime());
                    } catch (InterruptedException e) {
                        throw new RuntimeException("END!", e);
                    }
                }
            }
        });

        for (int i = 0; i < configuration.getParallelRequests(); i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            final LogMessage message = messageBlockingQueue.take();
                            try {
                                final HttpURLConnection conn = connectionFactory.create(remoteUrl);
                                conn.setDoOutput(true);
                                conn.setDoInput(true);
                                conn.setRequestProperty(CONTENT_TYPE_HEADER, JSON_MIME_TYPE);
                                conn.setRequestMethod(POST.getRawName());

                                GelfMessage gelfMessage = new GelfMessage();
                                gelfMessage.setMessage(message.getMessage());
                                gelfMessage.setLoggerName(message.getLoggerName());
                                gelfMessage.setLogLevel(message.getLevel().toString());
                                final Date timestamp = Date.from(message.getTimestamp().toInstant());
                                gelfMessage.setLogTimestamp(timestamp.getTime());
                                gelfMessage.setTimeZone(message.getTimestamp().getZone().getId());
                                gelfMessage.setThreadName(message.getThreadName());
                                gelfMessage.setExceptionClass(message.getExceptionClass());
                                gelfMessage.setExceptionMessage(message.getExceptionMessage());
                                gelfMessage.setMarker(message.getMarker());
                                gelfMessage.setContext(message.getContext());


                                final String content = gson.toJson(gelfMessage);
                                OutputStream w = conn.getOutputStream();
                                w.write(content.getBytes(CHARSET));
                                w.close();

                                //RESPONSE
                                int responseCode = conn.getResponseCode();
                                if (responseCode != HTTP_OK && responseCode != ACCEPTED) {
                                    throw new IOException("Bad Request! status code " + responseCode);
                                }
                            } catch (Exception e) {
                                boolean added = messageBlockingQueue.offer(message);
                                if (!added) {
                                    LOG.error("Log message can not be added to Queue of RemoteLogger! " + messageBlockingQueue.size() + " waiting messages!", e);
                                }
                                if (!remotingError.get()) {
                                    remotingError.set(true);
                                    remotingErrorTime.set(System.currentTimeMillis());
                                    LOG.error("Error in RemoteLogger! " + messageBlockingQueue.size() + " waiting messages!", e);
                                } else if (remotingErrorTime.get() < System.currentTimeMillis() - configuration.getRemotingErrorWaitTime() * 2) {
                                    remotingErrorTime.set(System.currentTimeMillis());
                                    LOG.error("Error in RemoteLogger! " + messageBlockingQueue.size() + " waiting messages!");
                                }
                                Thread.sleep(configuration.getRemotingErrorWaitTime());
                            }
                        } catch (InterruptedException e) {
                            throw new RuntimeException("END!", e);
                        }
                    }
                }
            });
        }

    }

    @Override
    public void log(final LogMessage logMessage) {
        if (LoggerUtils.isLevelEnabled(this.level, logMessage.getLevel())) {
            boolean added = messageBlockingQueue.offer(logMessage);
            if (!added) {
                throw new RuntimeException("Log message can not be added to Queue of RemoteLogger! " + messageBlockingQueue.size() + " waiting messages!");
            }
        }
    }
}
