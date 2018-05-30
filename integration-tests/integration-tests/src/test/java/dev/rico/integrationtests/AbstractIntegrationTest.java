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
import dev.rico.client.remoting.ClientContext;
import dev.rico.client.remoting.ClientContextFactory;
import dev.rico.client.remoting.ControllerProxy;
import dev.rico.client.remoting.Param;
import dev.rico.docker.DockerCompose;
import dev.rico.docker.Wait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class AbstractIntegrationTest {


    private final static Logger LOG = LoggerFactory.getLogger(AbstractIntegrationTest.class);

    private int bootTimeoutInMinutes = 3;

    private int timeoutInMinutes = 3;

    public final static String ENDPOINTS_DATAPROVIDER = "endpoints";

    private final DockerCompose dockerCompose;

    public AbstractIntegrationTest() {
        try {
            final URL dockerComposeURL = AbstractIntegrationTest.class.getClassLoader().getResource("docker-compose.yml");
            final Path dockerComposeFile = Paths.get(dockerComposeURL.toURI());
            dockerCompose = new DockerCompose(dockerComposeFile);
        } catch (Exception e) {
            throw new RuntimeException("Can not create Docker environment!", e);
        }
    }

    protected <T> ControllerProxy<T> createController(ClientContext clientContext, String controllerName) {
        try {
            return (ControllerProxy<T>) clientContext.createController(controllerName).get(2, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException("Can not create controller " + controllerName, e);
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
                    clientContext.connect().get(10, TimeUnit.SECONDS);
                } catch (Exception ex) {
                    // do nothing since server is not up at the moment...
                }
            }
            if (clientContext.getClientId() == null) {
                throw new Exception("Client context not created....");
            }

            return clientContext;
        } catch (Exception e) {
            throw new RuntimeException("Can not create client context for endpoint " + endpoint, e);
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
            clientContext.disconnect().get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            //do nothing
        }
    }

    @BeforeClass
    protected void startDockerContainers() {
        final Executor executor = Client.getClientConfiguration().getBackgroundExecutor();
        try {
            dockerCompose.start(2, TimeUnit.MINUTES,
                    Wait.forHttp(executor, new URI("http://localhost:8082/integration-tests/rest/health"), 200),
                    Wait.forHttp(executor, new URI("http://localhost:8083/integration-tests/rest/health"), 200));
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error", e);
        }
    }

    @AfterClass
    protected void stopDockerContainers() {
        dockerCompose.kill();
    }

    @DataProvider(name = ENDPOINTS_DATAPROVIDER, parallel = false)
    public Object[][] getEndpoints() {
        return new String[][]{
                //{"Payara", "http://localhost:8081/integration-tests"},
                {"TomEE", "http://localhost:8082/integration-tests"},
                {"Wildfly", "http://localhost:8083/integration-tests"}//,
                //{"Spring-Boot", "http://localhost:8084/integration-tests"}
        };
    }
}
