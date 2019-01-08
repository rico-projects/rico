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
package dev.rico.remoting.converter;

import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * Exception that is thrown if a data conversion ends in an error.
 *
 * @author Hendrik Ebbers
 * @see Converter
 */
@API(since = "0.x", status = MAINTAINED)
public class ValueConverterException extends Exception {

    /**
     * Constructor
     *
     * @param message detailed message
     */
    public ValueConverterException(String message) {
        super(message);
    }

    /**
     * Constructor
     *
     * @param message detailed message
     * @param cause   the cause
     */
    public ValueConverterException(String message, Throwable cause) {
        super(message, cause);
    }

}
