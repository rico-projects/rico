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
package dev.rico.internal.remoting.communication.commands.impl;

import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.communication.commands.AbstractCommand;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public final class CreateControllerCommand extends AbstractCommand {

    private String parentControllerId;

    private String controllerName;

    private String controllerId;

    private String modelId;

    public CreateControllerCommand() {
    }

    public CreateControllerCommand(final String uniqueIdentifier) {
        super(uniqueIdentifier);
    }

    public String getParentControllerId() {
        return parentControllerId;
    }

    public void setParentControllerId(final String parentControllerId) {
        this.parentControllerId = parentControllerId;
    }

    public String getControllerName() {
        return controllerName;
    }

    public void setControllerName(final String controllerName) {
        this.controllerName = Assert.requireNonBlank(controllerName, "controllerName");
    }

    public String getControllerId() {
        return controllerId;
    }

    public void setControllerId(final String controllerId) {
        this.controllerId = Assert.requireNonBlank(controllerId, "controllerId");
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(final String modelId) {
        this.modelId = Assert.requireNonBlank(modelId, "modelId");
    }
}
