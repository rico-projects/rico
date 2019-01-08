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
package dev.rico.internal.server.remoting.legacy;

import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * A factory class to create a ServerDolphin object.
 */
@API(since = "0.x", status = INTERNAL)
public class ServerDolphinFactory {

    private ServerDolphinFactory() {}

    /**
     * Creates a default ServerDolphin object containing a default ServerModelStore and ServerConnector.
     */
    public static ServerDolphin create() {
        return new DefaultServerDolphin();
    }

    /**
     * Creates a default ServerDolphin object using the supplied model store and server connector.
     * @param serverModelStore
     * @param serverConnector
     * @return
     */
    public static ServerDolphin create(final ServerModelStore serverModelStore, final ServerConnector serverConnector) {
        return new DefaultServerDolphin(serverModelStore, serverConnector);
    }
}
