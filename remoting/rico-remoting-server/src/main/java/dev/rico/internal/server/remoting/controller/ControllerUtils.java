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
package dev.rico.internal.server.remoting.controller;

import dev.rico.internal.core.Assert;
import dev.rico.server.remoting.RemotingAction;
import dev.rico.server.remoting.RemotingController;

import java.lang.reflect.Method;

public class ControllerUtils {


    public static String getActionMethodName(final Method method) {
        Assert.requireNonNull(method, "method");
        if (method.isAnnotationPresent(RemotingAction.class)) {
            RemotingAction actionAnnotation = method.getAnnotation(RemotingAction.class);
            String currentActionName = method.getName();
            if (actionAnnotation.value() != null && !actionAnnotation.value().trim().isEmpty()) {
                currentActionName = actionAnnotation.value();
            }

            return currentActionName;

        } else {
            throw new IllegalArgumentException("Method " + method.getName() + " is not annotated with " + RemotingAction.class);
        }
    }

    public static String getControllerName(final Class<?> clazz) {
        Assert.requireNonNull(clazz, "clazz");
        if (clazz.isAnnotationPresent(RemotingController.class)) {
            RemotingController controllerAnnotation = clazz.getAnnotation(RemotingController.class);
            String currentControllerName = clazz.getName();
            if (controllerAnnotation.value() != null && !controllerAnnotation.value().trim().isEmpty()) {
                currentControllerName = controllerAnnotation.value();
            }

            return currentControllerName;

        } else {
            throw new IllegalArgumentException("Class " + clazz.getName() + " is not annotated with " + RemotingController.class);
        }
    }
}
