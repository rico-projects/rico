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
package dev.rico.internal.remoting.converters;

import java.text.DateFormat;

public abstract class AbstractDateConverter<T> extends AbstractStringConverter<T> {

    private final static ThreadLocal<DateFormat> dateFormat = new ThreadLocal<>();

    protected DateFormat getDateFormat() {
        final DateFormat format = dateFormat.get();
        if(format != null) {
            return format;
        }
        final DateFormat newFormat = RemotingDateFormatUtils.createDefaultDateFormat();
        dateFormat.set(newFormat);
        return newFormat;
    }

}
