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

import dev.rico.internal.core.Assert;
import dev.rico.security.server.User;
import org.apiguardian.api.API;
import org.keycloak.KeycloakSecurityContext;

import java.util.Collections;
import java.util.Set;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.19.0", status = INTERNAL)
public class UserKeycloakImpl implements User {

    private final KeycloakSecurityContext keycloakSecurityContext;

    public UserKeycloakImpl(KeycloakSecurityContext keycloakSecurityContext) {
        this.keycloakSecurityContext = Assert.requireNonNull(keycloakSecurityContext, "keycloakSecurityContext");
    }

    @Override
    public Set<String> getRoles() {
        return Collections.unmodifiableSet(keycloakSecurityContext.getToken().getRealmAccess().getRoles());
    }

    @Override
    public String getEmail() {
        return keycloakSecurityContext.getToken().getEmail();
    }

    @Override
    public String getUserName() {
        return keycloakSecurityContext.getToken().getPreferredUsername();
    }

    @Override
    public String getFirstName() {
        return keycloakSecurityContext.getToken().getGivenName();
    }

    @Override
    public String getLastName() {
        return keycloakSecurityContext.getToken().getFamilyName();
    }

    @Override
    public String toString() {
        return "User " + getUserName() + " [first name:" + getFirstName() + ", last name:" + getLastName() + ", mail:" + getEmail() + "] Roles:" + getRoles().stream().reduce("", (a,b) -> a + ", " + b);
    }
}
