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
package dev.rico.server.remoting;

import dev.rico.internal.remoting.RemotingConstants;
import dev.rico.internal.remoting.converters.BeanConverterFactory;
import dev.rico.internal.remoting.legacy.LegacyConstants;
import dev.rico.internal.remoting.legacy.core.BasePresentationModel;
import dev.rico.internal.server.remoting.legacy.DTO;
import dev.rico.internal.server.remoting.legacy.ServerModelStore;
import dev.rico.internal.server.remoting.legacy.ServerPresentationModel;
import dev.rico.internal.server.remoting.legacy.Slot;
import dev.rico.server.remoting.util.AbstractRemotingTest;
import dev.rico.server.remoting.util.ListReferenceModel;
import dev.rico.server.remoting.util.SimpleTestModel;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class TestObservableListSync extends AbstractRemotingTest {

    private static class PresentationModelBuilder {

        private final String type;
        private final List<Slot> slots = new ArrayList<>();
        private final ServerModelStore serverModelStore;

        public PresentationModelBuilder(ServerModelStore serverModelStore, String type) {
            this.serverModelStore = serverModelStore;
            this.type = type;
            this.slots.add(new Slot(LegacyConstants.SOURCE_SYSTEM, LegacyConstants.SOURCE_SYSTEM_CLIENT));
        }

        public PresentationModelBuilder withAttribute(String name, Object value) {
            slots.add(new Slot(name, value));
            return this;
        }

        public BasePresentationModel create() {
            return serverModelStore.presentationModel(UUID.randomUUID().toString(), type, new DTO(slots));
        }

    }

    //////////////////////////////////////////////////////////////
    // Adding, removing, and replacing all element types as user
    //////////////////////////////////////////////////////////////
    @Test
    public void addingObjectElementAsUser_shouldAddElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final SimpleTestModel object = manager.create(SimpleTestModel.class);
        final BasePresentationModel objectModel = serverModelStore.findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);

        // when :
        model.getObjectList().add(object);

        // then :
        final List<ServerPresentationModel> changes = serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final BasePresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "objectList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), allOf(instanceOf(String.class), is((Object) objectModel.getId())));
    }

    @Test
    public void addingObjectNullAsUser_shouldAddElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        // when :
        model.getObjectList().add(null);

        // then :
        final List<ServerPresentationModel> changes = serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final BasePresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "objectList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), nullValue());
    }

    @Test
    public void addingPrimitiveElementAsUser_shouldAddElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String value = "Hello";

        // when :
        model.getPrimitiveList().add(value);

        // then :
        final List<ServerPresentationModel> changes = serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final BasePresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), allOf(instanceOf(String.class), is((Object) value)));
    }

    @Test
    public void addingPrimitiveNullAsUser_shouldAddElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        // when :
        model.getPrimitiveList().add(null);

        // then :
        final List<ServerPresentationModel> changes = serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final BasePresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), nullValue());
    }

    @Test
    public void deletingObjectElementAsUser_shouldDeleteElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final SimpleTestModel object = manager.create(SimpleTestModel.class);

        model.getObjectList().add(object);
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getObjectList().remove(0);

        // then :
        final List<ServerPresentationModel> changes = serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final BasePresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "objectList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }

    @Test
    public void deletingObjectNullAsUser_shouldDeleteElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getObjectList().add(null);
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getObjectList().remove(0);

        // then :
        final List<ServerPresentationModel> changes = serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final BasePresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "objectList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }

    @Test
    public void deletingPrimitiveElementAsUser_shouldDeleteElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().add("Hello");
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().remove(0);

        // then :
        final List<ServerPresentationModel> changes = serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final BasePresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }

    @Test
    public void deletingPrimitiveNullAsUser_shouldDeleteElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().add(null);
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().remove(0);

        // then :
        final List<ServerPresentationModel> changes = serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final BasePresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }

    @Test
    public void replaceObjectElementAsUser_shouldReplaceElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final SimpleTestModel newObject = manager.create(SimpleTestModel.class);
        final BasePresentationModel newObjectModel = serverModelStore.findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);
        final SimpleTestModel oldObject = manager.create(SimpleTestModel.class);

        model.getObjectList().add(oldObject);
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getObjectList().set(0, newObject);

        // then :
        final List<ServerPresentationModel> changes = serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final BasePresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "objectList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), allOf(instanceOf(String.class), is((Object) newObjectModel.getId())));
    }

    @Test
    public void replaceObjectElementWithNullAsUser_shouldReplaceElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final SimpleTestModel oldObject = manager.create(SimpleTestModel.class);

        model.getObjectList().add(oldObject);
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getObjectList().set(0, null);

        // then :
        final List<ServerPresentationModel> changes = serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final BasePresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "objectList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), nullValue());
    }

    @Test
    public void replaceObjectNullWithElementAsUser_shouldReplaceElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final SimpleTestModel newObject = manager.create(SimpleTestModel.class);
        final BasePresentationModel newObjectModel = serverModelStore.findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);

        model.getObjectList().add(null);
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getObjectList().set(0, newObject);

        // then :
        final List<ServerPresentationModel> changes = serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final BasePresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "objectList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), allOf(instanceOf(String.class), is((Object) newObjectModel.getId())));
    }

    @Test
    public void replacePrimitiveElementAsUser_shouldReplaceElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "Goodbye World";

        model.getPrimitiveList().add("Hello World");
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().set(0, newValue);

        // then :
        final List<ServerPresentationModel> changes = serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final BasePresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), allOf(instanceOf(String.class), is((Object) newValue)));
    }

    @Test
    public void replacePrimitiveElementWithNullAsUser_shouldReplaceElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().add("Hello World");
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().set(0, null);

        // then :
        final List<ServerPresentationModel> changes = serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final BasePresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), nullValue());
    }

    @Test
    public void replacePrimitiveNullWithElementAsUser_shouldReplaceElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "Goodbye World";

        model.getPrimitiveList().add(null);
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().set(0, newValue);

        // then :
        final List<ServerPresentationModel> changes = serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final BasePresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), allOf(instanceOf(String.class), is((Object) newValue)));
    }


    //////////////////////////////////////////////////////////////
    // Adding elements at different positions as user
    //////////////////////////////////////////////////////////////
    @Test
    public void addingMultipleElementInEmptyListAsUser_shouldAddElements() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String[] newElement = new String[]{"42", "4711", "Hello World"};

        // when :
        model.getPrimitiveList().addAll(0, Arrays.asList(newElement));

        // then :
        final List<ServerPresentationModel> changes = serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final BasePresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 3)));
        assertThat(change.getAttribute("0").getValue(), is((Object) "42"));
        assertThat(change.getAttribute("1").getValue(), is((Object) "4711"));
        assertThat(change.getAttribute("2").getValue(), is((Object) "Hello World"));
    }

    @Test
    public void addingSingleElementInBeginningAsUser_shouldAddElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newElement = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().add(0, newElement);

        // then :
        final List<ServerPresentationModel> changes = serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final BasePresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), is((Object) newElement));
    }

    @Test
    public void addingMultipleElementInBeginningAsUser_shouldAddElements() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String[] newElement = new String[]{"42", "4711", "Hello World"};

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().addAll(0, Arrays.asList(newElement));

        // then :
        final List<ServerPresentationModel> changes = serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final BasePresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 3)));
        assertThat(change.getAttribute("0").getValue(), is((Object) "42"));
        assertThat(change.getAttribute("1").getValue(), is((Object) "4711"));
        assertThat(change.getAttribute("2").getValue(), is((Object) "Hello World"));
    }

    @Test
    public void addingSingleElementInMiddleAsUser_shouldAddElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newElement = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().add(1, newElement);

        // then :
        final List<ServerPresentationModel> changes = serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final BasePresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), is((Object) newElement));
    }

    @Test
    public void addingMultipleElementInMiddleAsUser_shouldAddElements() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String[] newElement = new String[]{"42", "4711", "Hello World"};

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().addAll(1, Arrays.asList(newElement));

        // then :
        final List<ServerPresentationModel> changes = serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final BasePresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 3)));
        assertThat(change.getAttribute("0").getValue(), is((Object) "42"));
        assertThat(change.getAttribute("1").getValue(), is((Object) "4711"));
        assertThat(change.getAttribute("2").getValue(), is((Object) "Hello World"));
    }

    @Test
    public void addingSingleElementAtEndAsUser_shouldAddElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newElement = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().add(newElement);

        // then :
        final List<ServerPresentationModel> changes = serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final BasePresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 3)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 3)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), is((Object) newElement));
    }

    @Test
    public void addingMultipleElementAtEndAsUser_shouldAddElements() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String[] newElement = new String[]{"42", "4711", "Hello World"};

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().addAll(Arrays.asList(newElement));

        // then :
        final List<ServerPresentationModel> changes = serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final BasePresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 3)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 3)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 3)));
        assertThat(change.getAttribute("0").getValue(), is((Object) "42"));
        assertThat(change.getAttribute("1").getValue(), is((Object) "4711"));
        assertThat(change.getAttribute("2").getValue(), is((Object) "Hello World"));
    }

    private void removeAllPresentationModelsOfType(ServerModelStore serverModelStore, String type) {
        List<ServerPresentationModel> toRemove = new ArrayList<>();
        for(ServerPresentationModel model : serverModelStore.findAllPresentationModelsByType(type)) {
            toRemove.add(model);
        }
        for(ServerPresentationModel model : toRemove) {
            serverModelStore.remove(model);
        }
    }


    //////////////////////////////////////////////////////////////
    // Removing elements from different positions as user
    //////////////////////////////////////////////////////////////
    @Test
    public void deletingSingleElementInBeginningAsUser_shouldRemoveElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().remove(0);

        // then :
        final List<ServerPresentationModel> changes = serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final BasePresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }

    // TODO: Enable once ObservableArrayList.sublist() was implemented completely
    @Test(enabled = false)
    public void deletingMultipleElementInBeginningAsUser_shouldRemoveElements() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4", "5", "6"));
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().subList(0, 3).clear();

        // then :
        final List<ServerPresentationModel> changes = serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final BasePresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 3)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }

    @Test
    public void deletingSingleElementInMiddleAsUser_shouldDeleteElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().remove(1);

        // then :
        final List<ServerPresentationModel> changes = serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final BasePresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 2)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }

    // TODO: Enable once ObservableArrayList.sublist() was implemented completely
    @Test(enabled = false)
    public void deletingMultipleElementInMiddleAsUser_shouldDeleteElements() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4", "5", "6"));
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().subList(1, 4).clear();

        // then :
        final List<ServerPresentationModel> changes = serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final BasePresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 4)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }

    @Test
    public void deletingSingleElementAtEndAsUser_shouldDeleteElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().remove(2);

        // then :
        final List<ServerPresentationModel> changes = serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final BasePresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 2)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 3)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }

    // TODO: Enable once ObservableArrayList.sublist() was implemented completely
    @Test(enabled = false)
    public void deletingMultipleElementAtEndAsUser_shouldAddElements() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4", "5", "6"));
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().subList(3, 6).clear();

        // then :
        final List<ServerPresentationModel> changes = serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final BasePresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 3)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 6)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }


    //////////////////////////////////////////////////////////////
    // Replacing elements from different positions as user
    //////////////////////////////////////////////////////////////
    @Test
    public void replacingSingleElementAtBeginningAsUser_shouldReplaceElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().set(0, newValue);

        // then :
        final List<ServerPresentationModel> changes = serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final BasePresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), allOf(instanceOf(String.class), is((Object) newValue)));
    }

    @Test
    public void replacingSingleElementInMiddleAsUser_shouldReplaceElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().set(1, newValue);

        // then :
        final List<ServerPresentationModel> changes = serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final BasePresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 2)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), allOf(instanceOf(String.class), is((Object) newValue)));
    }

    @Test
    public void replacingSingleElementAtEndAsUser_shouldReplaceElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().set(2, newValue);

        // then :
        final List<ServerPresentationModel> changes = serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final BasePresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 2)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 3)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), allOf(instanceOf(String.class), is((Object) newValue)));
    }


    ///////////////////////////////////////////////////////////////////
    // Adding, removing, and replacing all element types
    ///////////////////////////////////////////////////////////////////
    @Test
    public void addingObjectElementFromserverModelStore_shouldAddElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final BasePresentationModel classDescription = serverModelStore.findAllPresentationModelsByType(RemotingConstants.REMOTING_BEAN).get(0);
        classDescription.getAttribute("objectList").setValue(BeanConverterFactory.FIELD_TYPE_REMOTING_BEAN);
        final SimpleTestModel object = manager.create(SimpleTestModel.class);
        final BasePresentationModel objectModel = serverModelStore.findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);

        // when :
        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", objectModel.getId())
                .create();

        // then :
        assertThat(model.getObjectList(), is(Collections.singletonList(object)));
        assertThat(serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingObjectNullFromRemoting_shouldAddElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final BasePresentationModel classDescription = serverModelStore.findAllPresentationModelsByType(RemotingConstants.REMOTING_BEAN).get(0);
        classDescription.getAttribute("objectList").setValue(BeanConverterFactory.FIELD_TYPE_REMOTING_BEAN);

        // when :
        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", null)
                .create();

        // then :
        assertThat(model.getObjectList(), is(Collections.<SimpleTestModel>singletonList(null)));
        assertThat(serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingPrimitiveElementFromRemoting_shouldAddElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String value = "Hello";

        // when :
        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", value)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Collections.singletonList(value)));
        assertThat(serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingPrimitiveNullFromRemoting_shouldAddElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        // when :
        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", null)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Collections.<String>singletonList(null)));
        assertThat(serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingObjectElementFromRemoting_shouldDeleteElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        manager.create(SimpleTestModel.class);
        final BasePresentationModel objectModel = serverModelStore.findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);

        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", objectModel.getId())
                .create();
        assertThat(model.getObjectList(), hasSize(1));

        // when :
        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getObjectList(), empty());
        assertThat(serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingObjectNullFromserverModelStore_shouldDeleteElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", null)
                .create();
        assertThat(model.getObjectList(), hasSize(1));

        // when :
        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getObjectList(), empty());
        assertThat(serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingPrimitiveElementFromRemoting_shouldDeleteElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String value = "Hello";

        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", value)
                .create();
        assertThat(model.getPrimitiveList(), hasSize(1));

        // when :
        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), empty());
        assertThat(serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingPrimitiveNullFromRemoting_shouldDeleteElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", null)
                .create();
        assertThat(model.getPrimitiveList(), hasSize(1));

        // when :
        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), empty());
        assertThat(serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingObjectElementFromRemoting_shouldReplaceElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final BasePresentationModel classDescription = serverModelStore.findAllPresentationModelsByType(RemotingConstants.REMOTING_BEAN).get(0);
        classDescription.getAttribute("objectList").setValue(BeanConverterFactory.FIELD_TYPE_REMOTING_BEAN);
        final SimpleTestModel oldObject = manager.create(SimpleTestModel.class);
        final BasePresentationModel oldObjectModel = serverModelStore.findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);
        final SimpleTestModel newObject = manager.create(SimpleTestModel.class);
        final List<ServerPresentationModel> models = serverModelStore.findAllPresentationModelsByType(SimpleTestModel.class.getName());
        final BasePresentationModel newObjectModel = oldObjectModel == models.get(1) ? models.get(0) : models.get(1);

        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", oldObjectModel.getId())
                .create();
        assertThat(model.getObjectList(), is(Collections.singletonList(oldObject)));

        // when :
        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("pos", 0)
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 1)
                .withAttribute("0", newObjectModel.getId())
                .create();

        // then :
        assertThat(model.getObjectList(), is(Collections.singletonList(newObject)));
        assertThat(serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingObjectElementWithNullFromRemoting_shouldReplaceElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final BasePresentationModel classDescription = serverModelStore.findAllPresentationModelsByType(RemotingConstants.REMOTING_BEAN).get(0);
        classDescription.getAttribute("objectList").setValue(BeanConverterFactory.FIELD_TYPE_REMOTING_BEAN);
        final SimpleTestModel oldObject = manager.create(SimpleTestModel.class);
        final BasePresentationModel oldObjectModel = serverModelStore.findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);

        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", oldObjectModel.getId())
                .create();
        assertThat(model.getObjectList(), is(Collections.singletonList(oldObject)));

        // when :
        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 1)
                .withAttribute("0", null)
                .create();

        // then :
        assertThat(model.getObjectList(), is(Collections.<SimpleTestModel>singletonList(null)));
        assertThat(serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingObjectNullWithElementFromRemoting_shouldReplaceElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final BasePresentationModel classDescription = serverModelStore.findAllPresentationModelsByType(RemotingConstants.REMOTING_BEAN).get(0);
        classDescription.getAttribute("objectList").setValue(BeanConverterFactory.FIELD_TYPE_REMOTING_BEAN);
        final SimpleTestModel newObject = manager.create(SimpleTestModel.class);
        final BasePresentationModel newObjectModel = serverModelStore.findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);

        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", null)
                .create();
        assertThat(model.getObjectList(), is(Collections.<SimpleTestModel>singletonList(null)));

        // when :
        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 1)
                .withAttribute("0", newObjectModel.getId())
                .create();

        // then :
        assertThat(model.getObjectList(), is(Collections.singletonList(newObject)));
        assertThat(serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingPrimitiveElementFromRemoting_shouldReplaceElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String oldValue = "Hello";
        final String newValue = "Goodbye";

        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", oldValue)
                .create();
        assertThat(model.getPrimitiveList(), is(Collections.singletonList(oldValue)));

        // when :
        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 1)
                .withAttribute("0", newValue)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Collections.singletonList(newValue)));
        assertThat(serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingPrimitiveElementWithNullFromRemoting_shouldReplaceElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String oldValue = "Hello";

        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", oldValue)
                .create();
        assertThat(model.getPrimitiveList(), is(Collections.singletonList(oldValue)));

        // when :
        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 1)
                .withAttribute("0", null)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Collections.<String>singletonList(null)));
        assertThat(serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingPrimitiveNullWithElementFromRemoting_shouldReplaceElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "Goodbye";

        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", null)
                .create();
        assertThat(model.getPrimitiveList(), is(Collections.<String>singletonList(null)));

        // when :
        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 1)
                .withAttribute("0", newValue)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Collections.singletonList(newValue)));
        assertThat(serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }


    //////////////////////////////////////////////////////////////
    // Adding elements at different positions
    //////////////////////////////////////////////////////////////
    @Test
    public void addingMultipleElementsInEmptyListFromRemoting_shouldAddElements() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        // when :
        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 3)
                .withAttribute("0", "42")
                .withAttribute("1", "4711")
                .withAttribute("2", "Hello World")
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("42", "4711", "Hello World")));
        assertThat(serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingSingleElementInBeginningFromRemoting_shouldAddElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newElement = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", newElement)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList(newElement, "1", "2", "3")));
        assertThat(serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingMultipleElementsInBeginningFromRemoting_shouldAddElements() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 3)
                .withAttribute("0", "42")
                .withAttribute("1", "4711")
                .withAttribute("2", "Hello World")
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("42", "4711", "Hello World", "1", "2", "3")));
        assertThat(serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingSingleElementInMiddleFromRemoting_shouldAddElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newElement = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 1)
                .withAttribute("to", 1)
                .withAttribute("count", 1)
                .withAttribute("0", newElement)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", newElement, "2", "3")));
        assertThat(serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingMultipleElementsInMiddleFromRemoting_shouldAddElements() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 1)
                .withAttribute("to", 1)
                .withAttribute("count", 3)
                .withAttribute("0", "42")
                .withAttribute("1", "4711")
                .withAttribute("2", "Hello World")
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "42", "4711", "Hello World", "2", "3")));
        assertThat(serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingSingleElementAtEndFromRemoting_shouldAddElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newElement = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 3)
                .withAttribute("to", 3)
                .withAttribute("count", 1)
                .withAttribute("0", newElement)
                .create();

        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "2", "3", newElement)));
        assertThat(serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingMultipleElementsAtEndFromRemoting_shouldAddElements() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 3)
                .withAttribute("to", 3)
                .withAttribute("count", 3)
                .withAttribute("0", "42")
                .withAttribute("1", "4711")
                .withAttribute("2", "Hello World")
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "2", "3", "42", "4711", "Hello World")));
        assertThat(serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }


    //////////////////////////////////////////////////////////////
    // Removing elements from different positions
    //////////////////////////////////////////////////////////////
    @Test
    public void deletingSingleElementInBeginningFromRemoting_shouldRemoveElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("2", "3")));
        assertThat(serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingMultipleElementInBeginningFromRemoting_shouldRemoveElements() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4", "5", "6"));
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 3)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("4", "5", "6")));
        assertThat(serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingSingleElementInMiddleFromRemoting_shouldDeleteElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 1)
                .withAttribute("to", 2)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "3")));
        assertThat(serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingMultipleElementInMiddleFromRemoting_shouldRemoveElements() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4", "5", "6"));
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 2)
                .withAttribute("to", 4)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "2", "5", "6")));
        assertThat(serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingSingleElementAtEndFromRemoting_shouldDeleteElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 2)
                .withAttribute("to", 3)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "2")));
        assertThat(serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingMultipleElementAtEndFromRemoting_shouldRemoveElements() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4", "5", "6"));
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 4)
                .withAttribute("to", 6)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "2", "3", "4")));
        assertThat(serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }


    //////////////////////////////////////////////////////////////
    // Replacing elements from different positions
    //////////////////////////////////////////////////////////////
    @Test
    public void replacingSingleElementInBeginningFromRemoting_shouldReplaceElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 1)
                .withAttribute("0", newValue)
                .create();

        assertThat(model.getPrimitiveList(), is(Arrays.asList("42", "2", "3")));
        assertThat(serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingMultipleElementsInBeginningFromRemoting_shouldReplaceElements() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4"));
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 2)
                .withAttribute("count", 3)
                .withAttribute("0", "42")
                .withAttribute("1", "4711")
                .withAttribute("2", "Hello World")
                .create();

        assertThat(model.getPrimitiveList(), is(Arrays.asList("42", "4711", "Hello World", "3", "4")));
        assertThat(serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingSingleElementInMiddleFromRemoting_shouldReplaceElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 1)
                .withAttribute("to", 2)
                .withAttribute("count", 1)
                .withAttribute("0", newValue)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "42", "3")));
        assertThat(serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingMultipleElementsInMiddleFromRemoting_shouldReplaceElements() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4"));
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 1)
                .withAttribute("to", 3)
                .withAttribute("count", 3)
                .withAttribute("0", "42")
                .withAttribute("1", "4711")
                .withAttribute("2", "Hello World")
                .create();

        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "42", "4711", "Hello World", "4")));
        assertThat(serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingSingleElementAtEndFromRemoting_shouldReplaceElement() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 2)
                .withAttribute("to", 3)
                .withAttribute("count", 1)
                .withAttribute("0", newValue)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "2", "42")));
        assertThat(serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }


    @Test
    public void replacingMultipleElementsAtEndFromRemoting_shouldReplaceElements() {
        // given :
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final BasePresentationModel sourceModel = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4"));
        removeAllPresentationModelsOfType(serverModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(serverModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 2)
                .withAttribute("to", 4)
                .withAttribute("count", 3)
                .withAttribute("0", "42")
                .withAttribute("1", "4711")
                .withAttribute("2", "Hello World")
                .create();

        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "2", "42", "4711", "Hello World")));
        assertThat(serverModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }
}
