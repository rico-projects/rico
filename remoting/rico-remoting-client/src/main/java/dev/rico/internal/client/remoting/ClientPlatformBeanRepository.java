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
package dev.rico.internal.client.remoting;

import dev.rico.client.remoting.Param;
import dev.rico.internal.remoting.Converters;
import dev.rico.internal.remoting.InternalAttributesBean;
import dev.rico.internal.remoting.BeanRepository;
import dev.rico.internal.remoting.RemotingEventHandler;
import dev.rico.internal.remoting.EventDispatcher;
import dev.rico.internal.remoting.legacy.core.PresentationModel;
import dev.rico.internal.client.remoting.legacy.ClientModelStore;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ClientPlatformBeanRepository {

    private final ClientModelStore modelStore;
    private final Converters converters;

    private InternalAttributesBean internalAttributesBean;

    public ClientPlatformBeanRepository(final ClientModelStore modelStore, final BeanRepository beanRepository, final EventDispatcher dispatcher, final Converters converters) {
        this.modelStore = modelStore;
        this.converters = converters;

        dispatcher.onceInternalAttributesBeanAddedHandler(new RemotingEventHandler() {
            @Override
            public void onEvent(PresentationModel model) {
                internalAttributesBean = new InternalAttributesBean(beanRepository, model);
            }
        });
    }

    public ClientControllerActionCallBean createControllerActionCallBean(final String controllerId, final String actionName, final Param... params) {
        return new ClientControllerActionCallBean(modelStore, converters, controllerId, actionName, params);
    }

    public InternalAttributesBean getInternalAttributesBean() {
        if (internalAttributesBean == null) {
            throw new IllegalStateException("InternalAttributesBean was not initialized yet");
        }
        return internalAttributesBean;
    }
}
