package dev.rico.integrationtests;

import java.net.URI;
import java.net.URISyntaxException;

public enum IntegrationEndpoint {
    WILDFLY("Wildfly", 8083), TOMEE("TomEE", 8082);

    private static final String ENDPOINT_URI_PREFIX = "http://localhost:";

    private static final String CONTEXT_PATH = "/integration-tests";

    private static final String HEALTH_PATH = "/rest/health";

    private static final String REMOTING_PATH = "/remoting";

    private final String name;
    private final int port;

    IntegrationEndpoint(String name, int port) {
        this.name = name;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }

    public URI getHealthEndpoint() {
        try {
            return new URI(getBasicEndpoint() + CONTEXT_PATH + HEALTH_PATH);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error creating endpoint", e);
        }
    }

    public URI getRemotingEndpoint() {
        try {
            return new URI(getBasicEndpoint() + CONTEXT_PATH + REMOTING_PATH);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error creating endpoint", e);
        }
    }

    public URI getEndpoint() {
        try {
            return new URI(getBasicEndpoint() + CONTEXT_PATH);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error creating endpoint", e);
        }
    }

    private String getBasicEndpoint() {
        return ENDPOINT_URI_PREFIX + getPort();
    }
}
