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
package dev.rico.core.context;

import dev.rico.core.functional.Assignment;
import org.apiguardian.api.API;

import java.util.Map;
import java.util.Optional;

import static org.apiguardian.api.API.Status.STABLE;

/**
 * Holder of attributes.
 * An attribute can either be global or thread local.
 */
@API(since = "2.0", status = STABLE)
public interface RicoApplicationContext {

    /**
     * Sets a global attribute.
     * <p>
     * Both name and value must not be {@code null}.
     *
     * @param name  the name of the attribute
     * @param value the value of the attribute
     * @return a callable which will remove the attribute when executed.
     */
    Assignment setGlobalAttribute(String name, String value);

    /**
     * Sets a thread local attribute.
     * <p>
     * Both name and value must not be {@code null}.
     *
     * @param name  the name of the attribute
     * @param value the value of the attribute
     * @return a callable which will remove the attribute when executed.
     */
    Assignment setThreadLocalAttribute(String name, String value);

    /**
     * Gets a single attribute.
     * If the attribute is defined in both the global and the thread local context,
     * then the thread local context takes precedence.
     * <p>
     * If the attribute is not present {@link Optional#empty()} is returned.
     *
     * @param name the name of the attribute
     * @return the value of the attribute or {@link Optional#empty()}
     */
    Optional<String> getAttribute(String name);

    /**
     * @return an unmodifiable copy of the current global context.
     */
    Map<String, String> getGlobalAttributes();

    /**
     * @return an unmodifiable copy of the current thread local context.
     */
    Map<String, String> getThreadLocalAttributes();

    /**
     * Gets a unmodifiable copy of the union of global and thread local context.
     * If a attribute is defined in both the global and the thread local context,
     * then the thread local context takes precedence.
     *
     * @return a unmodifiable map of global and thread local context
     */
    Map<String, String> getAttributes();
}
