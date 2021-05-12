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
import dev.rico.internal.remoting.legacy.communication.ValueChangedCommand;
import dev.rico.internal.remoting.server.legacy.ServerAttribute;
import dev.rico.internal.remoting.server.legacy.communication.ActionRegistry;
import dev.rico.internal.remoting.server.legacy.communication.CommandHandler;
import org.apiguardian.api.API;

import java.util.List;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class StoreValueChangeAction extends AbstractServerAction {

    private static final Logger LOG = LoggerFactory.getLogger(StoreValueChangeAction.class);

    public void registerIn(final ActionRegistry registry) {
        registry.register(ValueChangedCommand.class, new CommandHandler<ValueChangedCommand>() {
            @Override
            public void handleCommand(final ValueChangedCommand command, final List response) {
                final ServerAttribute attribute = getServerModelStore().findAttributeById(command.getAttributeId());
                if (attribute != null) {
                    attribute.silently(new Runnable() {
                        @Override
                        public void run() {
                            attribute.setValue(command.getNewValue());
                        }

                    });
                } else {
                    LOG.error("S: cannot find attribute with id '{}' to change value from to '{}'.", command.getAttributeId(), command.getNewValue());
                }
            }
        });
    }
}
