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
package dev.rico.internal.server.logging.service;

import dev.rico.internal.core.Assert;
import dev.rico.internal.logging.RicoLoggerFactory;
import dev.rico.internal.logging.spi.LogMessage;
import dev.rico.internal.logging.LoggerSearchRequest;

import java.util.Comparator;
import java.util.stream.Stream;

public class LocalLoggerRepository implements LoggerRepository {

    @Override
    public Stream<LogMessage> search(final LoggerSearchRequest searchRequest) {
        Assert.requireNonNull(searchRequest, "searchRequest");
        return RicoLoggerFactory.getLogCache().stream().
                filter(l -> l.getTimestamp().isAfter(searchRequest.getStartDate())).
                filter(l -> l.getTimestamp().isBefore(searchRequest.getEndDateTime())).
                filter(l -> searchRequest.getLevel().contains(l.getLevel())).
                sorted(Comparator.comparing(LogMessage::getTimestamp).reversed()).
                limit(searchRequest.getMaxResults());
    }
}
