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
package dev.rico.server.remoting.binding;

import dev.rico.internal.core.Assert;
import dev.rico.remoting.Property;
import org.apiguardian.api.API;

import java.util.UUID;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * A qualifier to define a server site binding of properties (see {@link Property}).
 *
 * @param <T> generic type of the property that can be bound by using the qualifier
 * @see PropertyBinder
 *
 * @author Hendrik Ebbers
 */
@API(since = "0.x", status = EXPERIMENTAL)
public final class Qualifier<T> {

    private final String identifier;

    /**
     * Constructor
     * @param identifier the unique identifier for this qualifier.
     */
    public Qualifier(final String identifier) {
        this.identifier = Assert.requireNonNull(identifier, "identifier");
    }

    /**
     * Returns the unique identifier for this qualifier
     * @return the unique identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Factory method that creates a qualifier with a generated unique identifier.
     * @param <T> generic type of the property that can be bound by using the created qualifier
     * @return the qualifier
     */
    public static <T> Qualifier<T> create() {
        return new Qualifier<>(UUID.randomUUID().toString());
    }

    /**
     * Factory method that creates a qualifier with the given unique identifier.
     * @param identifier the unique identifier
     * @param <T>  generic type of the property that can be bound by using the created qualifier
     * @return the qualifier
     */
    public static <T> Qualifier<T> create(String identifier) {
        return new Qualifier<>(identifier);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Qualifier<?> qualifier = (Qualifier<?>) o;

        return identifier != null ? identifier.equals(qualifier.identifier) : qualifier.identifier == null;

    }

    @Override
    public int hashCode() {
        return identifier != null ? identifier.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Qualifier: " + identifier;
    }
}
