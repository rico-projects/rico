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
import dev.rico.remoting.converter.ValueConverterException;
import dev.rico.internal.remoting.AbstractControllerActionCallBean;
import dev.rico.internal.remoting.Converters;
import dev.rico.internal.remoting.RemotingConstants;
import dev.rico.internal.remoting.MappingException;
import dev.rico.internal.core.Assert;
import dev.rico.internal.client.remoting.legacy.ClientModelStore;
import dev.rico.internal.client.remoting.legacy.ClientPresentationModel;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ClientControllerActionCallBean extends AbstractControllerActionCallBean {

    private final ClientModelStore modelStore;
    private ClientPresentationModel pm;

    public ClientControllerActionCallBean(final ClientModelStore modelStore, final Converters converters, final String controllerId, final String actionName, final Param... params) {
        this.modelStore = Assert.requireNonNull(modelStore, "modelStore");

        final ClientPresentationModelBuilder builder = new ClientPresentationModelBuilder(modelStore);
        builder.withType(RemotingConstants.CONTROLLER_ACTION_CALL_BEAN_NAME)
                .withAttribute(CONTROLLER_ID, controllerId)
                .withAttribute(ACTION_NAME, actionName)
                .withAttribute(ERROR_CODE);

        for (final Param param : params) {
            final Object value = param.getValue();
            final Object remotingValue;
            try {
                remotingValue = value != null? converters.getConverter(value.getClass()).convertToRemoting(value) : null;
            } catch (ValueConverterException e) {
               throw new MappingException("Error in value conversion", e);
            }
            final String paramName = PARAM_PREFIX + param.getName();
            builder.withAttribute(paramName, remotingValue);
        }

        this.pm = builder.create();
    }

    public boolean isError() {
        if (pm == null) {
            throw new IllegalStateException("ClientControllerActionCallBean was already unregistered");
        }
        return Boolean.TRUE.equals(pm.getAttribute(ERROR_CODE).getValue());
    }

    @SuppressWarnings("unchecked")
    public void unregister() {
        if (pm != null) {
            modelStore.remove(pm);
            pm = null;
        }
    }

}
