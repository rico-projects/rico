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
package dev.rico.integrationtests.server.enterprise;

import dev.rico.integrationtests.enterprise.EnterpriseTestBean;
import dev.rico.server.remoting.RemotingController;
import dev.rico.server.remoting.RemotingModel;
import dev.rico.server.remoting.event.RemotingEventBus;
import dev.rico.server.remoting.event.Topic;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import static dev.rico.integrationtests.enterprise.EnterpriseTestConstants.ENTERPRISE_CONTROLLER_NAME;

@RemotingController(ENTERPRISE_CONTROLLER_NAME)
public class EnterpriseTestController {

    @RemotingModel
    private EnterpriseTestBean model;

    @Inject
    private RemotingEventBus eventBus;

    @PostConstruct
    private void init() {
        model.setPostConstructCalled(true);

        model.setEventBusInjected(false);
        if(eventBus != null) {
            try {
                eventBus.publish(Topic.create(), "test-data");
                model.setEventBusInjected(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @PreDestroy
    private void destroy() {
        model.setPreDestroyCalled(true);
    }
}
