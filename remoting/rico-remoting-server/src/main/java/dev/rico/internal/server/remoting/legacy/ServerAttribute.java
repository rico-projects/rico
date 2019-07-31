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
package dev.rico.internal.server.remoting.legacy;


import dev.rico.internal.remoting.legacy.LegacyConstants;
import dev.rico.internal.remoting.legacy.communication.AttributeMetadataChangedCommand;
import dev.rico.internal.remoting.legacy.core.BaseAttribute;
import org.apiguardian.api.API;

import java.util.List;
import java.util.Objects;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
@Deprecated
public class ServerAttribute extends BaseAttribute {

    private boolean notifyClient = true;

    public ServerAttribute(final String propertyName, final Object initialValue) {
        super(propertyName, initialValue);
    }

    public ServerAttribute(final String propertyName, final Object baseValue, final String qualifier) {
        super(propertyName, baseValue, qualifier);
    }

    @Override
    public ServerPresentationModel getPresentationModel() {
        return (ServerPresentationModel) super.getPresentationModel();
    }

    @Override
    public void setValue(final Object newValue) {
        if (notifyClient) {
            ServerModelStore.changeValueCommand(getPresentationModel().getModelStore().getCurrentResponse(), this, newValue);
        }

        super.setValue(newValue);
        // on the server side, we have no listener on the model store to care for the distribution of
        // baseValue changes to all attributes of the same qualifier so we must care for that ourselves

        if (getQualifier() == null) {
            return;

        }

        if (getPresentationModel() == null) {
            return;

        }
        // we may not know the pm, yet
        for (ServerAttribute sameQualified : (List<ServerAttribute>)getPresentationModel().getModelStore().findAllAttributesByQualifier(getQualifier())) {
            if (sameQualified.equals(this)) {
                continue;
            }

            if (!Objects.equals(newValue, sameQualified.getValue())) {
                sameQualified.setValue(newValue);
            }
        }
    }

    @Override
    public void setQualifier(final String value) {
        super.setQualifier(value);
        if (notifyClient) {
            getPresentationModel().getModelStore().getCurrentResponse().add(new AttributeMetadataChangedCommand(getId(), BaseAttribute.QUALIFIER_NAME, value));
        }

    }

    public String getOrigin() {
        return LegacyConstants.SERVER_ORIGIN;
    }

    /**
     * Do the applyChange without creating commands that are sent to the client
     */
    public void silently(final Runnable applyChange) {
        boolean temp = notifyClient;
        notifyClient = false;
        try {
            applyChange.run();
        } finally {
            notifyClient = temp;
        }
    }

    /**
     * Do the applyChange with enforced creation of commands that are sent to the client
     */
    protected void verbosely(final Runnable applyChange) {
        boolean temp = notifyClient;
        notifyClient = true;
        try {
            applyChange.run();
        } finally {
            notifyClient = temp;
        }
    }

    /**
     * Overriding the standard behavior of PCLs such that firing is enforced to be done
     * verbosely. This is safe since on the server side PCLs are never used for the control
     * of the client notification as part of the infrastructure
     * (as opposed to the java client).
     * That is: all remaining PCLs are application specific and _must_ be called verbosely.
     */
    @Override
    protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
        verbosely(new Runnable() {
            @Override
            public void run() {
                ServerAttribute.super.firePropertyChange(propertyName, oldValue, newValue);
            }

        });
    }

}
