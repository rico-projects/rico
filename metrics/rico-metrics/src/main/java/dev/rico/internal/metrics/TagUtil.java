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
package dev.rico.internal.metrics;

import dev.rico.internal.core.Assert;
import dev.rico.core.context.Context;
import io.micrometer.core.instrument.Tag;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class TagUtil {

    private TagUtil() {
    }

    public static List<Tag> convertTags(final Context... contexts) {
        final List<Context> contextsList = Arrays.asList(contexts);
        return convertTags(new HashSet<>(contextsList));
    }

    public static List<Tag> convertTags(final Set<Context> contexts) {
        Assert.requireNonNull(contexts, "contexts");
        return contexts.stream()
                .map(t -> Tag.of(t.getType(), t.getValue()))
                .collect(Collectors.toList());
    }
}
