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

import org.apiguardian.api.API;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * <p>
 * A method of a remoting controller (see {@link RemotingController}) that is annotated by this
 * annotation defines a remoting action that can be called on the client by using the controller proxy.
 * <br>
 * <center><img src="doc-files/invoke-action.png" alt="server controller action is invoked by a client view"></center>
 *</p>
 * <p>
 * Example:
 *<blockquote>
 * <pre>
 *     {@literal @}RemotingController("my-controller")
 *     public class MyController {
 *
 *         {@literal @}RemotingAction("my-action")
 *         private void doSomeAction() { . . . };
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
@Target(ElementType.METHOD)
@API(since = "0.x", status = MAINTAINED)
public @interface RemotingAction {

    /**
     * Defines the name of the action
     * @return name of the action
     */
    String value() default "";
}
