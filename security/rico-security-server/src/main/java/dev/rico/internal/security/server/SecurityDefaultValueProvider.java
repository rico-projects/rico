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
package dev.rico.internal.security.server;

import dev.rico.internal.server.bootstrap.ConfigurationProviderAdapter;
import org.apiguardian.api.API;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.rico.internal.security.SecurityConstants.*;
import static dev.rico.internal.security.server.SecurityServerConstants.*;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.19.0", status = INTERNAL)
public class SecurityDefaultValueProvider extends ConfigurationProviderAdapter {

    @Override
    public Map<String, String> getStringProperties() {
        final HashMap<String, String> ret = new HashMap<>();
        ret.put(AUTH_ENDPOINT_PROPERTY_NAME, AUTH_ENDPOINT_PROPERTY_DEFAULT_VALUE);
        ret.put(LOGIN_ENDPOINTS_PROPERTY_NAME, LOGIN_ENDPOINTS_PROPERTY_DEFAULT_VALUE);
        ret.put(LOGOUT_ENDPOINTS_PROPERTY_NAME, LOGOUT_ENDPOINTS_PROPERTY_DEFAULT_VALUE);
        ret.put(REALM_PROPERTY_NAME, REALM_PROPERTY_DEFAULT_VALUE);
        ret.put(APPLICATION_PROPERTY_NAME, APPLICATION_PROPERTY_DEFAULT_VALUE);
        return ret;
    }

    @Override
    public Map<String, Boolean> getBooleanProperties() {
        final HashMap<String, Boolean> ret = new HashMap<>();
        ret.put(SECURITY_MODULE_ACTIVE_PROPERTY, SECURITY_MODULE_ACTIVE_PROPERTY_DEFAULT_VALUE);
        ret.put(CORS_PROPERTY_NAME, CORS_PROPERTY_DEFAULT_VALUE);
        ret.put(LOGIN_ENDPOINTS_ACTIVE_PROPERTY_NAME, LOGIN_ENDPOINTS_ACTIVE_PROPERTY_DEFAULT_VALUE);
        ret.put(DIRECT_CONNECTION_PROPERTY_NAME, DIRECT_CONNECTION_PROPERTY_DEFAULT_VALUE);
        ret.put(REALM_CHECK_ACTIVE_PROPERTY_NAME, REALM_CHECK_ACTIVE_PROPERTY_DEFAULT_VALUE);
        return ret;
    }

    @Override
    public Map<String, List<String>> getListProperties() {
        final HashMap<String, List<String>> ret = new HashMap<>();
        ret.put(SECURE_ENDPOINTS_PROPERTY_NAME, SECURE_ENDPOINTS_PROPERTY_DEFAULT_VALUE);
        ret.put(REALMS_PROPERTY_NAME, REALMS_PROPERTY_DEFAULT_VALUE);
        return ret;
    }
}
