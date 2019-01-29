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
package dev.rico.internal.client.remoting.legacy.communication;

import dev.rico.internal.client.remoting.legacy.ClientAttribute;
import dev.rico.internal.client.remoting.legacy.ClientModelStore;
import dev.rico.internal.client.remoting.legacy.ClientPresentationModel;
import dev.rico.internal.remoting.legacy.communication.AttributeMetadataChangedCommand;
import dev.rico.internal.remoting.legacy.communication.Command;
import dev.rico.internal.remoting.legacy.communication.CreatePresentationModelCommand;
import dev.rico.internal.remoting.legacy.communication.DeletePresentationModelCommand;
import dev.rico.internal.remoting.legacy.communication.ValueChangedCommand;
import dev.rico.internal.remoting.legacy.core.Attribute;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.apiguardian.api.API.Status.DEPRECATED;

@API(since = "0.x", status = DEPRECATED)
public class ClientResponseHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ClientResponseHandler.class);

    private final ClientModelStore clientModelStore;

    public ClientResponseHandler(final ClientModelStore clientModelStore) {
        this.clientModelStore = Objects.requireNonNull(clientModelStore);
    }

    public void dispatchHandle(final Command command) {
        if (command instanceof DeletePresentationModelCommand) {
            handleDeletePresentationModelCommand((DeletePresentationModelCommand) command);
        } else if (command instanceof CreatePresentationModelCommand) {
            handleCreatePresentationModelCommand((CreatePresentationModelCommand) command);
        } else if (command instanceof ValueChangedCommand) {
            handleValueChangedCommand((ValueChangedCommand) command);
        } else if (command instanceof AttributeMetadataChangedCommand) {
            handleAttributeMetadataChangedCommand((AttributeMetadataChangedCommand) command);
        } else {
            LOG.error("C: cannot withoutResult unknown command '{}'", command );
        }

    }

    private void handleDeletePresentationModelCommand(final DeletePresentationModelCommand serverCommand) {
        ClientPresentationModel model = clientModelStore.findPresentationModelById(serverCommand.getPmId());
        if (model == null) {
            return;
        }
        clientModelStore.delete(model);
    }

    private void handleCreatePresentationModelCommand(final CreatePresentationModelCommand serverCommand) {
        if (clientModelStore.containsPresentationModel(serverCommand.getPmId())) {
            throw new IllegalStateException("There already is a presentation model with id '" + serverCommand.getPmId() + "' known to the client.");
        }

        List<ClientAttribute> attributes = new ArrayList<ClientAttribute>();
        for (Map<String, Object> attr : serverCommand.getAttributes()) {

            Object propertyName = attr.get("propertyName");
            Object value = attr.get("value");
            Object qualifier = attr.get("qualifier");
            Object id = attr.get("id");

            ClientAttribute attribute = new ClientAttribute(propertyName != null ? propertyName.toString() : null, value, qualifier != null ? qualifier.toString() : null);
            if (id != null && id.toString().endsWith("S")) {
                attribute.setId(id.toString());
            }

            attributes.add(attribute);
        }

        ClientPresentationModel model = new ClientPresentationModel(serverCommand.getPmId(), attributes);
        model.setPresentationModelType(serverCommand.getPmType());
        if (serverCommand.isClientSideOnly()) {
            model.setClientSideOnly(true);
        }

        clientModelStore.add(model);
        clientModelStore.updateQualifiers(model);
    }

    private void handleValueChangedCommand(final ValueChangedCommand serverCommand) {
        final ClientAttribute attribute = clientModelStore.findAttributeById(serverCommand.getAttributeId());
        if (attribute == null) {
            LOG.warn("C: attribute with id '{}' not found, cannot update to new value '{}'", serverCommand.getAttributeId() , serverCommand.getNewValue() );
            return;
        }

        if (Objects.equals(attribute.getValue(), serverCommand.getNewValue())) {
            return;
        }

        LOG.trace("C: updating '{}' id '{}' from '{}' to '{}' ", attribute.getPropertyName(), serverCommand.getAttributeId(), attribute.getValue(), serverCommand.getNewValue());
        attribute.setValue(serverCommand.getNewValue());
    }

    private void handleAttributeMetadataChangedCommand(final AttributeMetadataChangedCommand serverCommand) {
        ClientAttribute attribute = clientModelStore.findAttributeById(serverCommand.getAttributeId());
        if (attribute == null) {
            return;
        }

        if (serverCommand.getMetadataName() != null && serverCommand.getMetadataName().equals(Attribute.VALUE_NAME)) {
            attribute.setValue(serverCommand.getValue());
        }

        if (serverCommand.getMetadataName() != null && serverCommand.getMetadataName().equals(Attribute.QUALIFIER_NAME)) {
            if (serverCommand.getValue() == null) {
                attribute.setQualifier(null);
            } else {
                attribute.setQualifier(serverCommand.getValue().toString());
            }
        }
    }

}
