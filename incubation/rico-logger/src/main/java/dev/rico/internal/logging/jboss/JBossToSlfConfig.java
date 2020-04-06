package dev.rico.internal.logging.jboss;

import java.security.AccessController;
import java.security.PrivilegedAction;

public class JBossToSlfConfig {

    private static final String LOGGING_PROVIDER_KEY = "org.jboss.logging.provider";

    private static final String LOGGING_PROVIDER_VALUE = "slf4j";

    public void setSystemProperty() {
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
                System.setProperty(LOGGING_PROVIDER_KEY, LOGGING_PROVIDER_VALUE);
                return null;
            }
        });
    }

}
