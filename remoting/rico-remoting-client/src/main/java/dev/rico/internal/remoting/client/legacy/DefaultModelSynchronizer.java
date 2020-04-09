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
package dev.rico.internal.remoting.client.legacy;

import dev.rico.internal.remoting.client.legacy.communication.AbstractClientConnector;
import dev.rico.internal.remoting.legacy.communication.ChangeAttributeMetadataCommand;
import dev.rico.internal.remoting.legacy.communication.Command;
import dev.rico.internal.remoting.legacy.communication.CreatePresentationModelCommand;
import dev.rico.internal.remoting.legacy.communication.PresentationModelDeletedCommand;
import dev.rico.internal.remoting.legacy.communication.ValueChangedCommand;
import dev.rico.internal.remoting.legacy.core.Attribute;
import org.apiguardian.api.API;

import java.beans.PropertyChangeEvent;
import java.util.function.Supplier;

import static org.apiguardian.api.API.Status.DEPRECATED;

@API(since = "0.x", status = DEPRECATED)
public class DefaultModelSynchronizer implements ModelSynchronizer {

    private final Supplier<AbstractClientConnector> connectionProvider;

    public DefaultModelSynchronizer(final Supplier<AbstractClientConnector> connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public void onAdded(final ClientPresentationModel model) {
        final Command command = CreatePresentationModelCommand.makeFrom(model);
        send(command);
    }

    @Override
    public void onDeleted(final ClientPresentationModel model) {
        final Command command = new PresentationModelDeletedCommand(model.getId());
        send(command);
    }

    @Override
    public void onPropertyChanged(final PropertyChangeEvent evt) {
        final Command command = new ValueChangedCommand(((Attribute) evt.getSource()).getId(),evt.getNewValue());
        send(command);
    }

    @Override
    public void onMetadataChanged(final PropertyChangeEvent evt) {
        final Command command = new ChangeAttributeMetadataCommand(((Attribute) evt.getSource()).getId(), evt.getPropertyName(), evt.getNewValue());
        send(command);
    }

    private void send(final Command command) {
        final AbstractClientConnector clientConnector = connectionProvider.get();
        if(clientConnector == null) {
            throw new IllegalStateException("No connection defined!");
        }
        clientConnector.send(command);
    }
}
