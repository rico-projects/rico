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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.apiguardian.api.API.Status.DEPRECATED;

/**
 * A BasePresentationModel is a collection of {@link BaseAttribute}s.
 * PresentationModels are not meant to be extended for the normal use, i.e. you typically don't need something like
 * a specialized "PersonPresentationModel" or so.
 */
@API(since = "0.x", status = DEPRECATED)
@Deprecated
public abstract class BasePresentationModel<A extends BaseAttribute> extends AbstractObservable {

    protected final List<A> attributes = new LinkedList<A>();

    private final String id;

    private String presentationModelType;


    /**
     * @throws AssertionError if the list of attributes is null or empty
     */
    public BasePresentationModel(final String id, final List<A> attributes) {
        this.id = id;
        for (final A attr : attributes) {
            _internal_addAttribute(attr);
        }
    }

    public void _internal_addAttribute(final A attribute) {
        if (null == attribute || attributes.contains(attribute)) return;
        if (null != getAttribute(attribute.getPropertyName())) {
            throw new IllegalStateException("There already is an attribute with property name '"
                                            + attribute.getPropertyName()
                                            + "' in presentation model with id '" + this.id + "'.");
        }
        if (attribute.getQualifier() != null && this.findAttributeByQualifier(attribute.getQualifier()) != null) {
            throw  new IllegalStateException("There already is an attribute with qualifier '" + attribute.getQualifier()
                    + "' in presentation model with id '" + this.id + "'.");
        }
        ((BaseAttribute)attribute).setPresentationModel(this);
        attributes.add(attribute);
    }

    public String getId() {
        return id;
    }

    public String getPresentationModelType() {
        return presentationModelType;
    }

    public void setPresentationModelType(String presentationModelType) {
        this.presentationModelType = presentationModelType;
    }

    /**
     * @return the immutable internal representation
     */
    public List<A> getAttributes() {
        return Collections.unmodifiableList(attributes);
    }

    public A getAttribute(final String propertyName) {
        if (null == propertyName) return null;
        for (final A attribute : attributes) {
            if (propertyName.equals(attribute.getPropertyName())) {
                return attribute;
            }
        }
        return null;
    }

    public A findAttributeByQualifier(final String qualifier) {
        if (null == qualifier) return null;
        for (final A attribute : attributes) {
            if (qualifier.equals(attribute.getQualifier())) {
                return attribute;
            }
        }
        return null;
    }

    public A findAttributeById(final String id) {
        for (final A attribute : attributes) {
            if (attribute.getId().equals(id)) {
                return attribute;
            }
        }
        return null;
    }
}