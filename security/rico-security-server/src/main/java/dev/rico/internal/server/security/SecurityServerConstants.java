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
package dev.rico.internal.server.security;

import org.apiguardian.api.API;

@API(since = "0.19.0", status = API.Status.EXPERIMENTAL)
public interface SecurityServerConstants {

    String CORS_PROPERTY_NAME = "security.keycloak.cors";

    boolean CORS_PROPERTY_DEFAULT_VALUE = true;

    String REALMS_PROPERTY_NAME = "security.keycloak.realms";

    String FILTER_NAME = "SecurityFilter";

    String EXTRACTOR_FILTER_NAME = "KeycloakSecurityContextExtractFilter";

    String KEYCLOAK_CONFIG_RESOLVER_PROPERTY_NAME = "keycloak.config.resolver";

    String SECURITY_ACTIVE_PROPERTY_NAME = "security.active";

    boolean SECURITY_ACTIVE_PROPERTY_DEFAULT_VALUE = true;

    String SECURE_ENDPOINTS_PROPERTY_NAME = "security.endpoints";

    String LOGIN_ENDPOINTS_ACTIVE_PROPERTY_NAME = "security.loginEndpoint.active";

    String LOGIN_ENDPOINTS_PROPERTY_NAME = "security.loginEndpoint";

    String LOGIN_ENDPOINTS_PROPERTY_DEFAULT_VALUE = "/openid-connect";

    String LOGOUT_ENDPOINTS_PROPERTY_NAME = "security.logoutEndpoint";

    String LOGOUT_ENDPOINTS_PROPERTY_DEFAULT_VALUE = "/openid-logout";

    String SECURE_ENDPOINTS_PROPERTY_DEFAULT_VALUE = "/remoting";

    String SECURITY_MODULE_NAME = "SecurityModule";

    String SECURITY_MODULE_ACTIVE_PROPERTY = "security.active";
}
