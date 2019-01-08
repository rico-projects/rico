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
package dev.rico.internal.logging;

import dev.rico.internal.core.Assert;
import org.slf4j.event.Level;

public class LoggerUtils {

    private LoggerUtils() {}

    public static boolean isLevelEnabled(final Level baseLevel, final Level level) {
        Assert.requireNonNull(baseLevel, "baseLevel");
        Assert.requireNonNull(level, "level");
        return baseLevel.toInt() <= level.toInt();
    }

    public static boolean isLevelEnabled(final Level baseLevel, final String level) {
        Assert.requireNonNull(level, "level");
        return isLevelEnabled(baseLevel, Level.valueOf(level));
    }
}
