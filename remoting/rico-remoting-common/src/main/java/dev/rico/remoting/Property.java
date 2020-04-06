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
package dev.rico.remoting;

import dev.rico.core.functional.Subscription;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * Defines a property that can be part of a model (see {@link RemotingBean}). Since Java has no native property system this is needed to provide
 * listener / observer support to properties.
 * <p>
 * The public API of the remoting layer don't contain an implementation of this interface since the lifecycle of all
 * models must be managed by the {@link BeanManager}. By using the {@link Property} interface a small
 * remoting model will look like this:
 * <p>
 * <blockquote>
 * <pre>
 *     {@literal @}RemotingBean
 *     public class MyModel {
 *
 *
 *         {@code private Property<String> value;}
 *
 *         {@code public Property<String> valueProperty() {
 *              return value;
 *          }
 *         }
 *     }
 * </pre>
 * </blockquote>
 * <p>
 * The value can be easily accessed and modified by calling the {@link #set(Object)} and {@link #get()} method of the
 * {@link Property} but often the model classes provide some convenience methods like shown in the following example:
 * <blockquote>
 * <pre>
 *     {@literal @}RemotingBean
 *     public class MyModel {
 *
 *
 *         {@code private Property<String> value;}
 *
 *         {@code public Property<String> valueProperty() {
 *              return value;
 *          }
 *         }
 *
 *         public String getValue() {
 *              return value.get();
 *         }
 *
 *         public void setValue(String value) {
 *              this.value.set(value);
 *         }
 *     }
 * </pre>
 * </blockquote>
 * <p>
 * Currently remoting models support only the {@link Property} and {@link ObservableList}
 * interfaces to define attributes and collections in models. But by just using this 2 interfaces it's easy to createList
 * hierarchical models because a {@link Property} can contain another bean, for example. The following class shows this
 * design by a simple example:
 * <p>
 * <blockquote>
 * <pre>
 *     {@literal @}RemotingBean
 *     public class MainModel {
 *
 *
 *         {@code private Property<MyModel> innerModel;}
 *
 *         {@code public Property<MyModel> innerModelProperty() {
 *              return innerModel;
 *          }
 *         }
 *     }
 * </pre>
 * </blockquote>
 * <p>
 * For more information see {@link RemotingBean}
 *
 * @param <T> Type of the property must be a scalar, not a collection
 * @author Hendrik Ebbers
 */
@API(since = "0.x", status = MAINTAINED)
public interface Property<T> {

    /**
     * Sets the value of the property
     *
     * @param value the new value
     */
    void set(T value);

    /**
     * Returns the value of the property
     *
     * @return the current value
     */
    T get();

    /**
     * Adds a change listener to the property that will be called whenever the value of the property changes
     *
     * @param listener the change listener
     */
    Subscription onChanged(ValueChangeListener<? super T> listener);
}
