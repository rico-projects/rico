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
package dev.rico.internal.server.remoting.event;

import dev.rico.event.MessageEventContext;
import dev.rico.internal.core.Assert;
import org.apiguardian.api.API;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class HttpSessionEventFilter<T extends Serializable> implements Predicate<MessageEventContext<T>> {

    private final List<String> sessionIds;

    public HttpSessionEventFilter(String... sessionIds) {
        this.sessionIds = Arrays.asList(sessionIds);
    }

    @Override
    public boolean test(final MessageEventContext<T> context) {
        Assert.requireNonNull(context, "context");

        final Map<String, Serializable> metadata = context.getMetadata();
        if(metadata == null) {
            return false;
        }

        final Object httpSessionIdValue = metadata.get(EventConstants.HTTP_SESSION_PARAM);
        if(httpSessionIdValue == null || !sessionIds.contains(httpSessionIdValue)) {
            return false;
        }

        return true;
    }
}
