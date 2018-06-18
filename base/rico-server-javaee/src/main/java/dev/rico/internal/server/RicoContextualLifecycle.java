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
import dev.rico.internal.server.beans.PostConstructInterceptor;
import org.apache.deltaspike.core.util.metadata.builder.ContextualLifecycle;
import org.apiguardian.api.API;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionTarget;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * Implements a CDI Lifecylce for controllers
 *
 * @param <T> the class of the bean instance
 *
 * @author Hendrik Ebbers
 */
@API(since = "0.x", status = INTERNAL)
public class RicoContextualLifecycle<T> implements ContextualLifecycle<T> {

    private final InjectionTarget<T> injectionTarget;

    private PostConstructInterceptor<T> interceptor;

    public RicoContextualLifecycle(final InjectionTarget<T> injectionTarget, final PostConstructInterceptor<T> interceptor) {
        this.injectionTarget = Assert.requireNonNull(injectionTarget, "injectionTarget");
        this.interceptor = Assert.requireNonNull(interceptor, "interceptor");
    }

    @Override
    public T create(final Bean<T> bean, final CreationalContext<T> creationalContext) {
        Assert.requireNonNull(bean, "bean");
        Assert.requireNonNull(creationalContext, "creationalContext");
        if(interceptor == null) {
            throw new ModelInjectionException("No interceptor defined!");
        }
        try {
            final T instance = injectionTarget.produce(creationalContext);
            interceptor.intercept(instance);
            injectionTarget.inject(instance, creationalContext);
            injectionTarget.postConstruct(instance);
            return instance;
        } finally {
            interceptor = null;
        }
    }

    @Override
    public void destroy(final Bean<T> bean, T instance, final CreationalContext<T> creationalContext) {
        Assert.requireNonNull(bean, "bean");
        Assert.requireNonNull(creationalContext, "creationalContext");
        injectionTarget.preDestroy(instance);
        creationalContext.release();
    }
}
