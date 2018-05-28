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
package dev.rico.internal.security;

import org.apiguardian.api.API;

@API(since = "0.19.0", status = API.Status.EXPERIMENTAL)
public interface SecurityConstants {

    String AUTH_ENDPOINT_PROPERTY_NAME = "security.keycloak.endpoint";

    String REALM_PROPERTY_NAME = "security.keycloak.realm";

    String APPLICATION_PROPERTY_NAME = "security.keycloak.app";

    String DIRECT_CONNECTION_PROPERTY_NAME = "security.useDirectConnection";

    String AUTH_ENDPOINT_PROPERTY_DEFAULT_VALUE = "http://localhost:8080/openid-connect";

    boolean DIRECT_CONNECTION_PROPERTY_DEFAULT_VALUE = false;

    String REALM_NAME_HEADER = "X-platform-security-realm";

    String APPLICATION_NAME_HEADER = "X-platform-security-application";

    String BEARER_ONLY_HEADER = "X-platform-security-bearer-only";

    String AUTHORIZATION_HEADER = "Authorization";

    String BEARER = "Bearer ";

    String USER_CONTEXT = "user";

}
