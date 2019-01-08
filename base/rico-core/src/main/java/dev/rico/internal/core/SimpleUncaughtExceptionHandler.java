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
package dev.rico.internal.core;

import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class SimpleUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleUncaughtExceptionHandler.class);

    @Override
    public void uncaughtException(final Thread thread, final Throwable exception) {
        Assert.requireNonNull(thread, "thread");
        Assert.requireNonNull(exception, "exception");
        LOG.error("Unhandled error in background thread " + thread.getName(), exception);
    }
}
