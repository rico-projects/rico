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
package dev.rico.server.spi.components;

import dev.rico.internal.server.beans.PostConstructInterceptor;
import org.apiguardian.api.API;

import javax.servlet.ServletContext;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(since = "0.x", status = EXPERIMENTAL)
public interface ManagedBeanFactory {

    void init(ServletContext servletContext);

    <T> T createDependentInstance(Class<T> cls);

    <T> T createDependentInstance(Class<T> cls, PostConstructInterceptor<T> interceptor);

    <T> void destroyDependentInstance(T instance, Class<T> cls);
}
