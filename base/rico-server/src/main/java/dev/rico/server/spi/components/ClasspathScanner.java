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
package dev.rico.server.spi.components;

import org.apiguardian.api.API;

import java.lang.annotation.Annotation;
import java.util.Set;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * A classpath scanner that can be used to find all classes on the classpath that are annotated by a specific annotation.
 *
 * @author Hendrik Ebbers
 */
@API(since = "0.x", status = EXPERIMENTAL)
public interface ClasspathScanner {

    /**
     * Scans all classes in the classpath for the given annotation and returns a set that contains all classes that are annotated with the given annotation.
     * @param annotation the annotation
     * @return set of all annotated classes
     */
    Set<Class<?>> getTypesAnnotatedWith(final Class<? extends Annotation> annotation);
}
