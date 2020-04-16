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
package dev.rico.server.remoting.test;

import dev.rico.remoting.client.Param;
import dev.rico.remoting.RemotingBean;
import dev.rico.remoting.server.RemotingAction;
import dev.rico.remoting.server.RemotingController;
import org.apiguardian.api.API;

import java.util.Map;

import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * Provides acces to a controller (see {@link RemotingController}) and its model for tests
 *
 * @param <T> type of the model
 * @author Hendrik Ebbers
 */
@API(since = "0.x", status = MAINTAINED)
public interface ControllerUnderTest<T> {

    /**
     * Returns the model of the controller (see {@link RemotingBean})
     *
     * @return the model
     */
    T getModel();

    /**
     * Invokes an action on the controller. See {@link RemotingAction}
     *
     * @param actionName
     * @param params
     */
    void invoke(String actionName, Param... params);

    void invoke(String actionName, Map<String, Object> params);

    CommunicationMonitor createMonitor();

    /**
     * Destroys the controller
     */
    void destroy();

    <S> ControllerUnderTest<S> createController(String childControllerName);
}
