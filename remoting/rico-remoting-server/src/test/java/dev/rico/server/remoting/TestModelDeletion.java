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
package dev.rico.server.remoting;

import dev.rico.internal.remoting.BeanDefinitionException;
import dev.rico.internal.remoting.BeanRepository;
import dev.rico.internal.remoting.EventDispatcher;
import dev.rico.internal.server.remoting.legacy.ServerModelStore;
import dev.rico.internal.server.remoting.legacy.ServerPresentationModel;
import dev.rico.server.remoting.util.AbstractRemotingTest;
import dev.rico.server.remoting.util.ChildModel;
import dev.rico.server.remoting.util.ListReferenceModel;
import dev.rico.server.remoting.util.SimpleAnnotatedTestModel;
import dev.rico.server.remoting.util.SimpleTestModel;
import dev.rico.server.remoting.util.SingleReferenceModel;
import dev.rico.remoting.BeanManager;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class TestModelDeletion extends AbstractRemotingTest {

    @Test
    public void testWithAnnotatedSimpleModel() {
        final ServerModelStore serverModelStore = createServerModelStore();
        final EventDispatcher dispatcher = createEventDispatcher(serverModelStore);
        final BeanRepository beanRepository = createBeanRepository(serverModelStore, dispatcher);
        final BeanManager manager = createBeanManager(serverModelStore, beanRepository, dispatcher);

        SimpleAnnotatedTestModel model = manager.create(SimpleAnnotatedTestModel.class);

        beanRepository.delete(model);

        List<ServerPresentationModel> remotingModels = serverModelStore.findAllPresentationModelsByType(SimpleAnnotatedTestModel.class.getName());
        assertThat(remotingModels, empty());

        Collection<ServerPresentationModel> allremotingModels = serverModelStore.listPresentationModels();
        assertThat(allremotingModels, hasSize(1));

        assertThat(beanRepository.isManaged(model), is(false));
    }

    @Test
    public void testWithSimpleModel() {
        final ServerModelStore serverModelStore = createServerModelStore();
        final EventDispatcher dispatcher = createEventDispatcher(serverModelStore);
        final BeanRepository beanRepository = createBeanRepository(serverModelStore, dispatcher);
        final BeanManager manager = createBeanManager(serverModelStore, beanRepository, dispatcher);

        SimpleTestModel model = manager.create(SimpleTestModel.class);

        beanRepository.delete(model);

        List<ServerPresentationModel> remotingModels = serverModelStore.findAllPresentationModelsByType(SimpleTestModel.class.getName());
        assertThat(remotingModels, empty());

        Collection<ServerPresentationModel> allremotingModels = serverModelStore.listPresentationModels();
        assertThat(allremotingModels, hasSize(1));

        assertThat(beanRepository.isManaged(model), is(false));
    }

    @Test
    public void testWithSingleReferenceModel() {
        final ServerModelStore serverModelStore = createServerModelStore();
        final EventDispatcher dispatcher = createEventDispatcher(serverModelStore);
        final BeanRepository beanRepository = createBeanRepository(serverModelStore, dispatcher);
        final BeanManager manager = createBeanManager(serverModelStore, beanRepository, dispatcher);

        SingleReferenceModel model = manager.create(SingleReferenceModel.class);

        beanRepository.delete(model);

        List<ServerPresentationModel> remotingModels = serverModelStore.findAllPresentationModelsByType(SingleReferenceModel.class.getName());
        assertThat(remotingModels, empty());

        Collection<ServerPresentationModel> allremotingModels = serverModelStore.listPresentationModels();
        assertThat(allremotingModels, hasSize(1));

        assertThat(beanRepository.isManaged(model), is(false));
    }

    @Test(expectedExceptions = BeanDefinitionException.class)
    public void testWithWrongModelType() {
        final ServerModelStore serverModelStore = createServerModelStore();
        final EventDispatcher dispatcher = createEventDispatcher(serverModelStore);
        final BeanRepository beanRepository = createBeanRepository(serverModelStore, dispatcher);
        final BeanManager manager = createBeanManager(serverModelStore, beanRepository, dispatcher);

        beanRepository.delete("I'm a String");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testWithNull() {
        final ServerModelStore serverModelStore = createServerModelStore();
        final EventDispatcher dispatcher = createEventDispatcher(serverModelStore);
        final BeanRepository beanRepository = createBeanRepository(serverModelStore, dispatcher);
        final BeanManager manager = createBeanManager(serverModelStore, beanRepository, dispatcher);

        beanRepository.delete(null);
    }

    @Test
    public void testWithListReferenceModel() {
        final ServerModelStore serverModelStore = createServerModelStore();
        final EventDispatcher dispatcher = createEventDispatcher(serverModelStore);
        final BeanRepository beanRepository = createBeanRepository(serverModelStore, dispatcher);
        final BeanManager manager = createBeanManager(serverModelStore, beanRepository, dispatcher);

        ListReferenceModel model = manager.create(ListReferenceModel.class);

        beanRepository.delete(model);

        List<ServerPresentationModel> remotingModels = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName());
        assertThat(remotingModels, empty());

        Collection<ServerPresentationModel> allremotingModels = serverModelStore.listPresentationModels();
        assertThat(allremotingModels, hasSize(1));    //Class Repository wurde bereits angelegt

        assertThat(beanRepository.isManaged(model), is(false));
    }

    @Test
    public void testWithInheritedModel() {
        final ServerModelStore serverModelStore = createServerModelStore();
        final EventDispatcher dispatcher = createEventDispatcher(serverModelStore);
        final BeanRepository beanRepository = createBeanRepository(serverModelStore, dispatcher);
        final BeanManager manager = createBeanManager(serverModelStore, beanRepository, dispatcher);

        ChildModel model = manager.create(ChildModel.class);

        beanRepository.delete(model);

        List<ServerPresentationModel> remotingModels = serverModelStore.findAllPresentationModelsByType(ChildModel.class.getName());
        assertThat(remotingModels, empty());

        Collection<ServerPresentationModel> allremotingModels = serverModelStore.listPresentationModels();
        assertThat(allremotingModels, hasSize(1));

        assertThat(beanRepository.isManaged(model), is(false));
    }


}

