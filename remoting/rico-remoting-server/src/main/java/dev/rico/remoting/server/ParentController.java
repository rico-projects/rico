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
package dev.rico.remoting.server;

import org.apiguardian.api.API;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * Can be used in a remoting controller (see {@link RemotingController}) to
 * define and inject the parent controller. This can only be done if the controller
 * was created as a child of another controller (see client site API).
 *
 *  @author Hendrik Ebbers
 *
 *  @see PostChildCreated
 *  @see PreChildDestroyed
 *  @see RemotingController
 */
@Documented
@Retention(RUNTIME)
@Target(ElementType.FIELD)
@API(since = "0.x", status = MAINTAINED)
public @interface ParentController {
}
