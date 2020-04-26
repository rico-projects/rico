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
package dev.rico.internal.remoting.server.gc;

import dev.rico.remoting.RemotingBean;
import dev.rico.remoting.Property;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * A {@link Reference} that is defined by a {@link Property}.
 * Example for such a reference: Remoting bean A contains a {@link Property} that value is remoting bean B
 * For more information see {@link Reference} and {@link RemotingBean}
 */
@API(since = "0.x", status = INTERNAL)
public class PropertyReference extends Reference {

    private final Property property;

    /**
     * Constructor
     * @param parent the remoting bean that contains the property
     * @param property the property
     * @param child the remoting bean that is the internal value of the {@link Property}
     */
    public PropertyReference(Instance parent, Property property, Instance child) {
        super(parent, child);
        this.property = property;
    }

    /**
     * Returns the property
     * @return the property
     */
    public Property getProperty() {
        return property;
    }
}
