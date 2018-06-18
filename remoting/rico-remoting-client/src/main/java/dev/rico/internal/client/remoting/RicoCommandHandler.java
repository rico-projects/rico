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
package dev.rico.internal.client.remoting;

import dev.rico.internal.core.Assert;
import dev.rico.internal.client.remoting.legacy.communication.OnFinishedHandler;
import dev.rico.internal.remoting.legacy.communication.Command;
import org.apiguardian.api.API;

import java.util.concurrent.CompletableFuture;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class RicoCommandHandler {

    private final AbstractClientConnector clientConnector;

    public RicoCommandHandler(final AbstractClientConnector clientConnector) {
        this.clientConnector = Assert.requireNonNull(clientConnector, "clientConnector");
    }

    public CompletableFuture<Void> invokeCommand(final Command command) {
        Assert.requireNonNull(command, "command");
        final CompletableFuture<Void> result = new CompletableFuture<>();
        clientConnector.send(command, new OnFinishedHandler() {
            @Override
            public void onFinished() {
                result.complete(null);
            }
        });
        return result;
    }
}
