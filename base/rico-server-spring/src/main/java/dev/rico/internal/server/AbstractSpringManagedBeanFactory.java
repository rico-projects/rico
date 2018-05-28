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
import dev.rico.server.spi.components.ManagedBeanFactory;
import dev.rico.internal.server.beans.PostConstructInterceptor;
import org.apiguardian.api.API;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletContext;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public abstract class AbstractSpringManagedBeanFactory implements ManagedBeanFactory {

    @Override
    public void init(ServletContext servletContext) {
        init();
    }

    protected void init() {
        ApplicationContext context = getContext();
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();
        beanFactory.addBeanPostProcessor(SpringPreInjector.getInstance());
    }

    @Override
    public <T> T createDependentInstance(Class<T> cls) {
        Assert.requireNonNull(cls, "cls");
        ApplicationContext context = getContext();
        AutowireCapableBeanFactory beanFactory = context.getAutowireCapableBeanFactory();
        return beanFactory.createBean(cls);
    }

    @Override
    public <T> T createDependentInstance(Class<T> cls, PostConstructInterceptor<T> interceptor) {
        Assert.requireNonNull(cls, "cls");
        Assert.requireNonNull(interceptor, "interceptor");
        ApplicationContext context = getContext();
        AutowireCapableBeanFactory beanFactory = context.getAutowireCapableBeanFactory();
        SpringPreInjector.getInstance().prepare(cls, interceptor);
        return beanFactory.createBean(cls);
    }

    @Override
    public <T> void destroyDependentInstance(T instance, Class<T> cls) {
        Assert.requireNonNull(instance, "instance");
        Assert.requireNonNull(cls, "cls");
        ApplicationContext context = getContext();
        context.getAutowireCapableBeanFactory().destroyBean(instance);
    }

    /**
     * Returns the Spring {@link org.springframework.context.ApplicationContext} for the current {@link javax.servlet.ServletContext}
     *
     * @return the spring context
     */
    protected abstract ApplicationContext getContext();
}
