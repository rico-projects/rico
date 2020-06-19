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
package dev.rico.server.spi;

import org.apiguardian.api.API;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Annotation to define a module. Each module that should be started at the server start.
 *
 * @author Hendrik Ebbers
 * @see ServerModule
 */
@Documented
@Retention(RUNTIME)
@Target(ElementType.TYPE)
@API(since = "0.x", status = EXPERIMENTAL)
public @interface ModuleDefinition {

    /**
     * @return the unique name of the module
     */
    String name();

    /**
     * Some modules depend on other modules. This method returns a collection of the names of all modules that are needed to start this module.
     * The name of a module is defined in the {@link ModuleDefinition} annotation.
     *
     * @return a set of the names of all modules that are needed to start this module
     */
    String[] moduleDependencies() default {};

    /**
     * Defines the order number of the module.
     * <p>
     * All modules will be started sorted by their order number.
     * The module with the smallest number is started first.
     * Modules with the same number are started in an undefined order.
     *
     * @return the order number of the module
     */
    int order() default 100;
}
