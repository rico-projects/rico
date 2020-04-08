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

import dev.rico.internal.remoting.codec.OptimizedJsonCodec;
import dev.rico.internal.remoting.legacy.communication.ChangeAttributeMetadataCommand;
import dev.rico.internal.remoting.legacy.communication.CreatePresentationModelCommand;
import dev.rico.internal.remoting.legacy.communication.PresentationModelDeletedCommand;
import dev.rico.internal.remoting.legacy.communication.ValueChangedCommand;
import dev.rico.internal.server.remoting.legacy.DefaultServerDolphin;
import dev.rico.internal.server.remoting.legacy.ServerConnector;
import dev.rico.internal.server.remoting.legacy.ServerModelStore;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@SuppressWarnings("deprecation")
public class LegacyFactoryTest {

    @Test
    public void testCreation() {

        final ServerModelStore modelStore = new ServerModelStore();
        final ServerConnector serverConnector = new ServerConnector();
        serverConnector.setCodec(OptimizedJsonCodec.getInstance());
        serverConnector.setServerModelStore(modelStore);
        final DefaultServerDolphin serverInstance = new DefaultServerDolphin(modelStore, serverConnector);
        serverInstance.getServerConnector().registerDefaultActions();

        assertNotNull(serverInstance);
        assertNotNull(serverInstance.getModelStore());
        assertNotNull(serverInstance.getServerConnector());
        assertNotNull(serverInstance.getModelStore());
        assertNotNull(serverInstance.getServerConnector().getCodec());
        assertEquals(OptimizedJsonCodec.class, serverInstance.getServerConnector().getCodec().getClass());

        assertEquals(serverInstance.getServerConnector().getRegistry().getActions().size(), 4);
        assertTrue(serverInstance.getServerConnector().getRegistry().getActions().containsKey(ValueChangedCommand.class));
        assertTrue(serverInstance.getServerConnector().getRegistry().getActions().containsKey(CreatePresentationModelCommand.class));
        assertTrue(serverInstance.getServerConnector().getRegistry().getActions().containsKey(ChangeAttributeMetadataCommand.class));
        assertTrue(serverInstance.getServerConnector().getRegistry().getActions().containsKey(PresentationModelDeletedCommand.class));

        assertEquals(serverInstance.getModelStore().listPresentationModelIds().size(), 0);
    }
}
