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

import dev.rico.internal.remoting.legacy.communication.Command;
import dev.rico.internal.remoting.legacy.core.ModelStoreEvent;
import dev.rico.internal.remoting.legacy.core.ModelStoreListener;
import dev.rico.internal.remoting.legacy.core.PresentationModel;
import dev.rico.internal.remoting.server.legacy.DefaultServerDolphin;
import dev.rico.internal.remoting.server.legacy.ServerAttribute;
import dev.rico.internal.remoting.server.legacy.ServerDolphinFactory;
import dev.rico.internal.remoting.server.legacy.ServerPresentationModel;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings({"unchecked", "deprecation"})
public class ServerDolphinTest {

    private DefaultServerDolphin serverInstance;

    @BeforeMethod
    public void setUp() throws Exception {
        serverInstance = ((DefaultServerDolphin) (ServerDolphinFactory.create()));
        serverInstance.getModelStore().setCurrentResponse(new ArrayList<>());
    }

    @Test
    public void testListPresentationModels() {
        Assert.assertTrue(serverInstance.getModelStore().listPresentationModelIds().isEmpty());
        Assert.assertTrue(serverInstance.getModelStore().listPresentationModels().isEmpty());
        Assert.assertTrue(serverInstance.getModelStore().findAllAttributesByQualifier("no-such-qualifier").isEmpty());
        Assert.assertTrue(serverInstance.getModelStore().findAllPresentationModelsByType("no-such-type").isEmpty());

        ServerPresentationModel pm1 = new ServerPresentationModel("first", new ArrayList(), serverInstance.getModelStore());
        serverInstance.getModelStore().add(pm1);

        Assert.assertEquals(Collections.singleton("first"), serverInstance.getModelStore().listPresentationModelIds());
        Assert.assertEquals(1, serverInstance.getModelStore().listPresentationModelIds().size());
        Assert.assertEquals(pm1, serverInstance.getModelStore().listPresentationModels().iterator().next());

        ServerPresentationModel pm2 = new ServerPresentationModel("second", new ArrayList(), serverInstance.getModelStore());
        serverInstance.getModelStore().add(pm2);

        Assert.assertEquals(2, serverInstance.getModelStore().listPresentationModelIds().size());
        Assert.assertEquals(2, serverInstance.getModelStore().listPresentationModels().size());


        for (String id : serverInstance.getModelStore().listPresentationModelIds()) {
            PresentationModel model = serverInstance.getModelStore().findPresentationModelById(id);
            Assert.assertNotNull(model);
            Assert.assertTrue(serverInstance.getModelStore().listPresentationModels().contains(model));
        }


    }

    @Test
    public void testAddRemoveModelStoreListener() {
        final AtomicInteger typedListenerCallCount = new AtomicInteger(0);
        final AtomicInteger listenerCallCount = new AtomicInteger(0);
        ModelStoreListener listener = new ModelStoreListener() {
            @Override
            public void modelStoreChanged(ModelStoreEvent event) {
                listenerCallCount.incrementAndGet();
            }

        };
        ModelStoreListener typedListener = new ModelStoreListener() {
            @Override
            public void modelStoreChanged(ModelStoreEvent event) {
                typedListenerCallCount.incrementAndGet();
            }

        };
        serverInstance.getModelStore().addModelStoreListener("person", typedListener);
        serverInstance.getModelStore().addModelStoreListener(listener);
        serverInstance.getModelStore().add(new ServerPresentationModel("p1", Collections.<ServerAttribute>emptyList(), serverInstance.getModelStore()));
        ServerPresentationModel modelWithType = new ServerPresentationModel("person1", Collections.<ServerAttribute>emptyList(), serverInstance.getModelStore());
        modelWithType.setPresentationModelType("person");
        serverInstance.getModelStore().add(modelWithType);
        serverInstance.getModelStore().add(new ServerPresentationModel("p2", Collections.<ServerAttribute>emptyList(), serverInstance.getModelStore()));
        serverInstance.getModelStore().removeModelStoreListener("person", typedListener);
        serverInstance.getModelStore().removeModelStoreListener(listener);
        Assert.assertEquals(3, listenerCallCount.get());
        Assert.assertEquals(1, typedListenerCallCount.get());
    }

    @Test
    public void testAddModelStoreListenerWithClosure() {
        final AtomicInteger typedListenerCallCount = new AtomicInteger(0);
        final AtomicInteger listenerCallCount = new AtomicInteger(0);
        ModelStoreListener listener = new ModelStoreListener() {
            @Override
            public void modelStoreChanged(ModelStoreEvent event) {
                listenerCallCount.incrementAndGet();
            }

        };
        ModelStoreListener typedListener = new ModelStoreListener() {
            @Override
            public void modelStoreChanged(ModelStoreEvent event) {
                typedListenerCallCount.incrementAndGet();
            }

        };
        serverInstance.getModelStore().addModelStoreListener("person", typedListener);
        serverInstance.getModelStore().addModelStoreListener(listener);
        serverInstance.getModelStore().add(new ServerPresentationModel("p1", Collections.<ServerAttribute>emptyList(), serverInstance.getModelStore()));
        ServerPresentationModel modelWithType = new ServerPresentationModel("person1", Collections.<ServerAttribute>emptyList(), serverInstance.getModelStore());
        modelWithType.setPresentationModelType("person");
        serverInstance.getModelStore().add(modelWithType);
        serverInstance.getModelStore().add(new ServerPresentationModel("p2", Collections.<ServerAttribute>emptyList(), serverInstance.getModelStore()));
        Assert.assertEquals(3, listenerCallCount.get());
        Assert.assertEquals(1, typedListenerCallCount.get());
    }

    @Test
    public void testHasModelStoreListener() {
        ModelStoreListener listener = getListener();
        Assert.assertFalse(serverInstance.getModelStore().hasModelStoreListener(null));
        Assert.assertFalse(serverInstance.getModelStore().hasModelStoreListener(listener));
        serverInstance.getModelStore().addModelStoreListener(listener);
        Assert.assertTrue(serverInstance.getModelStore().hasModelStoreListener(listener));
        listener = getListener();
        serverInstance.getModelStore().addModelStoreListener("person", listener);
        Assert.assertFalse(serverInstance.getModelStore().hasModelStoreListener("car", listener));
        Assert.assertTrue(serverInstance.getModelStore().hasModelStoreListener("person", listener));
    }

    @Test
    public void testRegisterDefaultActions() {
        serverInstance.getServerConnector().registerDefaultActions();
        int numDefaultActions = serverInstance.getServerConnector().getRegistrationCount();

        // multiple calls should not lead to multiple initializations
        serverInstance.getServerConnector().registerDefaultActions();
        Assert.assertEquals(numDefaultActions, serverInstance.getServerConnector().getRegistrationCount());
    }

    private ModelStoreListener getListener() {
        return new ModelStoreListener() {
            @Override
            public void modelStoreChanged(ModelStoreEvent event) {
                // do nothing
            }

        };
    }
}
