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
package dev.rico.internal.client.remoting.legacy;

import dev.rico.internal.client.remoting.legacy.communication.AttributeChangeListener;
import dev.rico.internal.remoting.legacy.core.ModelStore;
import dev.rico.internal.remoting.legacy.core.ModelStoreConfig;
import org.apiguardian.api.API;

import java.util.Arrays;
import java.util.List;

import static org.apiguardian.api.API.Status.DEPRECATED;

/**
 * The ClientModelStore is a {@link ModelStore} with customized behavior appropriate to the client
 * (view) side of a connection.  It connects the model store with the {@link ClientDolphin} via
 * an {@link AttributeChangeListener}.  It automatically notifies the server side when presentation models are added
 * or removed.
 */
@API(since = "0.x", status = DEPRECATED)
public class ClientModelStore extends ModelStore<ClientAttribute, ClientPresentationModel> {

    private final ModelSynchronizer modelSynchronizer;

    protected final AttributeChangeListener attributeChangeListener;

    /**
     * Constructs a client model store with default capacities.
     * @see ModelStoreConfig
     */
    public ClientModelStore(final ModelSynchronizer modelSynchronizer) {
        super(new ModelStoreConfig());
        this.modelSynchronizer = modelSynchronizer;
        attributeChangeListener = new AttributeChangeListener(this, modelSynchronizer);
    }


    @Override
    public boolean add(final ClientPresentationModel model) {
        final boolean success = super.add(model);
        if (success) {
            final List<ClientAttribute> attributes = model.getAttributes();
            for (final ClientAttribute attribute : attributes) {
                attribute.addPropertyChangeListener(attributeChangeListener);
            }
            if (!model.isClientSideOnly()) {
                modelSynchronizer.onAdded(model);
            }
        }
        return success;
    }

    public void delete(final ClientPresentationModel model) {
        delete(model, true);
    }

    public void delete(final ClientPresentationModel model, boolean notify) {
        if (model == null) return;
        if (containsPresentationModel(model.getId())) {
            remove(model);
            if (!notify) return;
            if (model.isClientSideOnly()) return;
            modelSynchronizer.onDeleted(model);
        }
    }

    @Override
    public boolean remove(final ClientPresentationModel model) {
        boolean success = super.remove(model);
        for (final ClientAttribute attribute : model.getAttributes()) {
            attribute.removePropertyChangeListener(attributeChangeListener);
        }
        return success;
    }

    @Override
    @Deprecated
    public void registerAttribute(final ClientAttribute attribute) {
        super.registerAttribute(attribute);
        attribute.addPropertyChangeListener(attributeChangeListener);
    }


    public ClientPresentationModel createModel(final String id, final String presentationModelType, final ClientAttribute... attributes) {
        final ClientPresentationModel result = new ClientPresentationModel(id, Arrays.asList(attributes));
        result.setPresentationModelType(presentationModelType);
        add(result);
        return result;
    }

    @Deprecated
    public AttributeChangeListener getAttributeChangeListener() {
        return attributeChangeListener;
    }
}
