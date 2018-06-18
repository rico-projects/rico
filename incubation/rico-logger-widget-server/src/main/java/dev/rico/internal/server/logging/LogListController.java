/*
 * Copyright 2018 Karakun AG.
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
package dev.rico.internal.server.logging;

import dev.rico.internal.core.Assert;
import dev.rico.internal.logging.LogEntryBean;
import dev.rico.internal.logging.LogListBean;
import dev.rico.internal.logging.LoggerSearchRequest;
import dev.rico.internal.server.logging.service.LoggerRepository;
import dev.rico.internal.logging.spi.LogMessage;
import dev.rico.server.remoting.BeanManager;
import dev.rico.server.remoting.RemotingAction;
import dev.rico.server.remoting.RemotingController;
import dev.rico.server.remoting.RemotingModel;
import org.slf4j.event.Level;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static dev.rico.internal.logging.LoggerRemotingConstants.LOG_LIST_CONTROLLER_NAME;
import static dev.rico.internal.logging.LoggerRemotingConstants.UPDATE_ACTION;

@RemotingController(LOG_LIST_CONTROLLER_NAME)
public class LogListController {

    private final BeanManager beanManager;

    private final LoggerRepository repository;

    @RemotingModel
    private LogListBean model;

    @Inject
    public LogListController(final BeanManager beanManager, final LoggerRepository repository) {
        this.beanManager = Assert.requireNonNull(beanManager, "beanManager");
        this.repository = Assert.requireNonNull(repository, "repository");
    }

    protected final void update(final LoggerSearchRequest request) {
        Assert.requireNonNull(request, "request");

        model.getEntries().clear();
        repository.search(request).
                map(m -> convert(m)).
                forEach(b -> model.getEntries().add(b));
    }

    @RemotingAction(UPDATE_ACTION)
    public void update() {
        final ZonedDateTime startDate = ZonedDateTime.now().minusDays(1);
        final ZonedDateTime endDateTime = ZonedDateTime.now();
        final Set<Level> level = new HashSet<>(Arrays.asList(Level.values()));
        final int maxResults = 100;
        final LoggerSearchRequest request = new LoggerSearchRequest(startDate, endDateTime, level, maxResults);
        update(request);
    }

    private LogEntryBean convert(final LogMessage logMessage) {
        Assert.requireNonNull(logMessage, "logMessage");

        final LogEntryBean bean = beanManager.create(LogEntryBean.class);
        bean.setLoggerName(logMessage.getLoggerName());
        bean.setLogLevel(logMessage.getLevel());
        bean.setMessage(logMessage.getMessage());
        bean.setLogTimestamp(logMessage.getTimestamp());
        bean.setExceptionClass(logMessage.getExceptionClass());
        bean.setExceptionMessage(logMessage.getExceptionMessage());
        bean.setThreadName(logMessage.getThreadName());
        bean.getMarker().addAll(logMessage.getMarker());

        return bean;
    }
}
