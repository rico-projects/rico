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
package dev.rico.client.remoting.impl;

import dev.rico.client.remoting.util.AbstractRemotingTest;
import dev.rico.internal.client.remoting.RicoCommandHandler;
import dev.rico.internal.client.remoting.legacy.ClientAttribute;
import dev.rico.internal.client.remoting.legacy.ClientModelStore;
import dev.rico.internal.remoting.legacy.communication.Command;
import dev.rico.internal.server.remoting.legacy.ServerModelStore;
import dev.rico.internal.server.remoting.legacy.action.AbstractServerAction;
import dev.rico.internal.server.remoting.legacy.communication.ActionRegistry;
import dev.rico.internal.server.remoting.legacy.communication.CommandHandler;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.testng.Assert.assertEquals;

public class CommandHandlerTest extends AbstractRemotingTest {

    private final class TestChangeCommand extends Command {
        public TestChangeCommand() {
            super(TestChangeCommand.class.getSimpleName());
        }
    }

    @Test
    public void testInvocation() throws Exception {
        //Given:
        final TestConfiguration configuration = createTestConfiguration();
        final ServerModelStore serverModelStore = configuration.getServerModelStore();
        final ClientModelStore clientModelStore = configuration.getClientModelStore();
        final RicoCommandHandler commandHandler = new RicoCommandHandler(configuration.getClientConnector());
        final String modelId = UUID.randomUUID().toString();
        clientModelStore.createModel(modelId, null, new ClientAttribute("myAttribute", "UNKNOWN"));
        configuration.getServerConnector().register(new AbstractServerAction() {
            @Override
            public void registerIn(ActionRegistry registry) {
                registry.register(TestChangeCommand.class, new CommandHandler() {
                    @Override
                    public void handleCommand(Command command, List response) {
                        serverModelStore.findPresentationModelById(modelId).getAttribute("myAttribute").setValue("Hello World");
                    }
                });
            }
        });

        //When:
        commandHandler.invokeCommand(new TestChangeCommand()).get();

        //Then:
        assertEquals(clientModelStore.findPresentationModelById(modelId).getAttribute("myAttribute").getValue(), "Hello World");
    }

}
