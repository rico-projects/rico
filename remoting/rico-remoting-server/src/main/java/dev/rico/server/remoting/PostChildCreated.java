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
 * Can you used to annotate a method in a remoting controller (see {@link RemotingController}) as an event handler that will automatically be called if a child controller was created for the given controller (see client API).
 * The method must have exactly one parameter that is of the class or superclass / interface of the child controller.
 *
 * @author Hendrik Ebbers
 *
 * @see RemotingController
 * @see ParentController
 * @see PreChildDestroyed
 */
@Documented
@Retention(RUNTIME)
@Target(ElementType.METHOD)
@API(since = "0.x", status = MAINTAINED)
public @interface PostChildCreated {
}
