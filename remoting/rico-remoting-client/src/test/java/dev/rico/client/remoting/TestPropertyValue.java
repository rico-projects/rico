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
import dev.rico.client.remoting.util.ChildModel;
import dev.rico.client.remoting.util.ComplexDataTypesModel;
import dev.rico.client.remoting.util.PrimitiveDataTypesModel;
import dev.rico.client.remoting.util.SimpleAnnotatedTestModel;
import dev.rico.client.remoting.util.SimpleTestModel;
import dev.rico.client.remoting.util.SingleReferenceModel;
import dev.rico.internal.client.remoting.legacy.ClientModelStore;
import dev.rico.internal.client.remoting.legacy.ClientPresentationModel;
import dev.rico.internal.client.remoting.legacy.communication.AbstractClientConnector;
import dev.rico.internal.remoting.BeanRepository;
import dev.rico.internal.remoting.EventDispatcher;
import dev.rico.internal.remoting.legacy.core.Attribute;
import dev.rico.internal.remoting.legacy.core.PresentationModel;
import dev.rico.remoting.BeanManager;
import mockit.Mocked;
import org.testng.annotations.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import static dev.rico.client.remoting.util.ComplexDataTypesModel.EnumValues.VALUE_1;
import static dev.rico.client.remoting.util.ComplexDataTypesModel.EnumValues.VALUE_2;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class TestPropertyValue extends AbstractRemotingTest {

    @Test
    public void testWithAnnotatedSimpleModel(@Mocked AbstractClientConnector connector) {
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final EventDispatcher dispatcher = createEventDispatcher(clientModelStore);
        final BeanRepository repository = createBeanRepository(clientModelStore, dispatcher);
        final BeanManager manager = createBeanManager(clientModelStore, repository, dispatcher);

        SimpleAnnotatedTestModel model = manager.create(SimpleAnnotatedTestModel.class);

        PresentationModel remotingModel = clientModelStore.findAllPresentationModelsByType(SimpleAnnotatedTestModel.class.getName()).get(0);

        Attribute textAttribute = remotingModel.getAttribute("myProperty");
        assertThat(textAttribute.getValue(), nullValue());

        model.myProperty().set("Hallo Platform");
        assertThat(textAttribute.getValue(), is((Object) "Hallo Platform"));
        assertThat(model.myProperty().get(), is("Hallo Platform"));

        textAttribute.setValue("Hallo Endpoint");
        assertThat(textAttribute.getValue(), is((Object) "Hallo Endpoint"));
        assertThat(model.myProperty().get(), is("Hallo Endpoint"));
    }

    @Test
    public void testWithSimpleModel(@Mocked AbstractClientConnector connector) {
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final EventDispatcher dispatcher = createEventDispatcher(clientModelStore);
        final BeanRepository repository = createBeanRepository(clientModelStore, dispatcher);
        final BeanManager manager = createBeanManager(clientModelStore, repository, dispatcher);

        SimpleTestModel model = manager.create(SimpleTestModel.class);

        PresentationModel remotingModel = clientModelStore.findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);

        Attribute textAttribute = remotingModel.getAttribute("text");
        assertThat(textAttribute.getValue(), nullValue());

        model.getTextProperty().set("Hallo Platform");
        assertThat(textAttribute.getValue(), is((Object) "Hallo Platform"));
        assertThat(model.getTextProperty().get(), is("Hallo Platform"));

        textAttribute.setValue("Hallo Endpoint");
        assertThat(textAttribute.getValue(), is((Object) "Hallo Endpoint"));
        assertThat(model.getTextProperty().get(), is("Hallo Endpoint"));
    }

    @Test
    public void testWithAllPrimitiveDataTypesModel(@Mocked AbstractClientConnector connector) {
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final EventDispatcher dispatcher = createEventDispatcher(clientModelStore);
        final BeanRepository repository = createBeanRepository(clientModelStore, dispatcher);
        final BeanManager manager = createBeanManager(clientModelStore, repository, dispatcher);

        PrimitiveDataTypesModel model = manager.create(PrimitiveDataTypesModel.class);

        PresentationModel remotingModel = clientModelStore.findAllPresentationModelsByType(PrimitiveDataTypesModel.class.getName()).get(0);

        Attribute textAttribute = remotingModel.getAttribute("textProperty");
        assertThat(textAttribute.getValue(), nullValue());

        model.getTextProperty().set("Hallo Platform");
        assertThat(textAttribute.getValue(), is((Object) "Hallo Platform"));
        assertThat(model.getTextProperty().get(), is("Hallo Platform"));

        textAttribute.setValue("Hallo Endpoint");
        assertThat(textAttribute.getValue(), is((Object) "Hallo Endpoint"));
        assertThat(model.getTextProperty().get(), is("Hallo Endpoint"));


        Attribute intAttribute = remotingModel.getAttribute("integerProperty");
        assertThat(intAttribute.getValue(), nullValue());

        model.getIntegerProperty().set(1);
        assertThat(intAttribute.getValue(), is((Object) 1));
        assertThat(model.getIntegerProperty().get(), is(1));

        intAttribute.setValue(2);
        assertThat(intAttribute.getValue(), is((Object) 2));
        assertThat(model.getIntegerProperty().get(), is(2));


        Attribute booleanAttribute = remotingModel.getAttribute("booleanProperty");
        assertThat(booleanAttribute.getValue(), nullValue());

        model.getBooleanProperty().set(true);
        assertThat(booleanAttribute.getValue(), is((Object) true));
        assertThat(model.getBooleanProperty().get(), is(true));

        model.getBooleanProperty().set(false);
        assertThat(booleanAttribute.getValue(), is((Object) false));
        assertThat(model.getBooleanProperty().get(), is(false));

    }


    @Test
    public void testWithComplexDataTypesModel(@Mocked AbstractClientConnector connector) {
        final Calendar date1 = new GregorianCalendar(2016, Calendar.MARCH, 1, 0, 1, 2);
        date1.set(Calendar.MILLISECOND, 3);
        date1.setTimeZone(TimeZone.getTimeZone("GMT+2:00"));
        final Calendar date2 = new GregorianCalendar(2016, Calendar.FEBRUARY, 29, 0, 1, 2);
        date2.set(Calendar.MILLISECOND, 3);
        date2.setTimeZone(TimeZone.getTimeZone("UTC"));

        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final EventDispatcher dispatcher = createEventDispatcher(clientModelStore);
        final BeanRepository repository = createBeanRepository(clientModelStore, dispatcher);
        final BeanManager manager = createBeanManager(clientModelStore, repository, dispatcher);

        ComplexDataTypesModel model = manager.create(ComplexDataTypesModel.class);

        PresentationModel remotingModel = clientModelStore.findAllPresentationModelsByType(ComplexDataTypesModel.class.getName()).get(0);


        Attribute dateAttribute = remotingModel.getAttribute("dateProperty");
        assertThat(dateAttribute.getValue(), nullValue());

        model.getDateProperty().set(date1.getTime());
        assertThat(dateAttribute.getValue().toString(), is("2016-02-29T22:01:02.003Z"));
        assertThat(model.getDateProperty().get(), is(date1.getTime()));

        dateAttribute.setValue("2016-02-29T00:01:02.003Z");
        assertThat(dateAttribute.getValue().toString(), is("2016-02-29T00:01:02.003Z"));
        assertThat(model.getDateProperty().get(), is(date2.getTime()));


        Attribute calendarAttribute = remotingModel.getAttribute("calendarProperty");
        assertThat(calendarAttribute.getValue(), nullValue());

        model.getCalendarProperty().set(date1);
        assertThat(calendarAttribute.getValue().toString(), is("2016-02-29T22:01:02.003Z"));
        assertThat(model.getCalendarProperty().get().getTimeInMillis(), is(date1.getTimeInMillis()));

        calendarAttribute.setValue("2016-02-29T00:01:02.003Z");
        assertThat(calendarAttribute.getValue().toString(), is("2016-02-29T00:01:02.003Z"));
        assertThat(model.getCalendarProperty().get(), is(date2));


        Attribute enumAttribute = remotingModel.getAttribute("enumProperty");
        assertThat(enumAttribute.getValue(), nullValue());

        model.getEnumProperty().set(VALUE_1);
        assertThat(enumAttribute.getValue().toString(), is("VALUE_1"));
        assertThat(model.getEnumProperty().get(), is(VALUE_1));

        enumAttribute.setValue("VALUE_2");
        assertThat(enumAttribute.getValue().toString(), is("VALUE_2"));
        assertThat(model.getEnumProperty().get(), is(VALUE_2));
    }


    @Test
    public void testWithSingleReferenceModel(@Mocked AbstractClientConnector connector) {
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final EventDispatcher dispatcher = createEventDispatcher(clientModelStore);
        final BeanRepository repository = createBeanRepository(clientModelStore, dispatcher);
        final BeanManager manager = createBeanManager(clientModelStore, repository, dispatcher);

        final SimpleTestModel ref1 = manager.create(SimpleTestModel.class);
        ref1.getTextProperty().set("ref1_text");
        final SimpleTestModel ref2 = manager.create(SimpleTestModel.class);
        ref2.getTextProperty().set("ref2_text");
        final List<ClientPresentationModel> refPMs = clientModelStore.findAllPresentationModelsByType(SimpleTestModel.class.getName());
        final PresentationModel ref1PM = "ref1_text".equals(refPMs.get(0).getAttribute("text").getValue())? refPMs.get(0) : refPMs.get(1);
        final PresentationModel ref2PM = "ref2_text".equals(refPMs.get(0).getAttribute("text").getValue())? refPMs.get(0) : refPMs.get(1);

        final SingleReferenceModel model = manager.create(SingleReferenceModel.class);

        final PresentationModel remotingModel = clientModelStore.findAllPresentationModelsByType(SingleReferenceModel.class.getName()).get(0);

        final Attribute referenceAttribute = remotingModel.getAttribute("referenceProperty");
        assertThat(referenceAttribute.getValue(), nullValue());

        model.getReferenceProperty().set(ref1);
        assertThat(referenceAttribute.getValue(), is((Object) ref1PM.getId()));
        assertThat(model.getReferenceProperty().get(), is(ref1));

        referenceAttribute.setValue(ref2PM.getId());
        assertThat(referenceAttribute.getValue(), is((Object) ref2PM.getId()));
        assertThat(model.getReferenceProperty().get(), is(ref2));
    }

    @Test
    public void testWithInheritedModel(@Mocked AbstractClientConnector connector) {
        final ClientModelStore clientModelStore = createClientModelStore(connector);
        final EventDispatcher dispatcher = createEventDispatcher(clientModelStore);
        final BeanRepository repository = createBeanRepository(clientModelStore, dispatcher);
        final BeanManager manager = createBeanManager(clientModelStore, repository, dispatcher);

        ChildModel model = manager.create(ChildModel.class);

        PresentationModel remotingModel = clientModelStore.findAllPresentationModelsByType(ChildModel.class.getName()).get(0);

        Attribute childAttribute = remotingModel.getAttribute("childProperty");
        assertThat(childAttribute.getValue(), nullValue());
        Attribute parentAttribute = remotingModel.getAttribute("parentProperty");
        assertThat(parentAttribute.getValue(), nullValue());

        model.getChildProperty().set("Hallo Platform");
        assertThat(childAttribute.getValue(), is((Object) "Hallo Platform"));
        assertThat(model.getChildProperty().get(), is("Hallo Platform"));
        assertThat(parentAttribute.getValue(), nullValue());
        assertThat(model.getParentProperty().get(), nullValue());

        parentAttribute.setValue("Hallo Endpoint");
        assertThat(childAttribute.getValue(), is((Object) "Hallo Platform"));
        assertThat(model.getChildProperty().get(), is("Hallo Platform"));
        assertThat(parentAttribute.getValue(), is((Object) "Hallo Endpoint"));
        assertThat(model.getParentProperty().get(), is("Hallo Endpoint"));
    }


}
