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
package dev.rico.internal.server.javaee;

import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * This exception will be thrown if a MVC model can't be created or injected in teh controller
 */
@API(since = "0.x", status = INTERNAL)
public class ModelInjectionException extends RuntimeException {

    private static final long serialVersionUID = -4241723730436067531L;

    public ModelInjectionException() {
    }

    public ModelInjectionException(final String message) {
        super(message);
    }

    public ModelInjectionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ModelInjectionException(final Throwable cause) {
        super(cause);
    }
}
