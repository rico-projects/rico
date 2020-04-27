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
package dev.rico.internal.remoting.server.error;

import dev.rico.internal.core.Assert;
import dev.rico.internal.core.ReflectionHelper;
import dev.rico.internal.core.lang.TypeUtils;
import dev.rico.remoting.server.error.ActionExceptionEvent;
import dev.rico.remoting.server.error.ActionExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static dev.rico.internal.core.ReflectionHelper.isParameterizedType;

public class ActionErrorHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ActionErrorHandler.class);

    public <T extends Throwable> boolean handle(final T throwable, final Object controller, final String controllerName, final String actionName) {
        Assert.requireNonNull(throwable, "throwable");
        Assert.requireNonNull(controller, "controller");
        Assert.requireNonBlank(controllerName, "controllerName");
        Assert.requireNonBlank(actionName, "actionName");

        final ActionExceptionEventImpl exceptionEvent = new ActionExceptionEventImpl(actionName, controllerName, throwable);
        final List<Consumer<ActionExceptionEvent<T>>> consumers = new ArrayList<>();
        consumers.addAll(getConsumersForTypeInController(throwable.getClass(), controller));
        LOG.debug("Found {} handlers to handle action exception of type {}", consumers.size(), throwable.getClass());

        final Iterator<Consumer<ActionExceptionEvent<T>>> iterator = consumers.iterator();
        while (!exceptionEvent.isAborted() && iterator.hasNext()) {
            final Consumer<ActionExceptionEvent<T>> handler = iterator.next();
            try {
                handler.accept(exceptionEvent);
            } catch (final Exception e) {
                LOG.error("Error in calling exception handler for error of type '" + throwable.getClass() +
                        "' thrown by action '" + exceptionEvent.getActionName() +
                        "' of controller '" + exceptionEvent.getControllerName() + "'!", e);
            }
        }
        return exceptionEvent.isAborted();
    }

    private <T extends Throwable> List<Consumer<ActionExceptionEvent<T>>> getConsumersForTypeInController(final Class<? extends Throwable> throwableClass, final Object controller) {
        Assert.requireNonNull(throwableClass, "throwableClass");
        Assert.requireNonNull(controller, "controller");
        return ReflectionHelper.getInheritedDeclaredMethods(controller.getClass()).stream()
                .filter(m -> m.isAnnotationPresent(ActionExceptionHandler.class))
                .filter(m -> m.getParameterCount() == 1)
                .filter(m -> m.getGenericParameterTypes().length == 1)
                .filter(m -> ActionExceptionEvent.class.equals(m.getParameterTypes()[0]))
                .filter(m -> filterGenericExceptionType(m.getGenericParameterTypes()[0], throwableClass))
                .sorted((m1, m2) -> {
                    final int ordinal1 = m1.getAnnotation(ActionExceptionHandler.class).ordinal();
                    final int ordinal2 = m2.getAnnotation(ActionExceptionHandler.class).ordinal();
                    return Integer.compare(ordinal1, ordinal2);
                })
                .map(m -> this.<T>createConsumer(m, controller))
                .collect(Collectors.toList());
    }

    private boolean filterGenericExceptionType(final Type genericType, final Class<? extends Throwable> throwableClass) {
        Assert.requireNonNull(genericType, "genericType");
        Assert.requireNonNull(throwableClass, "throwableClass");
        if(isParameterizedType(genericType)) {
            final ParameterizedType parameterizedType = ReflectionHelper.toParameterizedType(genericType);
            if(ReflectionHelper.hasGenericTypeCount(parameterizedType, 1)) {
                final Type genericTypeDef = ReflectionHelper.getGenericType(parameterizedType, 0);
                return TypeUtils.isAssignable(throwableClass, genericTypeDef);
            }

        }
        return false;
    }

    private <T extends Throwable> Consumer<ActionExceptionEvent<T>> createConsumer(final Method method, final Object instance) {
        Assert.requireNonNull(method, "method");
        Assert.requireNonNull(instance, "instance");
        return throwable -> ReflectionHelper.invokePrivileged(method, instance, throwable);
    }
}
