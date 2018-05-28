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
package dev.rico.server.remoting;

import org.apiguardian.api.API;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * <p>Annotation to define a remoting controller. A remoting controller is the server side controller of a MVC group / widget.
 * When using the remoting layer of Rico your application is seperated in a hirarchie of several MVC groups. Each MVC group defines
 * a client side view, a server side controller and a model that is automatically shared between the client and server.
 * <br>
 * <center><img src="doc-files/model-sync.png" alt="model is synchronized between client and server"></center>
 * </p>
 * <p>
 * Each remoting controller must be annotated with {@link RemotingController}.
 * A remoting controller will automatically managed by the container (spring or JavaEE) and the lifecycle
 * of the controller is bound to the view lifecycle. A server site controller will be automatically created
 * when a macthing controller proxy is created at the client. This means that you can have several instances of
 * a remoting controller on the server, one for each matching view instance. This will happen if several clients use the
 * same view or a view is shown several times in a client.
 * </p>
 * <p>
 * Since the controller instances are managed by the platform you can use all teh default annotations and APIs
 * that are supported by the Platform like {@link javax.annotation.PostConstruct} or {@code javax.inject.Inject}.
 * In addition the remoting layer supports some custom annotations that can be used in a controller:
 * <ul>
 * <li>{@link RemotingModel}</li>
 * <li>{@link RemotingAction}</li>
 * <li>{@link Param}</li>
 * </ul>
 * </p>
 * <p>
 * Example:
 * <blockquote>
 * <pre>
 *     {@literal @}RemotingController("my-controller")
 *     public class MyController {
 *
 *          {@literal @}RemotingModel
 *          private MyModel model;
 *
 *         {@literal @}RemotingAction()
 *         private void onAction() { . . . };
 *     }
 * </pre>
 * </blockquote>
 * </p>
 *
 * @author Hendrik Ebbers
 */
@Documented
@Inherited
@Retention(RUNTIME)
@Target(ElementType.TYPE)
@API(since = "0.x", status = MAINTAINED)
public @interface RemotingController {

    /**
     * The unique name of the controller. If this is empty the fully qualified class name of the controller class
     * will be automatically used.
     * @return the unique name of the controller
     */
    String value() default "";

}
