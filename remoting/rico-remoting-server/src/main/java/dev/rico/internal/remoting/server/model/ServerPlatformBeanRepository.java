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
package dev.rico.internal.remoting.server.model;

import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.BeanRepository;
import dev.rico.internal.remoting.Converters;
import dev.rico.internal.remoting.RemotingEventHandler;
import dev.rico.internal.remoting.EventDispatcher;
import dev.rico.internal.remoting.InternalAttributesBean;
import dev.rico.internal.remoting.RemotingConstants;
import dev.rico.internal.remoting.legacy.core.PresentationModel;
import dev.rico.internal.remoting.server.legacy.ServerModelStore;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ServerPlatformBeanRepository {

    private ServerControllerActionCallBean controllerActionCallBean;

    private final InternalAttributesBean internalAttributesBean;

    public ServerPlatformBeanRepository(ServerModelStore serverModelStore, BeanRepository beanRepository, EventDispatcher dispatcher, final Converters converters) {
        Assert.requireNonNull(dispatcher, "dispatcher");
        dispatcher.addControllerActionCallBeanAddedHandler(new RemotingEventHandler() {
            @Override
            public void onEvent(PresentationModel model) {
                final String type = model.getPresentationModelType();
                switch (type) {
                    case RemotingConstants.CONTROLLER_ACTION_CALL_BEAN_NAME:
                        controllerActionCallBean = new ServerControllerActionCallBean(converters, model);
                        break;
                }
            }
        });

        dispatcher.addControllerActionCallBeanRemovedHandler(new RemotingEventHandler() {
            @Override
            public void onEvent(PresentationModel model) {
                final String type = model.getPresentationModelType();
                switch (type) {
                    case RemotingConstants.CONTROLLER_ACTION_CALL_BEAN_NAME:
                        controllerActionCallBean = null;
                        break;
                }
            }
        });

        internalAttributesBean = new InternalAttributesBean(beanRepository, new ServerPresentationModelBuilder(serverModelStore));
    }

    public ServerControllerActionCallBean getControllerActionCallBean() {
        return controllerActionCallBean;
    }

    public InternalAttributesBean getInternalAttributesBean() {
        return internalAttributesBean;
    }
}
