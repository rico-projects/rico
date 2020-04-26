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
package dev.rico.internal.remoting.server.context;

import dev.rico.internal.core.Assert;
import dev.rico.remoting.server.ClientSessionExecutor;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ClientSessionExecutorImpl implements ClientSessionExecutor {

    private final static Logger LOG = LoggerFactory.getLogger(ClientSessionExecutorImpl.class);

    private final Executor runLaterExecutor;

    public ClientSessionExecutorImpl(final Executor runLaterExecutor) {
        this.runLaterExecutor = Assert.requireNonNull(runLaterExecutor, "runLaterExecutor");
    }

    @Override
    public CompletableFuture<Void> runLaterInClientSession(final Runnable task) {
        Assert.requireNonNull(task, "task");
        return callLaterInClientSession(new Callable<>() {
            @Override
            public Void call() throws Exception {
                task.run();
                return null;
            }
        });
    }

    @Override
    public <T> CompletableFuture<T> callLaterInClientSession(final Callable<T> task) {
        Assert.requireNonNull(task, "task");
        final CompletableFuture<T> future = new CompletableFuture();
        runLaterExecutor.execute(() -> {
            try {
                final T result = task.call();
                future.complete(result);
            } catch (final Exception e) {
                LOG.error("Unchaught exception in task!", e);
                future.completeExceptionally(e);
            }
        });
        return future;
    }
}
