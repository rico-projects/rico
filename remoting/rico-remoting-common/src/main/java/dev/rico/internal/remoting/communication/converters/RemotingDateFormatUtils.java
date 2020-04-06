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
package dev.rico.internal.remoting.communication.converters;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static dev.rico.internal.core.RicoConstants.TIMEZONE_UTC;
import static dev.rico.internal.remoting.RemotingConstants.REMOTING_DATE_FORMAT_PATTERN;

public class RemotingDateFormatUtils {

    public static DateFormat createDefaultDateFormat() {
        final DateFormat newFormat = new SimpleDateFormat(REMOTING_DATE_FORMAT_PATTERN);
        newFormat.setTimeZone(TimeZone.getTimeZone(TIMEZONE_UTC));
        return newFormat;
    }
}
