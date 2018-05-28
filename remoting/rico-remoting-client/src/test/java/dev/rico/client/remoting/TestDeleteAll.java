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
package dev.rico.client.remoting;

import dev.rico.client.remoting.util.AbstractRemotingTest;
import dev.rico.client.remoting.util.SimpleAnnotatedTestModel;
import dev.rico.client.remoting.util.SimpleTestModel;
import dev.rico.internal.client.remoting.legacy.ClientModelStore;
import dev.rico.internal.client.remoting.legacy.ClientPresentationModel;
import dev.rico.internal.client.remoting.legacy.communication.AbstractClientConnector;
import dev.rico.internal.remoting.BeanRepository;
import dev.rico.internal.remoting.EventDispatcher;
import dev.rico.remoting.BeanManager;
import mockit.Mocked;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class TestDeleteAll extends AbstractRemotingTest {

    @Test
    public void testWithSimpleModel(@Mocked AbstractClientConnector connector) {
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final EventDispatcher dispatcher = createEventDispatcher(clientModelStore);
        final BeanRepository repository = createBeanRepository(clientModelStore, dispatcher);
        final BeanManager manager = createBeanManager(clientModelStore, repository, dispatcher);


        SimpleTestModel model1 = manager.create(SimpleTestModel.class);
        SimpleTestModel model2 = manager.create(SimpleTestModel.class);
        SimpleTestModel model3 = manager.create(SimpleTestModel.class);

        SimpleAnnotatedTestModel wrongModel = manager.create(SimpleAnnotatedTestModel.class);

        for (Object bean : manager.findAll(SimpleTestModel.class)) {
            repository.delete(bean);
        }

        assertThat(repository.isManaged(model1), is(false));
        assertThat(repository.isManaged(model2), is(false));
        assertThat(repository.isManaged(model3), is(false));
        assertThat(repository.isManaged(wrongModel), is(true));

        List<ClientPresentationModel> testModels = clientModelStore.findAllPresentationModelsByType("dev.rico.client.remoting.util.SimpleTestModel");
        assertThat(testModels, hasSize(0));

    }
}

