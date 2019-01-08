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
package dev.rico.client;

import dev.rico.internal.client.ClientImpl;
import org.apiguardian.api.API;

import java.util.Set;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(since = "0.19.0", status = EXPERIMENTAL)
public interface Client {

    static void init(Toolkit toolkit) {
        ClientImpl.init(toolkit);
    }

    static ClientConfiguration getClientConfiguration() {
        return ClientImpl.getClientConfiguration();
    }

    static <S> boolean hasService(final Class<S> serviceClass) {
        return ClientImpl.hasService(serviceClass);
    }

    static <S> S getService(final Class<S> serviceClass) {
        return ClientImpl.getService(serviceClass);
    }

    static Set<Class<?>> getAllServiceTypes() {
        return ClientImpl.getAllServiceTypes();
    }

}
