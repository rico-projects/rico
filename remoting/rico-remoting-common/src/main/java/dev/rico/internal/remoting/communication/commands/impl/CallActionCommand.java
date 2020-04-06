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
package dev.rico.internal.remoting.communication.commands.impl;

import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.communication.commands.AbstractCommand;
import org.apiguardian.api.API;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public final class CallActionCommand extends AbstractCommand {

    private String controllerId;

    private String actionName;

    private final Map<String, Object> params = new HashMap<>();

    public CallActionCommand() {
    }

    public CallActionCommand(final String uniqueIdentifier) {
        super(uniqueIdentifier);
    }

    public String getControllerId() {
        return controllerId;
    }

    public void setControllerId(final String controllerId) {
        Assert.requireNonBlank(controllerId, "controllerId");
        this.controllerId = controllerId;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(final String actionName) {
        this.actionName = Assert.requireNonBlank(actionName, "actionName");
    }

    public Map<String, Object> getParams() {
        return Collections.unmodifiableMap(params);
    }

    public void addParam(final String name, final Object value) {
        Assert.requireNonBlank(name, "name");
        params.put(name, value);
    }
}

