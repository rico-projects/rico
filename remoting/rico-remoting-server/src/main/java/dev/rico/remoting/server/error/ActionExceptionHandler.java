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
package dev.rico.remoting.server.error;

import dev.rico.remoting.server.RemotingAction;
import dev.rico.remoting.server.RemotingController;
import org.apiguardian.api.API;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * This annotation can be used to mark methods as exception handler for an exception that is thrown by
 * a remote action call (see {@link RemotingAction}). A method that is
 * annotated with this annotation must be a non static method with no return type
 * (<code>void</code> method) and must it define exactly one parameter of the type
 * {@link ActionExceptionEvent}.
 *
 * A method in a remote controller (see {@link RemotingController})
 * can be used used as an exception handler. Such a handler will handle all exception that are thrown by
 * a action method of the given controller class (or any sub class). A controller class can define
 * multiple methods that are used as exception handlers.
 *
 * Since multiple exception handlers can be defined an exception that is thrown by a remote action call
 * can be handled by several exception handlers. All handlers will be called in a defined ordinal:
 * <ul>
 *     <li>The handlers that are defined in the controller class (or a super class) that throws the exception will be called first.</li>
 *     <li>All handlers for the controller will be sorted by the
 *     {@link ActionExceptionHandler#ordinal()} value. The highest value will be called first.</li>
 *     <li>Only handlers of that controller that define the thrown exception type or a super type of that exception by the generic type of the {@link ActionExceptionEvent} parameter will be called.</li>
 * </ul>
 *
 */
@Documented
@Retention(RUNTIME)
@Target(ElementType.METHOD)
@API(since = "1.0.0-RC4", status = EXPERIMENTAL)
public @interface ActionExceptionHandler {

    int ordinal() default 1;
}
