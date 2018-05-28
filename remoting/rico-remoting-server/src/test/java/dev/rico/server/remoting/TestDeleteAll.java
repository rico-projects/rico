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

import dev.rico.internal.remoting.BeanRepository;
import dev.rico.internal.remoting.EventDispatcher;
import dev.rico.internal.server.remoting.legacy.ServerModelStore;
import dev.rico.internal.server.remoting.legacy.ServerPresentationModel;
import dev.rico.server.remoting.util.AbstractRemotingTest;
import dev.rico.server.remoting.util.SimpleAnnotatedTestModel;
import dev.rico.server.remoting.util.SimpleTestModel;
import dev.rico.remoting.BeanManager;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class TestDeleteAll extends AbstractRemotingTest {

    @Test
    public void testWithSimpleModel() {
        final ServerModelStore serverModelStore = createServerModelStore();
        final EventDispatcher dispatcher = createEventDispatcher(serverModelStore);
        final BeanRepository beanRepository = createBeanRepository(serverModelStore, dispatcher);
        final BeanManager manager = createBeanManager(serverModelStore, beanRepository, dispatcher);

        SimpleTestModel model1 = manager.create(SimpleTestModel.class);
        SimpleTestModel model2 = manager.create(SimpleTestModel.class);
        SimpleTestModel model3 = manager.create(SimpleTestModel.class);

        SimpleAnnotatedTestModel wrongModel = manager.create(SimpleAnnotatedTestModel.class);

        for (final Object bean : manager.findAll(SimpleTestModel.class)) {
            beanRepository.delete(bean);
        }

        assertThat(beanRepository.isManaged(model1), is(false));
        assertThat(beanRepository.isManaged(model2), is(false));
        assertThat(beanRepository.isManaged(model3), is(false));
        assertThat(beanRepository.isManaged(wrongModel), is(true));

        List<ServerPresentationModel> testModels = serverModelStore.findAllPresentationModelsByType(SimpleTestModel.class.getSimpleName());
        assertThat(testModels, hasSize(0));

    }
}

