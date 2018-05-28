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
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * <p>
 * When defining a controller method by using {@link RemotingAction} the
 * remoting layer supports methods parameters. The parameters can be set on the client when calling
 * the action method. To define parameters o the server controller method each paramter must be annotated
 * with the {@link Param} annotation.
 *</p>
 * <p>
 * Example:
 *
 * <blockquote>
 * <pre>
 *     {@literal @}RemotingController("my-controller")
 *     public class MyController {
 *
 *         {@literal @}RemotingAction("my-action")
 *         private void showById(@Param("id") id) { . . . };
 *     }
 * </pre>
 * </blockquote>
 *</p>
 *
 * @author Hendrik Ebbers
 * @see RemotingAction
 */
@Documented
@Retention(RUNTIME)
@Target(ElementType.PARAMETER)
@API(since = "0.x", status = MAINTAINED)
public @interface Param {

    /**
     * The name of the param
     * @return the name
     */
    String value() default "";

}
