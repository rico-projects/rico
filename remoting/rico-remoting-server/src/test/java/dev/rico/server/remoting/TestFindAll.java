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

import dev.rico.internal.server.remoting.legacy.ServerModelStore;
import dev.rico.server.remoting.util.AbstractRemotingTest;
import dev.rico.server.remoting.util.SimpleAnnotatedTestModel;
import dev.rico.server.remoting.util.SimpleTestModel;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TestFindAll extends AbstractRemotingTest {

    @Test
    public void testWithSimpleModel() {
        ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        SimpleTestModel model1 = manager.create(SimpleTestModel.class);
        SimpleTestModel model2 = manager.create(SimpleTestModel.class);
        SimpleTestModel model3 = manager.create(SimpleTestModel.class);

        manager.create(SimpleAnnotatedTestModel.class);

        List<SimpleTestModel> models = manager.findAll(SimpleTestModel.class);
        assertThat(models, is(Arrays.asList(model1, model2, model3)));
    }
}
