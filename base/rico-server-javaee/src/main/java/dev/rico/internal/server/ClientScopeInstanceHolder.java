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
package dev.rico.internal.server;

import dev.rico.internal.core.Assert;
import org.apiguardian.api.API;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ClientScopeInstanceHolder<T> {

    private final Bean<T> bean;

    private final CreationalContext<T> creationalContext;

    private final T instance;

    public ClientScopeInstanceHolder(final Bean<T> bean, final CreationalContext<T> creationalContext, final T instance) {
        this.bean = Assert.requireNonNull(bean, "bean");
        this.creationalContext = Assert.requireNonNull(creationalContext, "creationalContext");
        this.instance = Assert.requireNonNull(instance, "instance");
    }

    public Bean<T> getBean() {
        return bean;
    }

    public CreationalContext<T> getCreationalContext() {
        return creationalContext;
    }

    public T getInstance() {
        return instance;
    }

    public void destroy() {
        bean.destroy(instance, creationalContext);
    }
}
