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
package dev.rico.server.security;

import org.apiguardian.api.API;

import java.util.Collection;
import java.util.Optional;

@API(since = "0.19.0", status = API.Status.EXPERIMENTAL)
public interface SecurityContext {

    String[] EMPTY_STRING_ARRAY = {};

    default boolean hasRole(final String role) {
        return Optional.ofNullable(getUser()).
                map(u -> u.getRoles()).
                map(l -> l.contains(role)).
                orElse(false);
    }

    default void requireRole(final String role) {
        if (!hasRole(role)) {
            accessDenied();
        }
    }

    default void ifRole(final String role, final Runnable r) {
        if (hasRole(role)) {
            r.run();
        }
    }

    default boolean hasAnyRole(final String... roles) {
        for (String role : roles) {
            if (hasRole(role)) {
                return true;
            }
        }
        return false;
    }

    default void requireAnyRole(final String... roles) {
        if (!hasAnyRole(roles)) {
            accessDenied();
        }
    }

    default void ifAnyRole(final String[] roles, final Runnable r) {
        if (hasAnyRole(roles)) {
            r.run();
        }
    }

    default void ifAnyRole(final Collection<String> roles, final Runnable r) {
        if (hasAnyRole(roles.toArray(EMPTY_STRING_ARRAY))) {
            r.run();
        }
    }

    default boolean hasAllRoles(final String... roles) {
        for (String role : roles) {
            if (hasRole(role)) {
                return true;
            }
        }
        return false;
    }

    default void requireAllRoles(final String... roles) {
        if (!hasAllRoles(roles)) {
            accessDenied();
        }
    }

    default void ifAllRoles(final String[] roles, final Runnable r) {
        if (hasAllRoles(roles)) {
            r.run();
        }
    }

    default void ifAllRoles(final Collection<String> roles, final Runnable r) {
        if (hasAllRoles(roles.toArray(EMPTY_STRING_ARRAY))) {
            r.run();
        }
    }

    default boolean isUser(final String user) {
        return Optional.ofNullable(getUser()).
                map(u -> u.getUserName()).
                map(u -> u.equals(user)).orElse(false);
    }

    default void requireUser(final String user) {
        if (!isUser(user)) {
           accessDenied();
        }
    }

    default void ifUser(final String user, final Runnable r) {
        if (isUser(user)) {
            r.run();
        }
    }

    default boolean hasUser() {
        return Optional.ofNullable(getUser()).isPresent();
    }

    User getUser();

    void accessDenied();
}
