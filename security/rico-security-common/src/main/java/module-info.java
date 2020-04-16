module dev.rico.security.common {

    exports dev.rico.internal.security to dev.rico.security.client,
            dev.rico.security.server,
            dev.rico.security.server.spring;

    requires static org.apiguardian.api;
}
