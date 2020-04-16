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
package dev.rico.internal.remoting.server.error;

import dev.rico.internal.core.Assert;
import dev.rico.remoting.server.error.ActionExceptionEvent;

public class ActionExceptionEventImpl<T extends Throwable> implements ActionExceptionEvent<T> {

    private final T throwable;

    private final String actionName;

    private final String controllerName;

    private boolean aborted = false;

    public ActionExceptionEventImpl(final String actionName, final String controllerName, final T throwable) {
        this.actionName = Assert.requireNonBlank(actionName, "actionName");
        this.controllerName = Assert.requireNonBlank(controllerName, "controllerName");
        this.throwable = Assert.requireNonNull(throwable, "throwable");
    }

    @Override
    public String getActionName() {
        return actionName;
    }

    @Override
    public String getControllerName() {
        return null;
    }

    @Override
    public T getException() {
        return throwable;
    }

    @Override
    public void abort() {
        this.aborted = true;
    }

    public boolean isAborted() {
        return aborted;
    }
}
