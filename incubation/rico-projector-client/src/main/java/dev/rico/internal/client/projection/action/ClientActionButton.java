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
package dev.rico.internal.client.projection.action;

import dev.rico.internal.projection.action.ClientAction;
import dev.rico.internal.client.projection.base.ClientActionSupport;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ClientActionButton<T> extends AbstractActionButton<ClientAction<T>> {

    private final ClientActionSupport clientActionSupport;

    public ClientActionButton(ClientAction<T> action, ClientActionSupport clientActionSupport) {
        super(action);
        this.clientActionSupport = clientActionSupport;
    }

    @Override
    protected CompletableFuture<Void> callAction() {
        Supplier<CompletableFuture<T>> actionSupplier = clientActionSupport.getActionSupplier(getAction().getActionName());
        if(actionSupplier == null) {
            throw new NullPointerException("No action found!");
        }
        return actionSupplier.get().whenComplete((t, e) -> {
            if(e != null) {
                //TODO EXCEPTION
            }
            getAction().setResult(t);
        }).thenApply(t -> null);
    }
}

