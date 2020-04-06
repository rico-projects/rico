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
package dev.rico.server.remoting;

import dev.rico.remoting.RemotingBean;
import org.apiguardian.api.API;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * <p>
 * Can be used in a remoting controller (see {@link RemotingController}) to
 * define and inject the model of the controller (MVC group). In each remoting controller only one field can
 * be annoted by {@link RemotingModel} and the field type must match the model
 * definition of the remoting layer (see {@link RemotingBean} for a detailed definition).
 *</p>
 * <p>
 * As defined by the remoting all models will be automatically synchronized between client and server. See
 * {@link RemotingBean} for more details.
 * <br>
 * <center><img src="doc-files/model-sync.png" alt="model is synchronized between client and server"></center>
 *</p>
 * <p>
 * A model that is injected by using {@link RemotingModel} will automatically be createList when the
 * server controller is created and will be destroyed with the controller. By doing so the complete MVC group (shared
 * model, controller on server side and the view on client side) will have the same lifecycle and the model can easily
 * be accessed from client and server.
 *</p>
 * <p>
 * Example:
 *<blockquote>
 * <pre>
 *     {@literal @}RemotingController("my-controller")
 *     public class MyController {
 *
 *          {@literal @}RemotingModel
 *          private MyModel model;
 *
 *         {@literal @}PostContruct()
 *         private void init() {
 *             model.setViewTitle("My View");
 *         };
 *     }
 * </pre>
 * </blockquote>
 *</p>
 *
 * @author Hendrik Ebbers
 * @see RemotingController
 */
@Documented
@Retention(RUNTIME)
@Target(ElementType.FIELD)
@API(since = "0.x", status = MAINTAINED)
public @interface RemotingModel {
}
