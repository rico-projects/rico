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
package dev.rico.integrationtests;

import dev.rico.client.Client;
import dev.rico.client.concurrent.BackgroundExecutor;
import dev.rico.client.remoting.ClientContext;
import dev.rico.client.remoting.ClientContextFactory;
import dev.rico.client.remoting.ControllerProxy;
import dev.rico.client.remoting.Param;
import dev.rico.docker.DockerCompose;
import dev.rico.docker.Wait;
import dev.rico.internal.core.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static dev.rico.integrationtests.AbstractIntegrationTest.INTEGRATION_TESTS_TEST_GROUP;
import static dev.rico.internal.core.http.HttpStatus.HTTP_OK;

@Test(groups = INTEGRATION_TESTS_TEST_GROUP)
public class AbstractIntegrationTest {

    private final static Logger LOG = LoggerFactory.getLogger(AbstractIntegrationTest.class);

    private int timeoutInMinutes = 3;

    public final static String ENDPOINTS_DATAPROVIDER = "endpoints";

    public final static String INTEGRATION_TESTS_TEST_GROUP = "INTEGRATION-TESTS";

    private final DockerCompose dockerCompose;

    private final List<IntegrationEndpoint> endpoints;

    public AbstractIntegrationTest() {
        endpoints = Arrays.asList(IntegrationEndpoint.values());
        try {
            final URL dockerComposeURL = AbstractIntegrationTest.class.getClassLoader().getResource("docker-compose.yml");
            final Path dockerComposeFile = Paths.get(dockerComposeURL.toURI());
            final BackgroundExecutor backgroundExecutor = Client.getService(BackgroundExecutor.class);
            dockerCompose = new DockerCompose(backgroundExecutor, dockerComposeFile);
        } catch (Exception e) {
            throw new RuntimeException("Can not createList Docker environment!", e);
        }
    }

    @BeforeGroups(INTEGRATION_TESTS_TEST_GROUP)
    protected void startDockerContainers() {
        final Wait[] waits = endpoints.stream()
                .map(e -> Wait.forHttp(e.getHeathEndpoint(), HTTP_OK))
                .collect(Collectors.toList())
                .toArray(new Wait[]{});
        dockerCompose.start(timeoutInMinutes, TimeUnit.MINUTES, waits);
    }

    @AfterGroups(INTEGRATION_TESTS_TEST_GROUP)
    protected void stopDockerContainers() {
        dockerCompose.kill();
    }

    @BeforeMethod
    public void onTest(final Method method, final Object[] data) {
        Assert.requireNonNull(method, "method");
        Assert.requireNonNull(data, "data");
        LOG.info("Starting test " + method.getDeclaringClass().getSimpleName() +"." + method.getName() + " for " + data[0]);
    }

    @DataProvider(name = ENDPOINTS_DATAPROVIDER, parallel = false)
    public Object[][] getEndpoints() {
        return endpoints.stream()
                .map(e -> new String[]{e.getName(), e.getEndpoint().toString()})
                .collect(Collectors.toList())
                .toArray(new String[][]{});
    }

    protected <T> ControllerProxy<T> createController(ClientContext clientContext, String controllerName) {
        try {
            return (ControllerProxy<T>) clientContext.createController(controllerName).get(timeoutInMinutes, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException("Can not createList controller " + controllerName, e);
        }
    }

    protected ClientContext connect(String endpoint) {
        Client.init(new IntegrationTestToolkit());
        Client.getClientConfiguration().getCookieStore().removeAll();
        try {
            ClientContext clientContext = Client.getService(ClientContextFactory.class).create(Client.getClientConfiguration(), new URI(endpoint + "/remoting"));
            long timeOutTime = System.currentTimeMillis() + Duration.ofMinutes(timeoutInMinutes).toMillis();
            while (System.currentTimeMillis() < timeOutTime && clientContext.getClientId() == null) {
                try {
                    clientContext.connect().get(timeoutInMinutes, TimeUnit.MINUTES);
                } catch (Exception ex) {
                    // do nothing since server is not up at the moment...
                }
            }
            if (clientContext.getClientId() == null) {
                throw new Exception("Client context not created....");
            }

            return clientContext;
        } catch (Exception e) {
            throw new RuntimeException("Can not createList client context for endpoint " + endpoint, e);
        }
    }

    protected void invoke(ControllerProxy<?> controllerProxy, String actionName, String containerType, Param... params) {
        try {
            controllerProxy.invoke(actionName, params).get(timeoutInMinutes, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException("Can not withoutResult action " + actionName + " for containerType " + containerType, e);
        }
    }

    protected void invoke(ControllerProxy<?> controllerProxy, String actionName, String containerType, Map<String, ?> params) {
        try {
            controllerProxy.invoke(actionName, params).get(timeoutInMinutes, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException("Can not withoutResult action " + actionName + " for containerType " + containerType, e);
        }
    }

    protected void destroy(ControllerProxy<?> controllerProxy, String endpoint) {
        try {
            controllerProxy.destroy().get(timeoutInMinutes, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException("Can not destroy controller for endpoint " + endpoint, e);
        }
    }

    protected void disconnect(ClientContext clientContext, String endpoint) {
        try {
            clientContext.disconnect().get(timeoutInMinutes, TimeUnit.MINUTES);
        } catch (Exception e) {
            //do nothing
        }
    }
}
