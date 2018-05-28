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
package dev.rico.internal.core.context;

import dev.rico.internal.core.Assert;
import dev.rico.core.context.Context;

import java.util.Objects;

public class ContextImpl implements Context {

    private final String key;

    private final String value;

    public ContextImpl(final String key, final String value) {
        this.key = Assert.requireNonBlank(key, "key");
        this.value = value;
    }

    @Override
    public String getType() {
        return key;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ContextImpl context = (ContextImpl) o;
        return Objects.equals(key, context.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
