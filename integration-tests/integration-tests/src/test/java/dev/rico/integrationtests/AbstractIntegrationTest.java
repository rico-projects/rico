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
package dev.rico.integrationtests;

import dev.rico.docker.DockerCompose;
import dev.rico.docker.Wait;
import dev.rico.internal.core.Assert;
import dev.rico.internal.core.SimpleThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static dev.rico.integrationtests.AbstractIntegrationTest.INTEGRATION_TESTS_TEST_GROUP;
import static dev.rico.internal.core.http.HttpStatus.HTTP_OK;

@Test(groups = INTEGRATION_TESTS_TEST_GROUP)
public class AbstractIntegrationTest implements ITest {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractIntegrationTest.class);

    private static final ThreadLocal<String> testName = new ThreadLocal<>();

    private final int timeoutInMinutes = 1;

    public static final String ENDPOINTS_DATAPROVIDER = "endpoints";

    public static final String INTEGRATION_TESTS_TEST_GROUP = "INTEGRATION-TESTS";

    private final DockerCompose dockerCompose;

    private final List<IntegrationEndpoint> endpoints;

    public AbstractIntegrationTest() {
        endpoints = Arrays.asList(IntegrationEndpoint.values());
        try {
            final URL dockerComposeURL = AbstractIntegrationTest.class.getClassLoader().getResource("docker-compose.yml");
            final Path dockerComposeFile = Paths.get(dockerComposeURL.toURI());
            final Executor backgroundExecutor = Executors.newCachedThreadPool(new SimpleThreadFactory(true));
            dockerCompose = new DockerCompose(backgroundExecutor, dockerComposeFile);
        } catch (Exception e) {
            throw new RuntimeException("Can not create Docker environment!", e);
        }
    }

    @BeforeGroups(INTEGRATION_TESTS_TEST_GROUP)
    protected void startDockerContainers() {
        final Wait[] waits = endpoints.stream()
                .map(e -> Wait.forHttp(e.getHealthEndpoint(), HTTP_OK))
                .collect(Collectors.toList())
                .toArray(new Wait[]{});
        dockerCompose.start(timeoutInMinutes, TimeUnit.MINUTES, waits);
    }

    @AfterGroups(INTEGRATION_TESTS_TEST_GROUP)
    protected void stopDockerContainers() {
        dockerCompose.kill();
    }

    @BeforeMethod
    public void beforeMethod(final Method method, final Object[] data) {
        Assert.requireNonNull(method, "method");
        Assert.requireNonNull(data, "data");
        final String name = method.getDeclaringClass().getSimpleName() + "." + method.getName() + "_" + data[0];
        LOG.info("Starting test " + name);
        testName.set(name);
    }

    @AfterMethod
    public void afterMethod(){
        final String name = testName.get();
        LOG.info("DONE test " + name);
    }

    @Override
    public String getTestName() {
        return testName.get();
    }

    @DataProvider(name = ENDPOINTS_DATAPROVIDER, parallel = false)
    public Object[][] getEndpoints() {
        return endpoints.stream()
                .map(e -> new String[]{e.getName(), e.getEndpoint().toString()})
                .collect(Collectors.toList())
                .toArray(new String[][]{});
    }

    @BeforeClass
    public void setDataProviderThreadCount(ITestContext context) {
        context.getCurrentXmlTest().getSuite().setDataProviderThreadCount(1);
    }

    public int getTimeoutInMinutes() {
        return timeoutInMinutes;
    }
}
