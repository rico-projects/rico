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
package dev.rico.internal.remoting.server.event;

import dev.rico.internal.server.bootstrap.PlatformBootstrap;
import dev.rico.remoting.server.event.RemotingEventBus;
import dev.rico.server.spi.ServerCoreComponents;
import org.apiguardian.api.API;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class LazyEventBusInvocationHandler implements InvocationHandler {

    private final static String DUMMY_OBJECT = "";

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final ServerCoreComponents serverCoreComponents = PlatformBootstrap.getServerCoreComponents();
        if(serverCoreComponents != null) {
            final RemotingEventBus instance = serverCoreComponents.getInstance(RemotingEventBus.class);
            if (instance != null) {
                return method.invoke(instance, args);
            }
        }
        if(method.getDeclaringClass().equals(Object.class)) {
            return method.invoke(DUMMY_OBJECT, args);
        }
        if (method.getName().equals("subscribe")) {
            throw new IllegalStateException("Subscription can only be done from remoting Context! Current thread: " + Thread.currentThread().getName());
        } else {
            return null;
        }
    }
}
