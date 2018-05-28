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
package dev.rico.server;

import dev.rico.internal.server.remoting.config.ConfigurationFileLoader;
import dev.rico.internal.server.remoting.config.ServerConfiguration;
import dev.rico.server.scanner.documented.DocumentAnnotatedClass;
import dev.rico.server.util.AnnotatedClassForClasspathScan;
import dev.rico.server.util.AnnotationForClasspathScanTest;
import dev.rico.internal.server.scanner.DefaultClasspathScanner;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import javax.annotation.Resources;
import java.lang.annotation.Documented;
import java.util.Set;

import static dev.rico.internal.server.bootstrap.BasicConfigurationProvider.ROOT_PACKAGE_FOR_CLASSPATH_SCAN;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class ClasspathScannerTest {

    private static final String CLASSPATH_SCAN = "dev.rico.server.scanner";

    @Test
    public void testSimpleScan() {
        //There can't be a class that is annotated with Inject
        final ServerConfiguration defaultPlatformConfiguration = ConfigurationFileLoader.loadConfiguration();
        final DefaultClasspathScanner scanner = new DefaultClasspathScanner(defaultPlatformConfiguration.getListProperty(ROOT_PACKAGE_FOR_CLASSPATH_SCAN));
        Set<Class<?>> classes = scanner.getTypesAnnotatedWith(Resources.class);
        assertNotNull(classes);
        assertEquals(classes.size(), 0);

        classes = scanner.getTypesAnnotatedWith(AnnotationForClasspathScanTest.class);
        assertNotNull(classes);
        assertEquals(classes.size(), 1);
        assertTrue(classes.contains(AnnotatedClassForClasspathScan.class));
    }

    @Test
    public void testInPackageScan() {
        //There can't be a class that is annotated with Inject
        final DefaultClasspathScanner scanner = new DefaultClasspathScanner(CLASSPATH_SCAN);
        assertNotNull(scanner);
        assertForAnnotation(scanner);
    }

    @Test
    public void testInMultiplePackages() {
        //There can't be a class that is annotated with Inject
        final DefaultClasspathScanner scanner = new DefaultClasspathScanner("dev.rico.server.scanner.documented", "dev.rico.server.scanner.resource");
        assertForAnnotation(scanner);
    }

    private void assertForAnnotation(final DefaultClasspathScanner scanner) {
        final Set<Class<?>> resourceClasses = scanner.getTypesAnnotatedWith(Resource.class);
        assertNotNull(resourceClasses);
        assertEquals(resourceClasses.size(), 1);

        final Set<Class<?>> documentClasses = scanner.getTypesAnnotatedWith(Documented.class);
        assertNotNull(documentClasses);
        assertEquals(documentClasses.size(), 1);
    }

    @Test
    public void testInMultiplePackagesWithBasePackageFirst() {
        //There can't be a class that is annotated with Inject
        final DefaultClasspathScanner scanner = new DefaultClasspathScanner(CLASSPATH_SCAN, "com.canoo.impl.server.classpathscan.documented", "com.canoo.impl.server.classpathscan.resource");
        assertForAnnotation(scanner);
    }

    @Test
    public void testScanOtherPackage() {
        //There can't be a class that is annotated with Inject
        final DefaultClasspathScanner scanner = new DefaultClasspathScanner(CLASSPATH_SCAN);
        Set<Class<?>> classes = scanner.getTypesAnnotatedWith(Resources.class);
        assertNotNull(classes);
        assertEquals(classes.size(), 0);

        classes = scanner.getTypesAnnotatedWith(AnnotationForClasspathScanTest.class);
        assertNotNull(classes);
        assertEquals(classes.size(), 0);

        classes = scanner.getTypesAnnotatedWith(Documented.class);
        assertNotNull(classes);
        assertEquals(classes.size(), 1);
        assertTrue(classes.contains(DocumentAnnotatedClass.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullArgument() {
        final DefaultClasspathScanner scanner = new DefaultClasspathScanner(CLASSPATH_SCAN);
        Set<Class<?>> classes = scanner.getTypesAnnotatedWith(null);
    }

}
