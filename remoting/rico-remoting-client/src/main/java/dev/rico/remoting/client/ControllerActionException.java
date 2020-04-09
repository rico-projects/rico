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
package dev.rico.remoting.client;

import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * Exception that is thrown when the invocation of a remoting action in the server controller thrown
 * an exception. This exception will be thrown on the client.
 */
@API(since = "0.x", status = MAINTAINED)
public class ControllerActionException extends Exception {

    private static final long serialVersionUID = -7706398106144587910L;

    /**
     * constructor
     * @param message error message
     */
    public ControllerActionException(final String message) {
        super(message);
    }

}
