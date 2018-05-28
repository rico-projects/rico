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
package dev.rico.internal.client.remoting.legacy.communication;

import dev.rico.internal.client.remoting.legacy.ClientAttribute;
import dev.rico.internal.client.remoting.legacy.ClientModelStore;
import dev.rico.internal.client.remoting.legacy.ClientPresentationModel;
import dev.rico.internal.client.remoting.legacy.ModelSynchronizer;
import dev.rico.internal.remoting.legacy.core.Attribute;
import dev.rico.internal.remoting.legacy.core.PresentationModel;
import org.apiguardian.api.API;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import static org.apiguardian.api.API.Status.DEPRECATED;

@API(since = "0.x", status = DEPRECATED)
public class AttributeChangeListener implements PropertyChangeListener {

    private final ClientModelStore clientModelStore;

    private final ModelSynchronizer modelSynchronizer;

    public AttributeChangeListener(final ClientModelStore clientModelStore, final ModelSynchronizer modelSynchronizer) {
        this.clientModelStore = clientModelStore;
        this.modelSynchronizer = modelSynchronizer;
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(Attribute.VALUE_NAME)) {
            if (evt.getOldValue() == null && evt.getNewValue() == null || evt.getOldValue() != null && evt.getNewValue() != null && evt.getOldValue().equals(evt.getNewValue())) {
                return;
            }

            if (isSendable(evt)) {
                modelSynchronizer.onPropertyChanged(evt);
            }

            List<ClientAttribute> attributes = clientModelStore.findAllAttributesByQualifier(((Attribute) evt.getSource()).getQualifier());
            for (ClientAttribute attribute : attributes) {
                attribute.setValue(evt.getNewValue());
            }

        } else {
            // we assume the change is on a metadata property such as qualifier
            if (isSendable(evt)) {
                modelSynchronizer.onMetadataChanged(evt);
            }
        }
    }

    private boolean isSendable(final PropertyChangeEvent evt) {
        PresentationModel pmOfAttribute = ((Attribute) evt.getSource()).getPresentationModel();
        if (pmOfAttribute == null) {
            return true;
        }

        if (pmOfAttribute instanceof ClientPresentationModel && ((ClientPresentationModel) pmOfAttribute).isClientSideOnly()) {
            return false;
        }

        return true;
    }

}
