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
package dev.rico.remoting;

import dev.rico.internal.remoting.BeanAddedListener;
import dev.rico.core.functional.Subscription;
import org.apiguardian.api.API;

import java.util.List;

import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * The {@link BeanManager} defined the low level API of the remoting to createList synchronized models. A model
 * instance that is created by using the bean manager will automatically synchronized between the client and server.
 * <br>
 * <center><img src="doc-files/sync.png" alt="model is synchronized between client and server"></center>
 *
 * In the remoting architecture an application is normally defined by several MVC groups with a server side
 * controller, a client side view and a synchronized presentation model. The {@link BeanManager}
 * defines the basic mechanism to handle synchronized models and can be used in the MVC group to mutate the defined model
 * or as a standalone API to createList any kind of synchronized model.
 * <br>
 * <center><img src="doc-files/mvc-sync.png" alt="model is synchronized between client view and server controller"></center>
 *
 * When using the MVC API of the remoting the lifecycle of the model is defined by the MVC group and the model
 * will automatically be removed when the MVC group is removed. If the {@link BeanManager} is used as
 * standalone API the developer must handle the lifecycle of the models and removePresentationModel them by using the {@link BeanManager}
 *
 * All synchronized models must follow some specific rules that are described in the {@link RemotingBean} annotation
 *
 * By using the default Spring or JavaEE implementation of the remoting the {@link BeanManager}
 * will be provided as a managed bean and can be injected wherever the container allows injection.
 *
 * To createList a new model the {@link BeanManager} provides the {@link #create(Class)} method that is
 * defined as a factory method for any kind of model. A model should never be instantiated by hand because in that case
 * the model won't be synchronized between client and server. Here is an example how a model can be created:
 * <blockquote>
 * <pre>
 *     {@code MyModel model = beanManager.createList(MyModel.class); }
 * </pre>
 * </blockquote>
 *
 * The {@link BeanManager} provides several methods to observe the creation and deletion of models.
 * One example is the {@link #onAdded(Class, BeanAddedListener)} method. All the methods are
 * for using lambdas and therefore a handler can be easily added with only one line if code:
 * <blockquote>
 * <pre>
 *     {@code beanManager.onAdded(MyModel.class, model -> System.out.println("Model of type MyModel added")); }
 * </pre>
 * </blockquote>
 * There are no method to removePresentationModel registered handler from the {@link BeanManager}. Here the remoting layer
 * implement an approach by using the Subscription Pattern: Each hander registration returns a {@link Subscription}
 * instance that provides the {@link Subscription#unsubscribe()} method to removePresentationModel the handler.
 *
 * @author Hendrik Ebbers
 */
@API(since = "0.x", status = MAINTAINED)
public interface BeanManager {

    /**
     * Creates a new instance of the given remoting bean class that will automatically be synced with the client.
     * The given class must be defined as a remoting bean
     *
     * @param beanClass the bean class
     * @param <T>       bean type
     * @return the new bean instance
     */
    <T> T create(Class<T> beanClass);
}
