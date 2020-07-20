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
package dev.rico.internal.server.scanner;

import dev.rico.core.logging.Logger;
import dev.rico.core.logging.LoggerFactory;
import dev.rico.internal.core.Assert;
import dev.rico.server.spi.components.ClasspathScanner;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import org.apiguardian.api.API;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * This class can be used to search for a set of classes in the classpath. Currently all classes that are annotated
 * with a specific annotation can be found.
 */
@API(since = "0.x", status = INTERNAL)
public class DefaultClasspathScanner implements ClasspathScanner {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultClasspathScanner.class);

    private final String[] rootPackages;

    public DefaultClasspathScanner(final String rootPackage) {
        this(Collections.singletonList(rootPackage));
    }

    public DefaultClasspathScanner(final String... rootPackages) {
        Assert.requireNonNull(rootPackages, "rootPackages");

        LOG.debug("Scanning class path for root packages {}", Arrays.toString(rootPackages));

        this.rootPackages = rootPackages;
    }

    public DefaultClasspathScanner(final List<String> rootPackages) {
        this(Assert.requireNonNull(rootPackages, "rootPackages").toArray(new String[0]));
    }

    /**
     * Returns a set that contains all classes in the classpath that are annotated with the given annotation
     *
     * @param annotation the annotation
     * @return the set of annotated classes
     */
    public synchronized Set<Class<?>> getTypesAnnotatedWith(final Class<? extends Annotation> annotation) {
        Assert.requireNonNull(annotation, "annotation");

        Set<Class<?>> result = new HashSet<>();

        final ClassGraph classGraph = new ClassGraph()
                //.verbose()
                .enableAnnotationInfo()
                .enableClassInfo()
                .ignoreClassVisibility()
                .whitelistPackages(rootPackages);

        final long startTime = System.currentTimeMillis();
        try (final ScanResult scanResult = classGraph.scan()) {                   // Start the scan
            for (final ClassInfo classInfo : scanResult.getClassesWithAnnotation(annotation.getName())) {
                if (!classInfo.isAnnotation()) {
                    final Class<?> annotatedClass = classInfo.loadClass();
                    result.add(annotatedClass);
                }
            }
        }
        LOG.debug("Classpath scan for annotation '{}' took {} ms", annotation.getName(), System.currentTimeMillis() - startTime);
        return Collections.unmodifiableSet(result);
    }
}
