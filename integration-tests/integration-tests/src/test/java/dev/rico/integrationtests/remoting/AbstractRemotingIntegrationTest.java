package dev.rico.integrationtests.remoting;

import dev.rico.client.Client;
import dev.rico.integrationtests.AbstractIntegrationTest;
import dev.rico.integrationtests.IntegrationTestToolkit;
import dev.rico.remoting.client.ClientContext;
import dev.rico.remoting.client.ClientContextFactory;
import dev.rico.remoting.client.ControllerProxy;
import dev.rico.remoting.client.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;

import java.io.Serializable;
import java.net.HttpCookie;
import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AbstractRemotingIntegrationTest extends AbstractIntegrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractRemotingIntegrationTest.class);

    protected <T> ControllerProxy<T> createController(final ClientContext clientContext, final String controllerName) {
        return createController(clientContext, controllerName, Collections.emptyMap());
    }

    @SuppressWarnings("unchecked")
    protected <T> ControllerProxy<T> createController(final ClientContext clientContext, final String controllerName, final Map<String, Serializable> parameters) {
        try {
            LOG.trace("Cookies before create controller: " + Client.getClientConfiguration().getCookieStore().getCookies());
            return (ControllerProxy<T>) clientContext.createController(controllerName, parameters).get(getTimeoutInMinutes(), TimeUnit.MINUTES);
        } catch (Exception e) {
            LOG.trace("Cookies when failed to create controller: " + Client.getClientConfiguration().getCookieStore().getCookies());
            throw new RuntimeException("Can not create controller " + controllerName, e);
        }
    }

    @AfterMethod
    public void tearDown() {
        final List<HttpCookie> cookies = Client.getClientConfiguration().getCookieStore().getCookies();
        if (cookies.size() != 1) {
            Assert.fail("Found unexpected number of cookies in store: " + cookies);
        }
        Client.getClientConfiguration().getCookieStore().removeAll();
    }

    protected ClientContext connect(final String endpoint) {
        Client.init(new IntegrationTestToolkit());
        final List<HttpCookie> cookies = Client.getClientConfiguration().getCookieStore().getCookies();
        if (!cookies.isEmpty()) {
            Assert.fail("Cookie store should have been empty, but found: " + cookies);
        }
        try {
            final ClientContext clientContext = Client.getService(ClientContextFactory.class).create(Client.getClientConfiguration(), new URI(endpoint + "/remoting"));
            final long timeOutTime = System.currentTimeMillis() + Duration.ofMinutes(getTimeoutInMinutes()).toMillis();
            while (System.currentTimeMillis() < timeOutTime && clientContext.getClientId() == null) {
                try {
                    clientContext.connect().get(getTimeoutInMinutes(), TimeUnit.MINUTES);
                } catch (Exception ex) {
                    LOG.error("Failed to connect", ex);
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

    protected void invoke(final ControllerProxy<?> controllerProxy, final String actionName, final String containerType, final Param... params) {
        try {
            controllerProxy.invoke(actionName, params).get(getTimeoutInMinutes(), TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException("Can not withoutResult action " + actionName + " for containerType " + containerType, e);
        }
    }

    protected void invoke(final ControllerProxy<?> controllerProxy, final String actionName, final String containerType, final Map<String, ?> params) {
        try {
            controllerProxy.invoke(actionName, params).get(getTimeoutInMinutes(), TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException("Can not withoutResult action " + actionName + " for containerType " + containerType, e);
        }
    }

    protected void destroy(final ControllerProxy<?> controllerProxy, final String endpoint) {
        try {
            controllerProxy.destroy().get(getTimeoutInMinutes(), TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException("Can not destroy controller for endpoint " + endpoint, e);
        }
    }

    protected void disconnect(final ClientContext clientContext, final String endpoint) {
        try {
            clientContext.disconnect().get(getTimeoutInMinutes(), TimeUnit.MINUTES);
        } catch (Exception e) {
            //do nothing
        }
    }
}
