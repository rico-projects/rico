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
import dev.rico.internal.server.beans.PostConstructInterceptor;
import org.apiguardian.api.API;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * A specific implementation of {@link InstantiationAwareBeanPostProcessorAdapter} that injects
 * the model instance in controllers.
 *
 * @author Hendrik Ebbers
 * @since 0.7
 */
@API(since = "0.7", status = INTERNAL)
public class SpringPreInjector extends InstantiationAwareBeanPostProcessorAdapter {

    private final ThreadLocal<PostConstructInterceptor> currentInterceptor = new ThreadLocal<>();

    private final ThreadLocal<Class> currentControllerClass = new ThreadLocal<>();

    private static final SpringPreInjector instance = new SpringPreInjector();

    public void prepare(final Class controllerClass, final PostConstructInterceptor interceptor) {
        Assert.requireNonNull(controllerClass, "controllerClass");
        Assert.requireNonNull(interceptor, "interceptor");

        currentControllerClass.set(controllerClass);
        currentInterceptor.set(interceptor);
    }

    @Override
    public boolean postProcessAfterInstantiation(final Object bean, final String beanName) throws BeansException {
        Assert.requireNonNull(bean, "bean");
        Class controllerClass = currentControllerClass.get();
        if (controllerClass != null && controllerClass.isAssignableFrom(bean.getClass())) {
            PostConstructInterceptor modelInjector = currentInterceptor.get();
            if (modelInjector != null) {
                modelInjector.intercept(bean);
            }
            currentControllerClass.set(null);
            currentInterceptor.set(null);
        }
        return true;
    }

    public static SpringPreInjector getInstance() {
        return instance;
    }
}
