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
package dev.rico.internal.remoting.legacy.core;

import org.apiguardian.api.API;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import static org.apiguardian.api.API.Status.DEPRECATED;

/**
 * The value may be null as long as the BaseAttribute is used as a "placeholder".
 */
@API(since = "0.x", status = DEPRECATED)
@Deprecated
public abstract class BaseAttribute extends AbstractObservable {

    public static final String QUALIFIER_NAME = "qualifier";
    public static final String VALUE_NAME = "value";
    public static final String PROPERTY_NAME = "propertyName";
    public static final String ID = "id";
    private static AtomicLong instanceCount = new AtomicLong();

    private final String propertyName;

    private Object value;

    private BasePresentationModel presentationModel;

    private String id ;

    private String qualifier; // application specific semantics apply

    public BaseAttribute(final String propertyName, final Object value) {
        this(propertyName, value, null);
    }

    public BaseAttribute(final String propertyName, final Object value, final String qualifier) {
        this.id = instanceCount.incrementAndGet() + getOrigin();
        this.propertyName = propertyName;
        this.value = value;
        this.qualifier = qualifier;
    }

    /**
     * @return 'C' for client or 'S' for server
     */
    public abstract String getOrigin();

    public void setPresentationModel(final BasePresentationModel presentationModel) {
        if (this.presentationModel != null) {
            throw new IllegalStateException("You can not set a presentation model for an attribute that is already bound.");
        }
        this.presentationModel = presentationModel;
    }

    public BasePresentationModel getPresentationModel() {
        return this.presentationModel;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(final Object newValue) {
        if (!Objects.equals(value, newValue)) {
            firePropertyChange(VALUE_NAME, value, value = newValue);
        }
    }

    public String toString() {
        return new StringBuilder()
                .append(id)
                .append(" : ")
                .append(propertyName)
                .append(" (")
                .append(qualifier).append(") ")
                .append(value).toString();
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(final String qualifier) {
        firePropertyChange(QUALIFIER_NAME, this.qualifier, this.qualifier = qualifier);
    }
}
