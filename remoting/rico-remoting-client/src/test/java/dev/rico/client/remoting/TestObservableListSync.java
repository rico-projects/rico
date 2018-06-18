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
import dev.rico.client.remoting.util.ListReferenceModel;
import dev.rico.client.remoting.util.SimpleTestModel;
import dev.rico.internal.client.remoting.legacy.ClientAttribute;
import dev.rico.internal.client.remoting.legacy.ClientModelStore;
import dev.rico.internal.client.remoting.legacy.ClientPresentationModel;
import dev.rico.internal.remoting.RemotingConstants;
import dev.rico.internal.remoting.communication.converters.BeanConverterFactory;
import dev.rico.internal.remoting.legacy.LegacyConstants;
import dev.rico.internal.remoting.legacy.core.PresentationModel;
import dev.rico.remoting.BeanManager;
import mockit.Mocked;
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

        private final List<ClientAttribute> attributes = new ArrayList<>();
        final ClientModelStore clientModelStore;
        private final String type;

        public PresentationModelBuilder(final ClientModelStore clientModelStore, String type) {
            this.clientModelStore = clientModelStore;
            this.type = type;
            this.attributes.add(new ClientAttribute(LegacyConstants.SOURCE_SYSTEM, LegacyConstants.SOURCE_SYSTEM_SERVER));
        }

        public PresentationModelBuilder withAttribute(String name, Object value) {
            attributes.add(new ClientAttribute(name, value));
            return this;
        }

        public PresentationModel create() {
            return clientModelStore.createModel(UUID.randomUUID().toString(), type, attributes.toArray(new ClientAttribute[attributes.size()]));
        }

    }

    //////////////////////////////////////////////////////////////
    // Adding, removing, and replacing all element types as user
    //////////////////////////////////////////////////////////////
    @Test
    public void addingObjectElementAsUser_shouldAddElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final SimpleTestModel object = manager.create(SimpleTestModel.class);
        final PresentationModel objectModel = clientModelStore.findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);

        // when :
        model.getObjectList().add(object);

        // then :
        final List<ClientPresentationModel> changes = clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "objectList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), allOf(instanceOf(String.class), is((Object) objectModel.getId())));
    }

    @Test
    public void addingObjectNullAsUser_shouldAddElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        // when :
        model.getObjectList().add(null);

        // then :
        final List<ClientPresentationModel> changes = clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "objectList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), nullValue());
    }

    @Test
    public void addingPrimitiveElementAsUser_shouldAddElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String value = "Hello";

        // when :
        model.getPrimitiveList().add(value);

        // then :
        final List<ClientPresentationModel> changes = clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), allOf(instanceOf(String.class), is((Object) value)));
    }

    @Test
    public void addingPrimitiveNullAsUser_shouldAddElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        // when :
        model.getPrimitiveList().add(null);

        // then :
        final List<ClientPresentationModel> changes = clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), nullValue());
    }

    @Test
    public void deletingObjectElementAsUser_shouldDeleteElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final SimpleTestModel object = manager.create(SimpleTestModel.class);

        model.getObjectList().add(object);
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getObjectList().remove(0);

        // then :
        final List<ClientPresentationModel> changes = clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "objectList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }

    @Test
    public void deletingObjectNullAsUser_shouldDeleteElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getObjectList().add(null);
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getObjectList().remove(0);

        // then :
        final List<ClientPresentationModel> changes = clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "objectList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }

    @Test
    public void deletingPrimitiveElementAsUser_shouldDeleteElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().add("Hello");
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().remove(0);

        // then :
        final List<ClientPresentationModel> changes = clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }

    @Test
    public void deletingPrimitiveNullAsUser_shouldDeleteElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().add(null);
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().remove(0);

        // then :
        final List<ClientPresentationModel> changes = clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }

    @Test
    public void replaceObjectElementAsUser_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final SimpleTestModel newObject = manager.create(SimpleTestModel.class);
        final PresentationModel newObjectModel = clientModelStore.findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);
        final SimpleTestModel oldObject = manager.create(SimpleTestModel.class);

        model.getObjectList().add(oldObject);
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getObjectList().set(0, newObject);

        // then :
        final List<ClientPresentationModel> changes = clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "objectList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), allOf(instanceOf(String.class), is((Object) newObjectModel.getId())));
    }

    @Test
    public void replaceObjectElementWithNullAsUser_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final SimpleTestModel oldObject = manager.create(SimpleTestModel.class);

        model.getObjectList().add(oldObject);
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getObjectList().set(0, null);

        // then :
        final List<ClientPresentationModel> changes = clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "objectList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), nullValue());
    }

    @Test
    public void replaceObjectNullWithElementAsUser_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final SimpleTestModel newObject = manager.create(SimpleTestModel.class);
        final PresentationModel newObjectModel = clientModelStore.findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);

        model.getObjectList().add(null);
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getObjectList().set(0, newObject);

        // then :
        final List<ClientPresentationModel> changes = clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "objectList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), allOf(instanceOf(String.class), is((Object) newObjectModel.getId())));
    }

    @Test
    public void replacePrimitiveElementAsUser_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "Goodbye World";

        model.getPrimitiveList().add("Hello World");
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().set(0, newValue);

        // then :
        final List<ClientPresentationModel> changes = clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), allOf(instanceOf(String.class), is((Object) newValue)));
    }

    @Test
    public void replacePrimitiveElementWithNullAsUser_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().add("Hello World");
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().set(0, null);

        // then :
        final List<ClientPresentationModel> changes = clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), nullValue());
    }

    @Test
    public void replacePrimitiveNullWithElementAsUser_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "Goodbye World";

        model.getPrimitiveList().add(null);
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().set(0, newValue);

        // then :
        final List<ClientPresentationModel> changes = clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
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
    public void addingMultipleElementsInEmptyListAsUser_shouldAddElements(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String[] newElement = new String[]{"42", "4711", "Hello World"};

        // when :
        model.getPrimitiveList().addAll(0, Arrays.asList(newElement));

        // then :
        final List<ClientPresentationModel> changes = clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
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
    public void addingSingleElementInBeginningAsUser_shouldAddElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newElement = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().add(0, newElement);

        // then :
        final List<ClientPresentationModel> changes = clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), is((Object) newElement));
    }

    @Test
    public void addingMultipleElementsInBeginningAsUser_shouldAddElements(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String[] newElement = new String[]{"42", "4711", "Hello World"};

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().addAll(0, Arrays.asList(newElement));

        // then :
        final List<ClientPresentationModel> changes = clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
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
    public void addingSingleElementInMiddleAsUser_shouldAddElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newElement = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().add(1, newElement);

        // then :
        final List<ClientPresentationModel> changes = clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), is((Object) newElement));
    }

    @Test
    public void addingMultipleElementsInMiddleAsUser_shouldAddElements(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String[] newElement = new String[]{"42", "4711", "Hello World"};

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().addAll(1, Arrays.asList(newElement));

        // then :
        final List<ClientPresentationModel> changes = clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
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
    public void addingSingleElementAtEndAsUser_shouldAddElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newElement = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().add(newElement);

        // then :
        final List<ClientPresentationModel> changes = clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 3)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 3)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), is((Object) newElement));
    }

    @Test
    public void addingMultipleElementsAtEndAsUser_shouldAddElements(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String[] newElement = new String[]{"42", "4711", "Hello World"};

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().addAll(Arrays.asList(newElement));

        // then :
        final List<ClientPresentationModel> changes = clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 3)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 3)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 3)));
        assertThat(change.getAttribute("0").getValue(), is((Object) "42"));
        assertThat(change.getAttribute("1").getValue(), is((Object) "4711"));
        assertThat(change.getAttribute("2").getValue(), is((Object) "Hello World"));
    }


    //////////////////////////////////////////////////////////////
    // Removing elements from different positions as user
    //////////////////////////////////////////////////////////////
    @Test
    public void deletingSingleElementInBeginningAsUser_shouldRemoveElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().remove(0);

        // then :
        final List<ClientPresentationModel> changes = clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }

    // TODO: Enable once ObservableArrayList.sublist() was implemented completely
    @Test(enabled = false)
    public void deletingMultipleElementInBeginningAsUser_shouldRemoveElements(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4", "5", "6"));
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().subList(0, 3).clear();

        // then :
        final List<ClientPresentationModel> changes = clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 3)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }

    @Test
    public void deletingSingleElementInMiddleAsUser_shouldDeleteElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().remove(1);

        // then :
        final List<ClientPresentationModel> changes = clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 2)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }

    // TODO: Enable once ObservableArrayList.sublist() was implemented completely
    @Test(enabled = false)
    public void deletingMultipleElementInMiddleAsUser_shouldDeleteElements(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4", "5", "6"));
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().subList(1, 4).clear();

        // then :
        final List<ClientPresentationModel> changes = clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 4)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }

    @Test
    public void deletingSingleElementAtEndAsUser_shouldDeleteElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().remove(2);

        // then :
        final List<ClientPresentationModel> changes = clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 2)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 3)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
    }

    // TODO: Enable once ObservableArrayList.sublist() was implemented completely
    @Test(enabled = false)
    public void deletingMultipleElementAtEndAsUser_shouldAddElements(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4", "5", "6"));
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().subList(3, 6).clear();

        // then :
        final List<ClientPresentationModel> changes = clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
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
    public void replacingSingleElementAtBeginningAsUser_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().set(0, newValue);

        // then :
        final List<ClientPresentationModel> changes = clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 0)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), allOf(instanceOf(String.class), is((Object) newValue)));
    }

    @Test
    public void replacingSingleElementInMiddleAsUser_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().set(1, newValue);

        // then :
        final List<ClientPresentationModel> changes = clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
        assertThat(change.getAttribute("source").getValue(), allOf(instanceOf(String.class), is((Object) sourceModel.getId())));
        assertThat(change.getAttribute("attribute").getValue(), allOf(instanceOf(String.class), is((Object) "primitiveList")));
        assertThat(change.getAttribute("from").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("to").getValue(), allOf(instanceOf(Integer.class), is((Object) 2)));
        assertThat(change.getAttribute("count").getValue(), allOf(instanceOf(Integer.class), is((Object) 1)));
        assertThat(change.getAttribute("0").getValue(), allOf(instanceOf(String.class), is((Object) newValue)));
    }

    @Test
    public void replacingSingleElementAtEndAsUser_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        model.getPrimitiveList().set(2, newValue);

        // then :
        final List<ClientPresentationModel> changes = clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE);
        assertThat(changes, hasSize(1));

        final PresentationModel change = changes.get(0);
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
    public void addingObjectElementFromRemoting_shouldAddElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final PresentationModel classDescription = clientModelStore.findAllPresentationModelsByType(RemotingConstants.REMOTING_BEAN).get(0);
        classDescription.getAttribute("objectList").setValue(BeanConverterFactory.FIELD_TYPE_REMOTING_BEAN);
        final SimpleTestModel object = manager.create(SimpleTestModel.class);
        final PresentationModel objectModel = clientModelStore.findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);

        // when :
        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", objectModel.getId())
                .create();

        // then :
        assertThat(model.getObjectList(), is(Collections.singletonList(object)));
        assertThat(clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingObjectNullFromRemoting_shouldAddElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final PresentationModel classDescription = clientModelStore.findAllPresentationModelsByType(RemotingConstants.REMOTING_BEAN).get(0);
        classDescription.getAttribute("objectList").setValue(BeanConverterFactory.FIELD_TYPE_REMOTING_BEAN);

        // when :
        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", null)
                .create();

        // then :
        assertThat(model.getObjectList(), is(Collections.singletonList((SimpleTestModel) null)));
        assertThat(clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingPrimitiveElementFromRemoting_shouldAddElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String value = "Hello";

        // when :
        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", value)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Collections.singletonList(value)));
        assertThat(clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingPrimitiveNullFromRemoting_shouldAddElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        // when :
        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", null)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Collections.singletonList((String) null)));
        assertThat(clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingObjectElementFromRemoting_shouldDeleteElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        manager.create(SimpleTestModel.class);
        final PresentationModel objectModel = clientModelStore.findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);

        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", objectModel.getId())
                .create();
        assertThat(model.getObjectList(), hasSize(1));

        // when :
        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getObjectList(), empty());
        assertThat(clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingObjectNullFromRemoting_shouldDeleteElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", null)
                .create();
        assertThat(model.getObjectList(), hasSize(1));

        // when :
        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getObjectList(), empty());
        assertThat(clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingPrimitiveElementFromRemoting_shouldDeleteElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String value = "Hello";

        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", value)
                .create();
        assertThat(model.getPrimitiveList(), hasSize(1));

        // when :
        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), empty());
        assertThat(clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingPrimitiveNullFromRemoting_shouldDeleteElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", null)
                .create();
        assertThat(model.getPrimitiveList(), hasSize(1));

        // when :
        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), empty());
        assertThat(clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingObjectElementFromRemoting_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final PresentationModel classDescription = clientModelStore.findAllPresentationModelsByType(RemotingConstants.REMOTING_BEAN).get(0);
        classDescription.getAttribute("objectList").setValue(BeanConverterFactory.FIELD_TYPE_REMOTING_BEAN);
        final SimpleTestModel oldObject = manager.create(SimpleTestModel.class);
        final PresentationModel oldObjectModel = clientModelStore.findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);
        final SimpleTestModel newObject = manager.create(SimpleTestModel.class);
        final List<ClientPresentationModel> models = clientModelStore.findAllPresentationModelsByType(SimpleTestModel.class.getName());
        final PresentationModel newObjectModel = oldObjectModel == models.get(1) ? models.get(0) : models.get(1);

        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", oldObjectModel.getId())
                .create();
        assertThat(model.getObjectList(), is(Collections.singletonList(oldObject)));

        // when :
        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
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
        assertThat(clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingObjectElementWithNullFromRemoting_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final PresentationModel classDescription = clientModelStore.findAllPresentationModelsByType(RemotingConstants.REMOTING_BEAN).get(0);
        classDescription.getAttribute("objectList").setValue(BeanConverterFactory.FIELD_TYPE_REMOTING_BEAN);
        final SimpleTestModel oldObject = manager.create(SimpleTestModel.class);
        final PresentationModel oldObjectModel = clientModelStore.findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);

        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", oldObjectModel.getId())
                .create();
        assertThat(model.getObjectList(), is(Collections.singletonList(oldObject)));

        // when :
        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 1)
                .withAttribute("0", null)
                .create();

        // then :
        assertThat(model.getObjectList(), is(Collections.singletonList((SimpleTestModel) null)));
        assertThat(clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingObjectNullWithElementFromRemoting_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final PresentationModel classDescription = clientModelStore.findAllPresentationModelsByType(RemotingConstants.REMOTING_BEAN).get(0);
        classDescription.getAttribute("objectList").setValue(BeanConverterFactory.FIELD_TYPE_REMOTING_BEAN);
        final SimpleTestModel newObject = manager.create(SimpleTestModel.class);
        final PresentationModel newObjectModel = clientModelStore.findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);

        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", null)
                .create();
        assertThat(model.getObjectList(), is(Collections.singletonList((SimpleTestModel) null)));

        // when :
        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "objectList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 1)
                .withAttribute("0", newObjectModel.getId())
                .create();

        // then :
        assertThat(model.getObjectList(), is(Collections.singletonList(newObject)));
        assertThat(clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingPrimitiveElementFromRemoting_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String oldValue = "Hello";
        final String newValue = "Goodbye";

        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", oldValue)
                .create();
        assertThat(model.getPrimitiveList(), is(Collections.singletonList(oldValue)));

        // when :
        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 1)
                .withAttribute("0", newValue)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Collections.singletonList(newValue)));
        assertThat(clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingPrimitiveElementWithNullFromRemoting_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String oldValue = "Hello";

        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", oldValue)
                .create();
        assertThat(model.getPrimitiveList(), is(Collections.singletonList(oldValue)));

        // when :
        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 1)
                .withAttribute("0", null)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Collections.singletonList((String) null)));
        assertThat(clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingPrimitiveNullWithElementFromRemoting_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "Goodbye";

        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", null)
                .create();
        assertThat(model.getPrimitiveList(), is(Collections.singletonList((String) null)));

        // when :
        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 1)
                .withAttribute("0", newValue)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Collections.singletonList(newValue)));
        assertThat(clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }


    //////////////////////////////////////////////////////////////
    // Adding elements at different positions
    //////////////////////////////////////////////////////////////
    @Test
    public void addingMultipleElementsInEmptyListFromRemote_shouldAddElements(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        // when :
        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
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
        assertThat(clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingSingleElementInBeginningFromRemoting_shouldAddElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newElement = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 0)
                .withAttribute("count", 1)
                .withAttribute("0", newElement)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList(newElement, "1", "2", "3")));
        assertThat(clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingMultipleElementsInBeginningFromRemoting_shouldAddElements(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
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
        assertThat(clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingSingleElementInMiddleFromRemoting_shouldAddElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newElement = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 1)
                .withAttribute("to", 1)
                .withAttribute("count", 1)
                .withAttribute("0", newElement)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", newElement, "2", "3")));
        assertThat(clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingMultipleElementsInMiddleFromRemoting_shouldAddElements(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
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
        assertThat(clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingSingleElementAtEndFromRemoting_shouldAddElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newElement = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 3)
                .withAttribute("to", 3)
                .withAttribute("count", 1)
                .withAttribute("0", newElement)
                .create();

        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "2", "3", newElement)));
        assertThat(clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void addingMultipleElementsAtEndFromRemoting_shouldAddElements(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
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
        assertThat(clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }


    //////////////////////////////////////////////////////////////
    // Removing elements from different positions
    //////////////////////////////////////////////////////////////
    @Test
    public void deletingSingleElementInBeginningFromRemoting_shouldRemoveElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("2", "3")));
        assertThat(clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingMultipleElementInBeginningFromRemoting_shouldRemoveElements(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4", "5", "6"));
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 3)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("4", "5", "6")));
        assertThat(clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingSingleElementInMiddleFromRemoting_shouldDeleteElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 1)
                .withAttribute("to", 2)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "3")));
        assertThat(clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingMultipleElementInMiddleFromRemoting_shouldRemoveElements(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4", "5", "6"));
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 2)
                .withAttribute("to", 4)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "2", "5", "6")));
        assertThat(clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingSingleElementAtEndFromRemoting_shouldDeleteElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 2)
                .withAttribute("to", 3)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "2")));
        assertThat(clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void deletingMultipleElementAtEndFromRemoting_shouldRemoveElements(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4", "5", "6"));
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 4)
                .withAttribute("to", 6)
                .withAttribute("count", 0)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "2", "3", "4")));
        assertThat(clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    private void deleteAllPresentationModelsOfType(ClientModelStore clientModelStore, String listSplice) {
        List<ClientPresentationModel> toDelete = new ArrayList<>(clientModelStore.findAllPresentationModelsByType(listSplice));
        for (ClientPresentationModel model : toDelete) {
            clientModelStore.delete(model);
        }
    }


    //////////////////////////////////////////////////////////////
    // Replacing elements from different positions
    //////////////////////////////////////////////////////////////
    @Test
    public void replacingSingleElementInBeginningFromRemoting_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 0)
                .withAttribute("to", 1)
                .withAttribute("count", 1)
                .withAttribute("0", newValue)
                .create();

        assertThat(model.getPrimitiveList(), is(Arrays.asList("42", "2", "3")));
        assertThat(clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingMultipleElementsInBeginningFromRemoting_shouldReplaceElements(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4"));
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
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
        assertThat(clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingSingleElementInMiddleFromRemoting_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 1)
                .withAttribute("to", 2)
                .withAttribute("count", 1)
                .withAttribute("0", newValue)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "42", "3")));
        assertThat(clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingMultipleElementsInMiddleFromRemoting_shouldReplaceElements(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4"));
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
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
        assertThat(clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }

    @Test
    public void replacingSingleElementAtEndFromRemoting_shouldReplaceElement(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);
        final String newValue = "42";

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3"));
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
                .withAttribute("source", sourceModel.getId())
                .withAttribute("attribute", "primitiveList")
                .withAttribute("from", 2)
                .withAttribute("to", 3)
                .withAttribute("count", 1)
                .withAttribute("0", newValue)
                .create();

        // then :
        assertThat(model.getPrimitiveList(), is(Arrays.asList("1", "2", "42")));
        assertThat(clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }


    @Test
    public void replacingMultipleElementsAtEndFromRemoting_shouldReplaceElements(@Mocked AbstractClientConnector connector) {
        // given :
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final BeanManager manager = createBeanManager(clientModelStore);

        final ListReferenceModel model = manager.create(ListReferenceModel.class);
        final PresentationModel sourceModel = clientModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName()).get(0);

        model.getPrimitiveList().addAll(Arrays.asList("1", "2", "3", "4"));
        deleteAllPresentationModelsOfType(clientModelStore, RemotingConstants.LIST_SPLICE);

        // when :
        new PresentationModelBuilder(clientModelStore, RemotingConstants.LIST_SPLICE)
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
        assertThat(clientModelStore.findAllPresentationModelsByType(RemotingConstants.LIST_SPLICE), empty());
    }
}
