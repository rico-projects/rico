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
package dev.rico.server.remoting.error;

import dev.rico.server.remoting.RemotingAction;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Exception wrapper that is used as parameter for error handler methods that are defined by
 * the {@link ActionExceptionHandler} annotation.
 *
 * By default all exception handler will be called in a defined ordinal (see {@link ActionExceptionHandler})
 * with the same {@link ActionExceptionEvent} instance once an error occurs in a remote action call
 * (see {@link RemotingAction}). By calling the
 * {@link ActionExceptionEvent#abort()} method in an handler all following handlers won't be called and the
 * action call will not be defined as failing. By doing so an error won't be send to the client.
 *
 * @param <T> type of the exception
 */
@API(since = "1.0.0-RC4", status = EXPERIMENTAL)
public interface ActionExceptionEvent<T extends Throwable> {

    /**
     * Returns the name of action that caused the exception
     * @return name of action
     */
    String getActionName();

    /**
     * Returns the name of the controller that contains the action that caused the exception
     * @return name of the controller
     */
    String getControllerName();

    /**
     * Returns the exception that is wrapped by this instance. The returned exception is the
     * exception instance that was thrown by the action call.
     * @return the exception
     */
    T getException();

    /**
     * By calling this method no following exception handler will be called.
     */
    void abort();
}
