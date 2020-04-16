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
package dev.rico.internal.client.projection.action;

import dev.rico.internal.projection.action.ServerAction;
import dev.rico.remoting.client.ControllerProxy;

import java.util.concurrent.CompletableFuture;

public class ServerActionButton extends AbstractActionButton<ServerAction> {

    private final ControllerProxy controllerProxy;

    public ServerActionButton(ControllerProxy controllerProxy, ServerAction action) {
        super(action);
        this.controllerProxy = controllerProxy;
    }

    @Override
    protected CompletableFuture<Void> callAction() {
        return controllerProxy.invoke(getAction().getActionName());
    }
}
