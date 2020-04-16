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

import dev.rico.internal.remoting.BeanDefinitionException;
import dev.rico.internal.remoting.BeanRepository;
import dev.rico.internal.remoting.EventDispatcher;
import dev.rico.internal.remoting.RemotingConstants;
import dev.rico.internal.remoting.converters.BooleanConverterFactory;
import dev.rico.internal.remoting.converters.ByteConverterFactory;
import dev.rico.internal.remoting.converters.CalendarConverterFactory;
import dev.rico.internal.remoting.converters.DateConverterFactory;
import dev.rico.internal.remoting.converters.BeanConverterFactory;
import dev.rico.internal.remoting.converters.DoubleConverterFactory;
import dev.rico.internal.remoting.converters.EnumConverterFactory;
import dev.rico.internal.remoting.converters.FloatConverterFactory;
import dev.rico.internal.remoting.converters.IntegerConverterFactory;
import dev.rico.internal.remoting.converters.LongConverterFactory;
import dev.rico.internal.remoting.converters.ShortConverterFactory;
import dev.rico.internal.remoting.converters.StringConverterFactory;
import dev.rico.internal.remoting.legacy.LegacyConstants;
import dev.rico.internal.remoting.legacy.core.Attribute;
import dev.rico.internal.remoting.legacy.core.PresentationModel;
import dev.rico.internal.remoting.server.legacy.ServerAttribute;
import dev.rico.internal.remoting.server.legacy.ServerModelStore;
import dev.rico.internal.remoting.server.legacy.ServerPresentationModel;
import dev.rico.server.remoting.util.AbstractRemotingTest;
import dev.rico.server.remoting.util.ChildModel;
import dev.rico.server.remoting.util.ComplexDataTypesModel;
import dev.rico.server.remoting.util.ListReferenceModel;
import dev.rico.server.remoting.util.PrimitiveDataTypesModel;
import dev.rico.server.remoting.util.SimpleAnnotatedTestModel;
import dev.rico.server.remoting.util.SimpleTestModel;
import dev.rico.server.remoting.util.SingleReferenceModel;
import dev.rico.remoting.BeanManager;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.testng.Assert.fail;

@SuppressWarnings({"unchecked", "deprecation"})
public class TestModelCreation extends AbstractRemotingTest {

    @Test
    public void testWithAnnotatedSimpleModel() {
        final ServerModelStore serverModelStore = createServerModelStore();
        final EventDispatcher dispatcher = createEventDispatcher(serverModelStore);
        final BeanRepository beanRepository = createBeanRepository(serverModelStore, dispatcher);
        final BeanManager manager = createBeanManager(serverModelStore, beanRepository, dispatcher);

        SimpleAnnotatedTestModel model = manager.create(SimpleAnnotatedTestModel.class);

        assertThat(model, notNullValue());
        assertThat(model.getMyProperty(), notNullValue());
        assertThat(model.getMyProperty().get(), nullValue());
        assertThat(beanRepository.isManaged(model), is(true));

        List<ServerPresentationModel> remotingModels = serverModelStore.findAllPresentationModelsByType(SimpleAnnotatedTestModel.class.getName());
        assertThat(remotingModels, hasSize(1));

        ServerPresentationModel remotingModel = remotingModels.get(0);

        List<ServerAttribute> attributes = remotingModel.getAttributes();

        assertThat(attributes, containsInAnyOrder(
                allOf(
                        hasProperty("propertyName", is("myProperty")),
                        hasProperty("value", nullValue()),
                        hasProperty("qualifier", nullValue())
                ),
                allOf(
                        hasProperty("propertyName", is(LegacyConstants.SOURCE_SYSTEM)),
                        hasProperty("value", is(LegacyConstants.SOURCE_SYSTEM_SERVER)),
                        hasProperty("qualifier", nullValue())
                )
        ));

        List<ServerPresentationModel> classModels = serverModelStore.findAllPresentationModelsByType(RemotingConstants.REMOTING_BEAN);
        assertThat(classModels, contains(
                hasProperty("attributes", containsInAnyOrder(
                        allOf(
                                hasProperty("propertyName", is(RemotingConstants.JAVA_CLASS)),
                                hasProperty("value", is(SimpleAnnotatedTestModel.class.getName())),
                                hasProperty("qualifier", nullValue())
                        ),
                        allOf(
                                hasProperty("propertyName", is("myProperty")),
                                hasProperty("value", is(StringConverterFactory.FIELD_TYPE_STRING)),
                                hasProperty("qualifier", nullValue())
                        ),
                        allOf(
                                hasProperty("propertyName", is(LegacyConstants.SOURCE_SYSTEM)),
                                hasProperty("value", is(LegacyConstants.SOURCE_SYSTEM_SERVER)),
                                hasProperty("qualifier", nullValue())
                        )
                ))
        ));
    }

    @Test
    public void testWithSimpleModel() {
        final ServerModelStore serverModelStore = createServerModelStore();
        final EventDispatcher dispatcher = createEventDispatcher(serverModelStore);
        final BeanRepository beanRepository = createBeanRepository(serverModelStore, dispatcher);
        final BeanManager manager = createBeanManager(serverModelStore, beanRepository, dispatcher);

        SimpleTestModel model = manager.create(SimpleTestModel.class);

        assertThat(model, notNullValue());
        assertThat(model.getTextProperty(), notNullValue());
        assertThat(model.getTextProperty().get(), nullValue());
        assertThat(beanRepository.isManaged(model), is(true));

        List<ServerPresentationModel> remotingModels = serverModelStore.findAllPresentationModelsByType(SimpleTestModel.class.getName());
        assertThat(remotingModels, hasSize(1));

        ServerPresentationModel remotingModel = remotingModels.get(0);

        List<ServerAttribute> attributes = remotingModel.getAttributes();

        assertThat(attributes, containsInAnyOrder(
                allOf(
                        hasProperty("propertyName", is("text")),
                        hasProperty("value", nullValue()),
                        hasProperty("qualifier", nullValue())
                ),
                allOf(
                        hasProperty("propertyName", is(LegacyConstants.SOURCE_SYSTEM)),
                        hasProperty("value", is(LegacyConstants.SOURCE_SYSTEM_SERVER)),
                        hasProperty("qualifier", nullValue())
                )
        ));

        List<ServerPresentationModel> classModels = serverModelStore.findAllPresentationModelsByType(RemotingConstants.REMOTING_BEAN);
        assertThat(classModels, contains(
                hasProperty("attributes", containsInAnyOrder(
                        allOf(
                                hasProperty("propertyName", is(RemotingConstants.JAVA_CLASS)),
                                hasProperty("value", is(SimpleTestModel.class.getName())),
                                hasProperty("qualifier", nullValue())
                        ),
                        allOf(
                                hasProperty("propertyName", is("text")),
                                hasProperty("value", is(StringConverterFactory.FIELD_TYPE_STRING)),
                                hasProperty("qualifier", nullValue())
                        ),
                        allOf(
                                hasProperty("propertyName", is(LegacyConstants.SOURCE_SYSTEM)),
                                hasProperty("value", is(LegacyConstants.SOURCE_SYSTEM_SERVER)),
                                hasProperty("qualifier", nullValue())
                        )
                ))
        ));
    }

    @Test(expectedExceptions = BeanDefinitionException.class)
    public void testWithWrongModelType() {
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        String model = manager.create(String.class);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testWithNull() {
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);
        String model = manager.create(null);
    }


    @Test
    public void testWithAllPrimitiveDatatypes() {
        final ServerModelStore serverModelStore = createServerModelStore();
        final EventDispatcher dispatcher = createEventDispatcher(serverModelStore);
        final BeanRepository beanRepository = createBeanRepository(serverModelStore, dispatcher);
        final BeanManager manager = createBeanManager(serverModelStore, beanRepository, dispatcher);

        PrimitiveDataTypesModel model = manager.create(PrimitiveDataTypesModel.class);

        assertThat(model, notNullValue());
        assertThat(model.getTextProperty(), notNullValue());
        assertThat(model.getTextProperty().get(), nullValue());
        assertThat(beanRepository.isManaged(model), is(true));

        List<ServerPresentationModel> remotingModels = serverModelStore.findAllPresentationModelsByType(PrimitiveDataTypesModel.class.getName());
        assertThat(remotingModels, hasSize(1));

        ServerPresentationModel remotingModel = remotingModels.get(0);

        List<ServerAttribute> attributes = remotingModel.getAttributes();
        assertThat(attributes, hasSize(9));

        for(Attribute attribute : attributes) {
            if (LegacyConstants.SOURCE_SYSTEM.equals(attribute.getPropertyName())) {
                assertThat(attribute.getValue(), Matchers.<Object>is(LegacyConstants.SOURCE_SYSTEM_SERVER));
            } else {
                assertThat(attribute.getValue(), nullValue());
            }
            assertThat(attribute.getQualifier(), nullValue());
        }

        final List<ServerPresentationModel> classModels = serverModelStore.findAllPresentationModelsByType(RemotingConstants.REMOTING_BEAN);
        assertThat(classModels, hasSize(1));

        final PresentationModel classModel = classModels.get(0);

        final List<Attribute> classAttributes = classModel.getAttributes();
        assertThat(classAttributes, hasSize(10));

        for(Attribute attribute : classAttributes) {
            if (RemotingConstants.JAVA_CLASS.equals(attribute.getPropertyName())) {
                assertThat(attribute.getValue(), Matchers.<Object>is(PrimitiveDataTypesModel.class.getName()));
            } else if (LegacyConstants.SOURCE_SYSTEM.equals(attribute.getPropertyName())) {
                assertThat(attribute.getValue(), Matchers.<Object>is(LegacyConstants.SOURCE_SYSTEM_SERVER));
            } else {
                switch (attribute.getPropertyName()) {
                    case "byteProperty":
                        assertThat(attribute.getValue(), Matchers.<Object>is(ByteConverterFactory.FIELD_TYPE_BYTE));
                        break;
                    case "shortProperty":
                        assertThat(attribute.getValue(), Matchers.<Object>is(ShortConverterFactory.FIELD_TYPE_SHORT));
                        break;
                    case "integerProperty":
                        assertThat(attribute.getValue(), Matchers.<Object>is(IntegerConverterFactory.FIELD_TYPE_INT));
                        break;
                    case "longProperty":
                        assertThat(attribute.getValue(), Matchers.<Object>is(LongConverterFactory.FIELD_TYPE_LONG));
                        break;
                    case "floatProperty":
                        assertThat(attribute.getValue(), Matchers.<Object>is(FloatConverterFactory.FIELD_TYPE_FLOAT));
                        break;
                    case "doubleProperty":
                        assertThat(attribute.getValue(), Matchers.<Object>is(DoubleConverterFactory.FIELD_TYPE_DOUBLE));
                        break;
                    case "booleanProperty":
                        assertThat(attribute.getValue(), Matchers.<Object>is(BooleanConverterFactory.FIELD_TYPE_BOOLEAN));
                        break;
                    case "textProperty":
                        assertThat(attribute.getValue(), Matchers.<Object>is(StringConverterFactory.FIELD_TYPE_STRING));
                        break;
                    default:
                        fail("Unknown attribute found: " + attribute);
                        break;
                }
            }
            assertThat(attribute.getQualifier(), nullValue());
        }
    }


    @Test
    public void testWithComplexDataTypesModel() {
        final ServerModelStore serverModelStore = createServerModelStore();
        final EventDispatcher dispatcher = createEventDispatcher(serverModelStore);
        final BeanRepository beanRepository = createBeanRepository(serverModelStore, dispatcher);
        final BeanManager manager = createBeanManager(serverModelStore, beanRepository, dispatcher);

        ComplexDataTypesModel model = manager.create(ComplexDataTypesModel.class);

        assertThat(model, notNullValue());
        assertThat(model.getDateProperty(), notNullValue());
        assertThat(model.getDateProperty().get(), nullValue());
        assertThat(model.getCalendarProperty(), notNullValue());
        assertThat(model.getCalendarProperty().get(), nullValue());
        assertThat(model.getEnumProperty(), notNullValue());
        assertThat(model.getEnumProperty().get(), nullValue());
        assertThat(beanRepository.isManaged(model), is(true));

        List<ServerPresentationModel> remotingModels = serverModelStore.findAllPresentationModelsByType(ComplexDataTypesModel.class.getName());
        assertThat(remotingModels, hasSize(1));

        PresentationModel remotingModel = remotingModels.get(0);

        List<Attribute> attributes = remotingModel.getAttributes();

        assertThat(attributes, containsInAnyOrder(
                allOf(
                        hasProperty("propertyName", is("dateProperty")),
                        hasProperty("value", nullValue()),
                        hasProperty("qualifier", nullValue())
                ),
                allOf(
                        hasProperty("propertyName", is("calendarProperty")),
                        hasProperty("value", nullValue()),
                        hasProperty("qualifier", nullValue())
                ),
                allOf(
                        hasProperty("propertyName", is("enumProperty")),
                        hasProperty("value", nullValue()),
                        hasProperty("qualifier", nullValue())
                ),
                allOf(
                        hasProperty("propertyName", is(LegacyConstants.SOURCE_SYSTEM)),
                        hasProperty("value", is(LegacyConstants.SOURCE_SYSTEM_SERVER)),
                        hasProperty("qualifier", nullValue())
                )
        ));

        List<ServerPresentationModel> classModels = serverModelStore.findAllPresentationModelsByType(RemotingConstants.REMOTING_BEAN);
        assertThat(classModels, contains(
                hasProperty("attributes", containsInAnyOrder(
                        allOf(
                                hasProperty("propertyName", is(RemotingConstants.JAVA_CLASS)),
                                hasProperty("value", is(ComplexDataTypesModel.class.getName())),
                                hasProperty("qualifier", nullValue())
                        ),
                        allOf(
                                hasProperty("propertyName", is("dateProperty")),
                                hasProperty("value", is(DateConverterFactory.FIELD_TYPE_DATE)),
                                hasProperty("qualifier", nullValue())
                        ),
                        allOf(
                                hasProperty("propertyName", is("calendarProperty")),
                                hasProperty("value", is(CalendarConverterFactory.FIELD_TYPE_CALENDAR)),
                                hasProperty("qualifier", nullValue())
                        ),
                        allOf(
                                hasProperty("propertyName", is("enumProperty")),
                                hasProperty("value", is(EnumConverterFactory.FIELD_TYPE_ENUM)),
                                hasProperty("qualifier", nullValue())
                        ),
                        allOf(
                                hasProperty("propertyName", is(LegacyConstants.SOURCE_SYSTEM)),
                                hasProperty("value", is(LegacyConstants.SOURCE_SYSTEM_SERVER)),
                                hasProperty("qualifier", nullValue())
                        )
                ))
        ));
    }


    @Test
    public void testWithSingleReferenceModel() {
        final ServerModelStore serverModelStore = createServerModelStore();
        final EventDispatcher dispatcher = createEventDispatcher(serverModelStore);
        final BeanRepository beanRepository = createBeanRepository(serverModelStore, dispatcher);
        final BeanManager manager = createBeanManager(serverModelStore, beanRepository, dispatcher);

        SingleReferenceModel model = manager.create(SingleReferenceModel.class);

        assertThat(model, notNullValue());
        assertThat(model.getReferenceProperty(), notNullValue());
        assertThat(model.getReferenceProperty().get(), nullValue());
        assertThat(beanRepository.isManaged(model), is(true));

        List<ServerPresentationModel> remotingModels = serverModelStore.findAllPresentationModelsByType(SingleReferenceModel.class.getName());
        assertThat(remotingModels, hasSize(1));

        ServerPresentationModel remotingModel = remotingModels.get(0);

        List<ServerAttribute> attributes = remotingModel.getAttributes();

        assertThat(attributes, containsInAnyOrder(
                allOf(
                        hasProperty("propertyName", is("referenceProperty")),
                        hasProperty("value", nullValue()),
                        hasProperty("qualifier", nullValue())
                ),
                allOf(
                        hasProperty("propertyName", is(LegacyConstants.SOURCE_SYSTEM)),
                        hasProperty("value", is(LegacyConstants.SOURCE_SYSTEM_SERVER)),
                        hasProperty("qualifier", nullValue())
                )
        ));

        List<ServerPresentationModel> classModels = serverModelStore.findAllPresentationModelsByType(RemotingConstants.REMOTING_BEAN);
        assertThat(classModels, contains(
                hasProperty("attributes", containsInAnyOrder(
                        allOf(
                                hasProperty("propertyName", is(RemotingConstants.JAVA_CLASS)),
                                hasProperty("value", is(SingleReferenceModel.class.getName())),
                                hasProperty("qualifier", nullValue())
                        ),
                        allOf(
                                hasProperty("propertyName", is("referenceProperty")),
                                hasProperty("value", is(BeanConverterFactory.FIELD_TYPE_REMOTING_BEAN)),
                                hasProperty("qualifier", nullValue())
                        ),
                        allOf(
                                hasProperty("propertyName", is(LegacyConstants.SOURCE_SYSTEM)),
                                hasProperty("value", is(LegacyConstants.SOURCE_SYSTEM_SERVER)),
                                hasProperty("qualifier", nullValue())
                        )
                ))
        ));
    }

    @Test
    public void testWithListReferenceModel() {
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        ListReferenceModel model = manager.create(ListReferenceModel.class);

        assertThat(model, notNullValue());
        assertThat(model.getObjectList(), empty());
        assertThat(model.getPrimitiveList(), empty());

        List<ServerPresentationModel> remotingModels = serverModelStore.findAllPresentationModelsByType(ListReferenceModel.class.getName());
        assertThat(remotingModels, hasSize(1));

        ServerPresentationModel remotingModel = remotingModels.get(0);

        List<ServerAttribute> attributes = remotingModel.getAttributes();

        assertThat(attributes, contains(
                allOf(
                        hasProperty("propertyName", is(LegacyConstants.SOURCE_SYSTEM)),
                        hasProperty("value", is(LegacyConstants.SOURCE_SYSTEM_SERVER)),
                        hasProperty("qualifier", nullValue())
                )
        ));

        List<ServerPresentationModel> classModels = serverModelStore.findAllPresentationModelsByType(RemotingConstants.REMOTING_BEAN);
        assertThat(classModels, contains(
                hasProperty("attributes", containsInAnyOrder(
                        allOf(
                                hasProperty("propertyName", is(RemotingConstants.JAVA_CLASS)),
                                hasProperty("value", is(ListReferenceModel.class.getName())),
                                hasProperty("qualifier", nullValue())
                        ),
                        allOf(
                                hasProperty("propertyName", is("objectList")),
                                hasProperty("value", is(BeanConverterFactory.FIELD_TYPE_REMOTING_BEAN)),
                                hasProperty("qualifier", nullValue())
                        ),
                        allOf(
                                hasProperty("propertyName", is("primitiveList")),
                                hasProperty("value", is(StringConverterFactory.FIELD_TYPE_STRING)),
                                hasProperty("qualifier", nullValue())
                        ),
                        allOf(
                                hasProperty("propertyName", is(LegacyConstants.SOURCE_SYSTEM)),
                                hasProperty("value", is(LegacyConstants.SOURCE_SYSTEM_SERVER)),
                                hasProperty("qualifier", nullValue())
                        )
                ))
        ));
    }

    @Test
    public void testWithInheritedModel() {
        final ServerModelStore serverModelStore = createServerModelStore();
        final EventDispatcher dispatcher = createEventDispatcher(serverModelStore);
        final BeanRepository beanRepository = createBeanRepository(serverModelStore, dispatcher);
        final BeanManager manager = createBeanManager(serverModelStore, beanRepository, dispatcher);

        ChildModel model = manager.create(ChildModel.class);

        assertThat(model, notNullValue());
        assertThat(model.getParentProperty(), notNullValue());
        assertThat(model.getParentProperty().get(), nullValue());
        assertThat(model.getChildProperty(), notNullValue());
        assertThat(model.getChildProperty().get(), nullValue());
        assertThat(beanRepository.isManaged(model), is(true));

        List<ServerPresentationModel> remotingModels = serverModelStore.findAllPresentationModelsByType(ChildModel.class.getName());
        assertThat(remotingModels, hasSize(1));

        ServerPresentationModel remotingModel = remotingModels.get(0);

        List<ServerAttribute> attributes = remotingModel.getAttributes();

        assertThat(attributes, containsInAnyOrder(
                allOf(
                        hasProperty("propertyName", is("childProperty")),
                        hasProperty("value", nullValue()),
                        hasProperty("qualifier", nullValue())
                ),
                allOf(
                        hasProperty("propertyName", is("parentProperty")),
                        hasProperty("value", nullValue()),
                        hasProperty("qualifier", nullValue())
                ),
                allOf(
                        hasProperty("propertyName", is(LegacyConstants.SOURCE_SYSTEM)),
                        hasProperty("value", is(LegacyConstants.SOURCE_SYSTEM_SERVER)),
                        hasProperty("qualifier", nullValue())
                )
        ));

        List<ServerPresentationModel> classModels = serverModelStore.findAllPresentationModelsByType(RemotingConstants.REMOTING_BEAN);
        assertThat(classModels, hasSize(1));
        assertThat(classModels, contains(
                hasProperty("attributes", containsInAnyOrder(
                        allOf(
                                hasProperty("propertyName", is(RemotingConstants.JAVA_CLASS)),
                                hasProperty("value", is(ChildModel.class.getName())),
                                hasProperty("qualifier", nullValue())
                        ),
                        allOf(
                                hasProperty("propertyName", is("childProperty")),
                                hasProperty("value", is(StringConverterFactory.FIELD_TYPE_STRING)),
                                hasProperty("qualifier", nullValue())
                        ),
                        allOf(
                                hasProperty("propertyName", is("parentProperty")),
                                hasProperty("value", is(StringConverterFactory.FIELD_TYPE_STRING)),
                                hasProperty("qualifier", nullValue())
                        ),
                        allOf(
                                hasProperty("propertyName", is(LegacyConstants.SOURCE_SYSTEM)),
                                hasProperty("value", is(LegacyConstants.SOURCE_SYSTEM_SERVER)),
                                hasProperty("qualifier", nullValue())
                        )
                ))
        ));
    }

}
