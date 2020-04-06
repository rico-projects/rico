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
package dev.rico.internal.server.security;

import dev.rico.server.security.SecurityContext;
import dev.rico.server.security.SecurityException;
import dev.rico.server.security.User;
import org.apiguardian.api.API;
import org.keycloak.KeycloakSecurityContext;

import java.util.Optional;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.19.0", status = INTERNAL)
public class SecurityContextKeycloakImpl implements SecurityContext {

    private final User user;

    private final AccessDeniedCallback accessDeniedCallback;

    public SecurityContextKeycloakImpl(final KeycloakSecurityContext keycloakSecurityContext, final AccessDeniedCallback accessDeniedCallback) {
        this.user = Optional.ofNullable(keycloakSecurityContext).map(c -> new UserKeycloakImpl(keycloakSecurityContext)).orElse(null);
        this.accessDeniedCallback = accessDeniedCallback;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void accessDenied() {
        try {
            accessDeniedCallback.onAccessDenied();
        } finally {
            throw new SecurityException("Access Denied");
        }
    }
}
