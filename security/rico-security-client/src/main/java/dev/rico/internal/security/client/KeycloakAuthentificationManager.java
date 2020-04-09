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
package dev.rico.internal.security.client;

import java.net.URL;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class KeycloakAuthentificationManager {

    private final static KeycloakAuthentificationManager INSTANCE = new KeycloakAuthentificationManager();

    private final AtomicReference<KeycloakAuthentification> auth;

    private KeycloakAuthentificationManager() {
        this.auth = new AtomicReference<>();
    }

    public Optional<KeycloakAuthentification> getAuthFor(final URL endpoint) {
        return Optional.ofNullable(auth.get());
    }

    public void setAuth(final KeycloakAuthentification auth) {
        this.auth.set(auth);
    }

    public static KeycloakAuthentificationManager getInstance() {
        return INSTANCE;
    }
}
