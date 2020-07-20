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
package dev.rico.internal.remoting.server.legacy.action;

import dev.rico.core.logging.Logger;
import dev.rico.core.logging.LoggerFactory;
import dev.rico.internal.remoting.legacy.LegacyConstants;
import dev.rico.internal.remoting.legacy.communication.CreatePresentationModelCommand;
import dev.rico.internal.remoting.server.legacy.ServerAttribute;
import dev.rico.internal.remoting.server.legacy.ServerModelStore;
import dev.rico.internal.remoting.server.legacy.ServerPresentationModel;
import dev.rico.internal.remoting.server.legacy.communication.ActionRegistry;
import dev.rico.internal.remoting.server.legacy.communication.CommandHandler;
import org.apiguardian.api.API;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class CreatePresentationModelAction extends AbstractServerAction {

    private static final Logger LOG = LoggerFactory.getLogger(CreatePresentationModelAction.class);

    public void registerIn(final ActionRegistry registry) {

        registry.register(CreatePresentationModelCommand.class, new CommandHandler<CreatePresentationModelCommand>() {
            @Override
            public void handleCommand(final CreatePresentationModelCommand command, final List response) {
                createPresentationModel(command, getServerModelStore());
            }
        });
    }

    private static void createPresentationModel(final CreatePresentationModelCommand command, final ServerModelStore serverModelStore) {
        if (serverModelStore.findPresentationModelById(command.getPmId()) != null) {
            LOG.trace("Ignoring create PM '{}' since it is already in the model store.", command.getPmId());
            return;
        }

        if (command.getPmId().endsWith(LegacyConstants.SERVER_PM_AUTO_ID_SUFFIX)) {
            LOG.trace("Creating the PM '{}' with reserved server-auto-suffix.", command.getPmId());
        }

        List<ServerAttribute> attributes = new LinkedList();
        for (Map<String, Object> attr : command.getAttributes()) {
            ServerAttribute attribute = new ServerAttribute((String) attr.get("propertyName"), attr.get("value"), (String) attr.get("qualifier"));
            attribute.setId((String) attr.get("id"));
            attributes.add(attribute);
        }

        ServerPresentationModel model = new ServerPresentationModel(command.getPmId(), attributes, serverModelStore);
        model.setPresentationModelType(command.getPmType());
        serverModelStore.checkClientAdded(model);
    }

}
