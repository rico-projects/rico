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
package dev.rico.client.remoting;

import dev.rico.client.remoting.util.AbstractRemotingTest;
import dev.rico.client.remoting.util.ChildModel;
import dev.rico.client.remoting.util.ListReferenceModel;
import dev.rico.client.remoting.util.SimpleAnnotatedTestModel;
import dev.rico.client.remoting.util.SimpleTestModel;
import dev.rico.client.remoting.util.SingleReferenceModel;
import dev.rico.internal.client.remoting.legacy.ClientModelStore;
import dev.rico.internal.client.remoting.legacy.ClientPresentationModel;
import dev.rico.internal.client.remoting.legacy.communication.AbstractClientConnector;
import dev.rico.internal.remoting.BeanDefinitionException;
import dev.rico.internal.remoting.BeanRepository;
import dev.rico.internal.remoting.EventDispatcher;
import dev.rico.remoting.BeanManager;
import mockit.Mocked;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class TestModelDeletion extends AbstractRemotingTest {

    @Test
    public void testWithAnnotatedSimpleModel(@Mocked AbstractClientConnector connector) {
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final EventDispatcher dispatcher = createEventDispatcher(clientModelStore);
        final BeanRepository repository = createBeanRepository(clientModelStore, dispatcher);
        final BeanManager manager = createBeanManager(clientModelStore, repository, dispatcher);

        SimpleAnnotatedTestModel model = manager.create(SimpleAnnotatedTestModel.class);

        repository.delete(model);

        List<ClientPresentationModel> remotingModels = clientModelStore.findAllPresentationModelsByType("simple_test_model");
        assertThat(remotingModels, empty());

        Collection<ClientPresentationModel> allremotingModels = clientModelStore.listPresentationModels();
        assertThat(allremotingModels, hasSize(1));

        assertThat(repository.isManaged(model), is(false));
    }

    @Test
    public void testWithSimpleModel(@Mocked AbstractClientConnector connector) {
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final EventDispatcher dispatcher = createEventDispatcher(clientModelStore);
        final BeanRepository repository = createBeanRepository(clientModelStore, dispatcher);
        final BeanManager manager = createBeanManager(clientModelStore, repository, dispatcher);

        SimpleTestModel model = manager.create(SimpleTestModel.class);

        repository.delete(model);

        List<ClientPresentationModel> remotingModels = clientModelStore.findAllPresentationModelsByType(SimpleTestModel.class.getName());
        assertThat(remotingModels, empty());

        Collection<ClientPresentationModel> allremotingModels = clientModelStore.listPresentationModels();
        assertThat(allremotingModels, hasSize(1));

        assertThat(repository.isManaged(model), is(false));
    }

    @Test(expectedExceptions = BeanDefinitionException.class)
    public void testWithWrongModelType(@Mocked AbstractClientConnector connector) {
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final EventDispatcher dispatcher = createEventDispatcher(clientModelStore);
        final BeanRepository repository = createBeanRepository(clientModelStore, dispatcher);
        final BeanManager manager = createBeanManager(clientModelStore, repository, dispatcher);

        repository.delete("I'm a String");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testWithNull(@Mocked AbstractClientConnector connector) {
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final EventDispatcher dispatcher = createEventDispatcher(clientModelStore);
        final BeanRepository repository = createBeanRepository(clientModelStore, dispatcher);
        final BeanManager manager = createBeanManager(clientModelStore, repository, dispatcher);

        repository.delete(null);
    }

    @Test
    public void testWithSingleReferenceModel(@Mocked AbstractClientConnector connector) {
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final EventDispatcher dispatcher = createEventDispatcher(clientModelStore);
        final BeanRepository repository = createBeanRepository(clientModelStore, dispatcher);
        final BeanManager manager = createBeanManager(clientModelStore, repository, dispatcher);

        SingleReferenceModel model = manager.create(SingleReferenceModel.class);

        repository.delete(model);

        List<ClientPresentationModel> remotingModels = clientModelStore.findAllPresentationModelsByType(SingleReferenceModel.class.getName());
        assertThat(remotingModels, empty());

        Collection<ClientPresentationModel> allremotingModels = clientModelStore.listPresentationModels();
        assertThat(allremotingModels, hasSize(1));

        assertThat(repository.isManaged(model), is(false));
    }

    @Test
    public void testWithListReferenceModel(@Mocked AbstractClientConnector connector) {
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final EventDispatcher dispatcher = createEventDispatcher(clientModelStore);
        final BeanRepository repository = createBeanRepository(clientModelStore, dispatcher);
        final BeanManager manager = createBeanManager(clientModelStore, repository, dispatcher);

        ListReferenceModel model = manager.create(ListReferenceModel.class);

        repository.delete(model);

        List<ClientPresentationModel> remotingModels = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName());
        assertThat(remotingModels, empty());

        Collection<ClientPresentationModel> allremotingModels = clientModelStore.listPresentationModels();
        assertThat(allremotingModels, hasSize(1));    //Class Repository wurde bereits angelegt

        assertThat(repository.isManaged(model), is(false));
    }

    @Test
    public void testWithInheritedModel(@Mocked AbstractClientConnector connector) {
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final EventDispatcher dispatcher = createEventDispatcher(clientModelStore);
        final BeanRepository repository = createBeanRepository(clientModelStore, dispatcher);
        final BeanManager manager = createBeanManager(clientModelStore, repository, dispatcher);

        ChildModel model = manager.create(ChildModel.class);

        repository.delete(model);

        List<ClientPresentationModel> remotingModels = clientModelStore.findAllPresentationModelsByType(ChildModel.class.getName());
        assertThat(remotingModels, empty());

        Collection<ClientPresentationModel> allremotingModels = clientModelStore.listPresentationModels();
        assertThat(allremotingModels, hasSize(1));

        assertThat(repository.isManaged(model), is(false));
    }


}

