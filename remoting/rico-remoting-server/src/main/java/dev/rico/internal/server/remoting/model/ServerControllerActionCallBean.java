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
package dev.rico.internal.server.remoting.model;

import dev.rico.remoting.converter.ValueConverterException;
import dev.rico.internal.remoting.AbstractControllerActionCallBean;
import dev.rico.internal.remoting.Converters;
import dev.rico.internal.remoting.MappingException;
import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.legacy.core.Attribute;
import dev.rico.internal.remoting.legacy.core.PresentationModel;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ServerControllerActionCallBean extends AbstractControllerActionCallBean {

    private final Converters converters;
    private final PresentationModel pm;

    public ServerControllerActionCallBean(Converters converters, PresentationModel pm) {
        this.converters = Assert.requireNonNull(converters, "converters");
        this.pm = Assert.requireNonNull(pm, "pm");
    }

    public String getControllerId() {
        return (String) pm.getAttribute(CONTROLLER_ID).getValue();
    }

    public String getActionName() {
        return (String) pm.getAttribute(ACTION_NAME).getValue();
    }

    public void setError(boolean error) {
        pm.getAttribute(ERROR_CODE).setValue(error);
    }

    public Object getParam(String name, Class<?> type) {
        final String internalName = PARAM_PREFIX + name;
        final Attribute valueAttribute = pm.getAttribute(internalName);
        if (valueAttribute == null) {
            throw new IllegalArgumentException(String.format("Invoking RemotingAction requires parameter '%s', but it was not withContent", name));
        }
        try {
            return converters.getConverter(type).convertFromRemoting(valueAttribute.getValue());
        } catch (ValueConverterException e) {
            throw new MappingException("Error in conversion", e);
        }
    }
}
