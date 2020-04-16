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

import dev.rico.internal.remoting.legacy.core.Attribute;
import dev.rico.internal.remoting.legacy.core.PresentationModel;
import dev.rico.internal.remoting.server.legacy.ServerModelStore;
import dev.rico.internal.remoting.server.legacy.ServerPresentationModel;
import dev.rico.server.remoting.util.AbstractRemotingTest;
import dev.rico.server.remoting.util.ChildModel;
import dev.rico.server.remoting.util.ComplexDataTypesModel;
import dev.rico.server.remoting.util.PrimitiveDataTypesModel;
import dev.rico.server.remoting.util.SimpleAnnotatedTestModel;
import dev.rico.server.remoting.util.SimpleTestModel;
import dev.rico.server.remoting.util.SingleReferenceModel;
import dev.rico.remoting.BeanManager;
import org.testng.annotations.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import static dev.rico.server.remoting.util.ComplexDataTypesModel.EnumValues.VALUE_1;
import static dev.rico.server.remoting.util.ComplexDataTypesModel.EnumValues.VALUE_2;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@SuppressWarnings("deprecation")
public class TestPropertyValue extends AbstractRemotingTest {

    @Test
    public void testWithAnnotatedSimpleModel() {
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        SimpleAnnotatedTestModel model = manager.create(SimpleAnnotatedTestModel.class);

        ServerPresentationModel remotingModel = serverModelStore.findAllPresentationModelsByType(SimpleAnnotatedTestModel.class.getName()).get(0);

        Attribute textAttribute = remotingModel.getAttribute("myProperty");
        assertThat(textAttribute.getValue(), nullValue());

        model.getMyProperty().set("Hallo Platform");
        assertThat((String) textAttribute.getValue(), is("Hallo Platform"));
        assertThat(model.getMyProperty().get(), is("Hallo Platform"));

        textAttribute.setValue("Hello endpoint");
        assertThat((String) textAttribute.getValue(), is("Hello endpoint"));
        assertThat(model.getMyProperty().get(), is("Hello endpoint"));
    }

    @Test
    public void testWithSimpleModel() {
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        SimpleTestModel model = manager.create(SimpleTestModel.class);

        ServerPresentationModel remotingModel = serverModelStore.findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);

        Attribute textAttribute = remotingModel.getAttribute("text");
        assertThat(textAttribute.getValue(), nullValue());

        model.getTextProperty().set("Hallo Platform");
        assertThat((String) textAttribute.getValue(), is("Hallo Platform"));
        assertThat(model.getTextProperty().get(), is("Hallo Platform"));

        textAttribute.setValue("Hello endpoint");
        assertThat((String) textAttribute.getValue(), is("Hello endpoint"));
        assertThat(model.getTextProperty().get(), is("Hello endpoint"));
    }

    @Test
    public void testWithAllPrimitiveDataTypesModel() {
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        PrimitiveDataTypesModel model = manager.create(PrimitiveDataTypesModel.class);

        ServerPresentationModel remotingModel = serverModelStore.findAllPresentationModelsByType(PrimitiveDataTypesModel.class.getName()).get(0);

        Attribute textAttribute = remotingModel.getAttribute("textProperty");
        assertThat(textAttribute.getValue(), nullValue());

        model.getTextProperty().set("Hallo Platform");
        assertThat((String) textAttribute.getValue(), is("Hallo Platform"));
        assertThat(model.getTextProperty().get(), is("Hallo Platform"));

        textAttribute.setValue("Hello endpoint");
        assertThat((String) textAttribute.getValue(), is("Hello endpoint"));
        assertThat(model.getTextProperty().get(), is("Hello endpoint"));


        Attribute intAttribute = remotingModel.getAttribute("integerProperty");
        assertThat(intAttribute.getValue(), nullValue());

        model.getIntegerProperty().set(1);
        assertThat((Integer) intAttribute.getValue(), is(1));
        assertThat(model.getIntegerProperty().get(), is(1));

        intAttribute.setValue(2);
        assertThat((Integer) intAttribute.getValue(), is(2));
        assertThat(model.getIntegerProperty().get(), is(2));


        Attribute booleanAttribute = remotingModel.getAttribute("booleanProperty");
        assertThat(booleanAttribute.getValue(), nullValue());

        model.getBooleanProperty().set(true);
        assertThat((Boolean) booleanAttribute.getValue(), is(true));
        assertThat(model.getBooleanProperty().get(), is(true));

        model.getBooleanProperty().set(false);
        assertThat((Boolean) booleanAttribute.getValue(), is(false));
        assertThat(model.getBooleanProperty().get(), is(false));

    }


    @Test
    public void testWithComplexDataTypesModel() {
        final Calendar date1 = new GregorianCalendar(2016, Calendar.MARCH, 1, 0, 1, 2);
        date1.set(Calendar.MILLISECOND, 3);
        date1.setTimeZone(TimeZone.getTimeZone("GMT+2:00"));
        final Calendar date2 = new GregorianCalendar(2016, Calendar.FEBRUARY, 29, 0, 1, 2);
        date2.set(Calendar.MILLISECOND, 3);
        date2.setTimeZone(TimeZone.getTimeZone("UTC"));

        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        ComplexDataTypesModel model = manager.create(ComplexDataTypesModel.class);

        PresentationModel remotingModel = serverModelStore.findAllPresentationModelsByType(ComplexDataTypesModel.class.getName()).get(0);


        Attribute dateAttribute = remotingModel.getAttribute("dateProperty");
        assertThat(dateAttribute.getValue(), nullValue());

        model.getDateProperty().set(date1.getTime());
        assertThat((String) dateAttribute.getValue(), is("2016-02-29T22:01:02.003Z"));
        assertThat(model.getDateProperty().get(), is(date1.getTime()));

        dateAttribute.setValue("2016-02-29T00:01:02.003Z");
        assertThat((String) dateAttribute.getValue(), is("2016-02-29T00:01:02.003Z"));
        assertThat(model.getDateProperty().get(), is(date2.getTime()));


        Attribute calendarAttribute = remotingModel.getAttribute("calendarProperty");
        assertThat(calendarAttribute.getValue(), nullValue());

        model.getCalendarProperty().set(date1);
        assertThat((String) calendarAttribute.getValue(), is("2016-02-29T22:01:02.003Z"));
        assertThat(model.getCalendarProperty().get().getTimeInMillis(), is(date1.getTimeInMillis()));

        calendarAttribute.setValue("2016-02-29T00:01:02.003Z");
        assertThat((String) calendarAttribute.getValue(), is("2016-02-29T00:01:02.003Z"));
        assertThat(model.getCalendarProperty().get(), is(date2));


        Attribute enumAttribute = remotingModel.getAttribute("enumProperty");
        assertThat(enumAttribute.getValue(), nullValue());

        model.getEnumProperty().set(VALUE_1);
        assertThat((String) enumAttribute.getValue(), is("VALUE_1"));
        assertThat(model.getEnumProperty().get(), is(VALUE_1));

        enumAttribute.setValue("VALUE_2");
        assertThat((String) enumAttribute.getValue(), is("VALUE_2"));
        assertThat(model.getEnumProperty().get(), is(VALUE_2));
    }


    @Test
    public void testWithSingleReferenceModel() {
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        final SimpleTestModel ref1 = manager.create(SimpleTestModel.class);
        ref1.getTextProperty().set("ref1_text");
        final SimpleTestModel ref2 = manager.create(SimpleTestModel.class);
        ref2.getTextProperty().set("ref2_text");
        final List<ServerPresentationModel> refPMs = serverModelStore.findAllPresentationModelsByType(SimpleTestModel.class.getName());
        final ServerPresentationModel ref1PM = "ref1_text".equals(refPMs.get(0).getAttribute("text").getValue())? refPMs.get(0) : refPMs.get(1);
        final ServerPresentationModel ref2PM = "ref2_text".equals(refPMs.get(0).getAttribute("text").getValue())? refPMs.get(0) : refPMs.get(1);

        final SingleReferenceModel model = manager.create(SingleReferenceModel.class);

        final ServerPresentationModel remotingModel = serverModelStore.findAllPresentationModelsByType(SingleReferenceModel.class.getName()).get(0);

        final Attribute referenceAttribute = remotingModel.getAttribute("referenceProperty");
        assertThat(referenceAttribute.getValue(), nullValue());

        model.getReferenceProperty().set(ref1);
        assertThat((String) referenceAttribute.getValue(), is(ref1PM.getId()));
        assertThat(model.getReferenceProperty().get(), is(ref1));

        referenceAttribute.setValue(ref2PM.getId());
        assertThat((String) referenceAttribute.getValue(), is(ref2PM.getId()));
        assertThat(model.getReferenceProperty().get(), is(ref2));
    }

    @Test
    public void testWithInheritedModel() {
        final ServerModelStore serverModelStore = createServerModelStore();
        final BeanManager manager = createBeanManager(serverModelStore);

        ChildModel model = manager.create(ChildModel.class);

        ServerPresentationModel remotingModel = serverModelStore.findAllPresentationModelsByType(ChildModel.class.getName()).get(0);

        Attribute childAttribute = remotingModel.getAttribute("childProperty");
        assertThat(childAttribute.getValue(), nullValue());
        Attribute parentAttribute = remotingModel.getAttribute("parentProperty");
        assertThat(parentAttribute.getValue(), nullValue());

        model.getChildProperty().set("Hallo Platform");
        assertThat((String) childAttribute.getValue(), is("Hallo Platform"));
        assertThat(model.getChildProperty().get(), is("Hallo Platform"));
        assertThat(parentAttribute.getValue(), nullValue());
        assertThat(model.getParentProperty().get(), nullValue());

        parentAttribute.setValue("Hello endpoint");
        assertThat((String) childAttribute.getValue(), is("Hallo Platform"));
        assertThat(model.getChildProperty().get(), is("Hallo Platform"));
        assertThat((String) parentAttribute.getValue(), is("Hello endpoint"));
        assertThat(model.getParentProperty().get(), is("Hello endpoint"));
    }


}
