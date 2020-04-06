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
package dev.rico.internal.remoting;

import dev.rico.remoting.ObservableList;
import dev.rico.remoting.RemotingBean;
import dev.rico.remoting.Property;
import dev.rico.internal.core.Assert;
import org.apiguardian.api.API;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * The class {@code RemotingUtils} is a horrible class that we should get rid of asap.
 */
@API(since = "0.x", status = INTERNAL)
public class RemotingUtils {

    private RemotingUtils() {
    }

    public static String getPresentationModelTypeForClass(Class<?> beanClass) {
        return assertIsRemotingBean(beanClass).getName();
    }

    public static <T> T assertIsRemotingBean(T bean) {
        Assert.requireNonNull(bean, "bean");
        assertIsRemotingBean(bean.getClass());
        return bean;
    }

    public static <T> Class<T> assertIsRemotingBean(Class<T> beanClass) {
        if (!isRemotingBean(beanClass)) {
            throw new BeanDefinitionException(beanClass);
        }
        return beanClass;
    }

    public static boolean isRemotingBean(Class<?> beanClass) {
        Assert.requireNonNull(beanClass, "beanClass");
        return beanClass.isAnnotationPresent(RemotingBean.class);
    }


    public static boolean isEnumType(final Class<?> cls) {
        Assert.requireNonNull(cls, "cls");
        return cls.isEnum();
    }

    public static boolean isAllowedForUnmanaged(final Class<?> cls) {
        return isBasicType(cls) || isProperty(cls) || isEnumType(cls);
    }

    public static boolean isProperty(final PropertyDescriptor descriptor) {
        Assert.requireNonNull(descriptor, "descriptor");
        return isProperty(descriptor.getPropertyType());
    }

    public static boolean isProperty(final Class<?> propertyType) {
        return Property.class.isAssignableFrom(propertyType);
    }

    public static boolean isObservableList(final Class<?> propertyType) {
        return ObservableList.class.isAssignableFrom(propertyType);
    }


    public static boolean isBasicType(final Class<?> cls) {
        Assert.requireNonNull(cls, "cls");
        return cls.isPrimitive() || cls.equals(String.class) || cls.equals(Boolean.class) || cls.equals(Byte.class) || Number.class.isAssignableFrom(cls);
    }
}
