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
package dev.rico.internal.server.remoting.test;

import java.util.Map;

import dev.rico.internal.core.Assert;
import dev.rico.client.remoting.ControllerProxy;
import dev.rico.client.remoting.Param;
import dev.rico.server.remoting.test.CommunicationMonitor;
import dev.rico.server.remoting.test.ControllerTestException;
import dev.rico.server.remoting.test.ControllerUnderTest;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ClientTestFactory {

    public static <T> ControllerUnderTest<T> createController(final TestClientContext clientContext, final String controllerName) {
        Assert.requireNonNull(clientContext, "clientContext");
        Assert.requireNonBlank(controllerName, "controllerName");
        try {
            final ControllerProxy<T> proxy = (ControllerProxy<T>) clientContext.createController(controllerName).get();
            return new ControllerUnderTestWrapper<>(clientContext, proxy);
        } catch (Exception e) {
            throw new ControllerTestException("Can't create controller proxy", e);
        }
    }

    static class ControllerUnderTestWrapper<T> implements ControllerUnderTest<T> {

        private final ControllerProxy<T> proxy;
        private final TestClientContext clientContext;

        public ControllerUnderTestWrapper(final TestClientContext clientContext, final ControllerProxy<T> controllerProxy) {
            this.clientContext = clientContext;
            this.proxy = controllerProxy;
        }

        @Override
        public T getModel() {
            return proxy.getModel();
        }

        @Override
        public void invoke(String actionName, Param... params) {
            try {
                proxy.invoke(actionName, params).get();
            } catch (Exception e) {
                throw new ControllerTestException("Error in action invocation", e);
            }
        }

        @Override
        public void invoke(String actionName, Map<String, Object> params) {
            try {
                proxy.invoke(actionName, params).get();
            } catch (Exception e) {
                throw new ControllerTestException("Error in action invocation", e);
            }
        }

        @Override
        public CommunicationMonitor createMonitor() {
            return new CommunicationMonitorImpl(clientContext);
        }

        @Override
        public void destroy() {
            try {
                proxy.destroy().get();
            } catch (Exception e) {
                throw new ControllerTestException("Error in destroy", e);
            }
        }

        @Override
        public <S> ControllerUnderTest<S> createController(String childControllerName) {
            try {
                ControllerProxy<T> controllerProxy = (ControllerProxy<T>) proxy.createController(childControllerName).get();
                return new ControllerUnderTestWrapper(clientContext, controllerProxy);
            } catch (Exception e) {
                throw new ControllerTestException("Error in sub controller", e);
            }
        }
    }
}

