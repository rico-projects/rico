/*
 * Copyright 2018-2019 Karakun AG.
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
package dev.rico.internal.core.lang;

import dev.rico.core.lang.StringPair;

/**
 * Default implementation of {@link StringPair}
 */
public class DefaultStringPair extends DefaultTuple<String, String> implements StringPair {
    public DefaultStringPair(String key, String value) {
        super(key, value);
    }
}
