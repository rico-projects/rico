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
package dev.rico.server.client;

import org.apiguardian.api.API;

import javax.servlet.http.HttpSession;
import java.util.Set;

import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * Defines a client session. For each client (each client context instance) one {@link ClientSession}
 * will be created on the server.
 *
 * @author Hendrik Ebbers
 */
@API(since = "0.x", status = MAINTAINED)
public interface ClientSession {

    /**
     * Binds the given object to this client session, using the given name.
     * @param name the name
     * @param value the object
     */
    void setAttribute(String name, Object value);

    /**
     * Returns the object that is bound to this client session with the given name or null.
     * @param name the name
     * @param <T> type of the object
     * @return the object or null
     */
    <T> T getAttribute(String name);

    /**
     * Removes the object bound with the given name from
     * this client session.
     * @param name the name
     */
    void removeAttribute(String name);

    /**
     * Returns a unmodifiable Set of all attribute names that defines objects that are bound to this session.
     * @return the set
     */
    Set<String> getAttributeNames();

    /**
     * Invalidates the client session.
     */
    void invalidate();

    /**
     * Returns the unique id of this client session.
     * @return the id
     */
    String getId();

    /**
     * Returns the Http session to that this client session belongs to.
     * @return the http session
     */
    HttpSession getHttpSession();
}
