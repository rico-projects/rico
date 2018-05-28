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
package dev.rico.internal.server.scanner;

import dev.rico.internal.core.Assert;
import dev.rico.server.spi.components.ClasspathScanner;
import org.apiguardian.api.API;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * This class can be used to search for a set of classes in the classpath. Currently all classes that are annotated
 * with a specific annotation can be found.
 */
@API(since = "0.x", status = INTERNAL)
public class DefaultClasspathScanner implements ClasspathScanner {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultClasspathScanner.class);

    private final Reflections reflections;

    public DefaultClasspathScanner(final String rootPackage){
        this(Collections.singletonList(rootPackage));
    }

    public DefaultClasspathScanner(final String... rootPackages){
        this(Arrays.asList(rootPackages));
    }

    public DefaultClasspathScanner(final List<String> rootPackages) {

        Assert.requireNonNull(rootPackages, "rootPackages");

        LOG.trace("Scanning class path for root packages {}", Arrays.toString(rootPackages.toArray()));

        ConfigurationBuilder configuration = ConfigurationBuilder.build(DefaultClasspathScanner.class.getClassLoader());
        configuration = configuration.setExpandSuperTypes(false);

        if(rootPackages.size() > 0) {
            configuration = configuration.forPackages(rootPackages.toArray(new String[rootPackages.size()]));
            configuration = configuration.setUrls(rootPackages.stream().map(rootPackage -> ClasspathHelper.forPackage(rootPackage)).flatMap(list -> list.stream()).collect(Collectors.toList()));
            configuration = configuration.filterInputsBy(new FilterBuilder().includePackage(rootPackages.toArray(new String[rootPackages.size()])));
        }

        //Special case for JBOSS Application server to get all classes
        try {
            final Enumeration<URL> res = DefaultClasspathScanner.class.getClassLoader().getResources("");
            configuration.getUrls().addAll(Collections.list(res));
        } catch (final IOException e) {
            throw new RuntimeException("Error in controller class scan", e);
        }


        //Remove native libs (will be added on Mac in a Spring Boot app)
        final Set<URL> urls = configuration.getUrls();
        final List<URL> toRemove = new ArrayList<>();
        for (final URL url : urls) {
            if (url.toString().endsWith(".jnilib")) {
                toRemove.add(url);
            }
        }
        LOG.trace("Configuration Urls {}", Arrays.toString(configuration.getUrls().toArray()));
        for (final URL url : toRemove) {
            LOG.trace("Url removed {}", url.toString());
            configuration.getUrls().remove(url);
        }
        reflections = new Reflections(configuration);
    }

    /**
     * Returns a set that contains all classes in the classpath that are annotated with the given annotation
     * @param annotation the annotation
     * @return the set of annotated classes
     */
    public synchronized Set<Class<?>> getTypesAnnotatedWith(final Class<? extends Annotation> annotation) {
        Assert.requireNonNull(annotation, "annotation");
        return reflections.getTypesAnnotatedWith(annotation);
    }
}
