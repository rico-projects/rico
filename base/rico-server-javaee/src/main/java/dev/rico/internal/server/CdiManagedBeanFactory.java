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
package dev.rico.internal.server;

import dev.rico.internal.core.Assert;
import dev.rico.server.spi.components.ManagedBeanFactory;
import dev.rico.internal.server.beans.PostConstructInterceptor;
import org.apache.deltaspike.core.api.provider.BeanManagerProvider;
import org.apache.deltaspike.core.util.bean.BeanBuilder;
import org.apache.deltaspike.core.util.metadata.builder.DelegatingContextualLifecycle;
import org.apiguardian.api.API;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * JavaEE / CDI based implementation of the {@link ManagedBeanFactory}
 *
 * @author Hendrik Ebbers
 */
@API(since = "0.x", status = INTERNAL)
public class CdiManagedBeanFactory implements ManagedBeanFactory {

    private final Map<Object, CreationalContext> contextMap = new HashMap<>();

    private final Map<Object, Bean> beanMap = new HashMap<>();

    @Override
    public void init(final ServletContext servletContext) {}

    @SuppressWarnings("unchecked")
    @Override
    public <T> T createDependentInstance(final Class<T> cls) {
        Assert.requireNonNull(cls, "cls");
        final BeanManager bm = BeanManagerProvider.getInstance().getBeanManager();
        final AnnotatedType annotatedType = bm.createAnnotatedType(cls);
        final InjectionTarget<T> injectionTarget = bm.createInjectionTarget(annotatedType);
        final Bean<T> bean = new BeanBuilder<T>(bm)
                .beanClass(cls)
                .name(UUID.randomUUID().toString())
                .scope(Dependent.class)
                .beanLifecycle(new DelegatingContextualLifecycle<T>(injectionTarget))
                .create();
        final Class<?> beanClass = bean.getBeanClass();
        final CreationalContext<T> creationalContext = bm.createCreationalContext(bean);
        final T instance = (T) bm.getReference(bean, beanClass, creationalContext);
        contextMap.put(instance, creationalContext);
        beanMap.put(instance, bean);
        return instance;
    }

    @Override
    public <T> T createDependentInstance(final Class<T> cls, final PostConstructInterceptor<T> interceptor) {
        Assert.requireNonNull(cls, "cls");
        Assert.requireNonNull(interceptor, "interceptor");
        BeanManager bm = BeanManagerProvider.getInstance().getBeanManager();
        final AnnotatedType annotatedType = bm.createAnnotatedType(cls);
        final InjectionTarget<T> injectionTarget = bm.createInjectionTarget(annotatedType);
        final Bean<T> bean = new BeanBuilder<T>(bm)
                .beanClass(cls)
                .name(UUID.randomUUID().toString())
                .scope(Dependent.class)
                .beanLifecycle(new RicoContextualLifecycle<T>(injectionTarget, interceptor))
                .create();
        final Class<?> beanClass = bean.getBeanClass();
        final CreationalContext<T> creationalContext = bm.createCreationalContext(bean);
        final T instance = (T) bm.getReference(bean, beanClass, creationalContext);
        contextMap.put(instance, creationalContext);
        beanMap.put(instance, bean);
        return instance;
    }

    @Override
    public <T> void destroyDependentInstance(final T instance, final Class<T> cls) {
        Assert.requireNonNull(instance, "instance");
        final Bean bean = beanMap.remove(instance);
        final CreationalContext context = contextMap.remove(instance);
        bean.destroy(instance, context);
    }

}
