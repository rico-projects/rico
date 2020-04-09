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
package dev.rico.internal.remoting.server.binding;

import dev.rico.core.functional.Binding;
import dev.rico.internal.remoting.BindingException;
import dev.rico.internal.remoting.PropertyImpl;
import dev.rico.remoting.Property;
import dev.rico.internal.core.Assert;
import dev.rico.internal.core.ReflectionHelper;
import dev.rico.remoting.server.binding.PropertyBinder;
import dev.rico.remoting.server.binding.Qualifier;
import dev.rico.internal.remoting.server.legacy.ServerAttribute;
import org.apiguardian.api.API;

import java.lang.reflect.Field;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class PropertyBinderImpl implements PropertyBinder {

    public <T> Binding bind(final Property<T> property, final Qualifier<T> qualifier) {
        Assert.requireNonNull(property, "property");
        Assert.requireNonNull(qualifier, "qualifier");

        if(property instanceof PropertyImpl) {
            try {
                final PropertyImpl p = (PropertyImpl) property;

                final Field attributeField = ReflectionHelper.getInheritedDeclaredField(PropertyImpl.class, "attribute");
                final ServerAttribute attribute = (ServerAttribute) ReflectionHelper.getPrivileged(attributeField, p);
                if(attribute == null) {
                    throw new NullPointerException("attribute == null");
                }
                attribute.setQualifier(qualifier.getIdentifier());
                return new Binding() {
                    @Override
                    public void unbind() {
                        attribute.setQualifier(null);
                    }
                };
            } catch (Exception e) {
                throw new BindingException("Can not bind the given property to the qualifier! Property: " + property + ", qualifier: " + qualifier , e);
            }
        } else {
            throw new BindingException("Can not bind the given property to the qualifier! Property: " + property + ", qualifier: " + qualifier);
        }
    }

}
