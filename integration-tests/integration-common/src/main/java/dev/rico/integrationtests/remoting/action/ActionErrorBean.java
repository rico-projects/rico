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
package dev.rico.integrationtests.remoting.action;

import dev.rico.remoting.Property;
import dev.rico.remoting.RemotingBean;

@RemotingBean
public class ActionErrorBean {

    private Property<String> actionName;

    private Property<String> controllerName;

    private Property<String> exceptionName;

    public String getActionName() {
        return actionName.get();
    }

    public Property<String> actionNameProperty() {
        return actionName;
    }

    public void setActionName(final String actionName) {
        this.actionName.set(actionName);
    }

    public String getControllerName() {
        return controllerName.get();
    }

    public Property<String> controllerNameProperty() {
        return controllerName;
    }

    public void setControllerName(final String controllerName) {
        this.controllerName.set(controllerName);
    }

    public String getExceptionName() {
        return exceptionName.get();
    }

    public Property<String> exceptionNameProperty() {
        return exceptionName;
    }

    public void setExceptionName(final String exceptionName) {
        this.exceptionName.set(exceptionName);
    }
}
