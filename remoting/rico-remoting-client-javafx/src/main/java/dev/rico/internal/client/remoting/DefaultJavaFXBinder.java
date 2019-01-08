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
package dev.rico.internal.client.remoting;

import dev.rico.core.functional.Binding;
import dev.rico.client.remoting.Converter;
import dev.rico.client.remoting.binding.JavaFXBinder;
import dev.rico.core.functional.Subscription;
import dev.rico.remoting.Property;
import dev.rico.internal.core.Assert;
import javafx.beans.value.WritableValue;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class DefaultJavaFXBinder<S> implements JavaFXBinder<S> {

    private final WritableValue<S> javaFxValue;

    public DefaultJavaFXBinder(final WritableValue<S> javaFxValue) {
        this.javaFxValue = Assert.requireNonNull(javaFxValue, "javaFxValue");
    }

    @Override
    public <T> Binding to(final Property<T> remotingProperty, final Converter<? super T, ? extends S> converter) {
        Assert.requireNonNull(remotingProperty, "remotingProperty");
        Assert.requireNonNull(converter, "converter");
        final Subscription subscription = remotingProperty.onChanged(event -> javaFxValue.setValue(converter.convert(remotingProperty.get())));
        javaFxValue.setValue(converter.convert(remotingProperty.get()));
        return () -> subscription.unsubscribe();
    }
}
