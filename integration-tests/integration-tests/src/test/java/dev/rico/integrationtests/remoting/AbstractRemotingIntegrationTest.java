package dev.rico.integrationtests.remoting;

import dev.rico.client.Client;
import dev.rico.client.remoting.ClientContext;
import dev.rico.client.remoting.ClientContextFactory;
import dev.rico.client.remoting.ControllerProxy;
import dev.rico.client.remoting.Param;
import dev.rico.integrationtests.AbstractIntegrationTest;
import dev.rico.integrationtests.IntegrationTestToolkit;

import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AbstractRemotingIntegrationTest extends AbstractIntegrationTest {

    protected <T> ControllerProxy<T> createController(final ClientContext clientContext, final String controllerName) {
        try {
            return (ControllerProxy<T>) clientContext.createController(controllerName).get(getTimeoutInMinutes(), TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException("Can not create controller " + controllerName, e);
        }
    }

    protected ClientContext connect(final String endpoint) {
        Client.init(new IntegrationTestToolkit());
        Client.getClientConfiguration().getCookieStore().removeAll();
        try {
            final ClientContext clientContext = Client.getService(ClientContextFactory.class).create(Client.getClientConfiguration(), new URI(endpoint + "/remoting"));
            final long timeOutTime = System.currentTimeMillis() + Duration.ofMinutes(getTimeoutInMinutes()).toMillis();
            while (System.currentTimeMillis() < timeOutTime && clientContext.getClientId() == null) {
                try {
                    clientContext.connect().get(getTimeoutInMinutes(), TimeUnit.MINUTES);
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

    protected void invoke(final ControllerProxy<?> controllerProxy, final String actionName, final String containerType, final Param... params) {
        try {
            controllerProxy.invoke(actionName, params).get(getTimeoutInMinutes(), TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException("Can not withoutResult action " + actionName + " for containerType " + containerType, e);
        }
    }

    protected void invoke(ControllerProxy<?> controllerProxy, String actionName, String containerType, Map<String, ?> params) {
        try {
            controllerProxy.invoke(actionName, params).get(getTimeoutInMinutes(), TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException("Can not withoutResult action " + actionName + " for containerType " + containerType, e);
        }
    }

    protected void destroy(ControllerProxy<?> controllerProxy, String endpoint) {
        try {
            controllerProxy.destroy().get(getTimeoutInMinutes(), TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException("Can not destroy controller for endpoint " + endpoint, e);
        }
    }

    protected void disconnect(ClientContext clientContext, String endpoint) {
        try {
            clientContext.disconnect().get(getTimeoutInMinutes(), TimeUnit.MINUTES);
        } catch (Exception e) {
            //do nothing
        }
    }
}
