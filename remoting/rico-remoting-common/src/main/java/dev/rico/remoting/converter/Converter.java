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

import dev.rico.remoting.Property;
import dev.rico.remoting.RemotingBean;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * A converter interface that convert custom data types to the internally supported data types of the remoting API. The remoting API supports all types that are supported by JSON:
 * - String
 * - Number
 * - Boolean
 *
 * @param <B> type of the custom data
 * @param <D> type of the internal remoting API supported data types that represents the custom data. Converter will be provided by custom implementations of the {@link ConverterFactory} interface.
 * @author Hendrik Ebbers
 * @see Property
 * @see RemotingBean
 * @see ConverterFactory
 */
@API(since = "0.x", status = MAINTAINED)
public interface Converter<B, D> {

    /**
     * Converts the given data in a supported data type to the custom data type of the remoting
     *
     * @param value the data in a supported data type of the remoting
     * @return data in the custom data type
     * @throws ValueConverterException if the data can not be converted
     */
    B convertFromRemoting(D value) throws ValueConverterException;

    /**
     * Converts the given data to a supported data type  of the remoting
     * @param value the data in the custom data type
     * @return the data in a supported data type
     * @throws ValueConverterException if the data can not be converted
     */
    D convertToRemoting(B value) throws ValueConverterException;

}
