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
package dev.rico.integrationtests.server.qualifier;

import dev.rico.integrationtests.qualifier.QualifierTestBean;
import dev.rico.integrationtests.qualifier.QualifierTestSubBean;
import dev.rico.integrationtests.server.TestConfiguration;
import dev.rico.server.remoting.test.ControllerUnderTest;
import dev.rico.server.remoting.test.SpringTestNGControllerTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static dev.rico.integrationtests.qualifier.QualifierTestConstants.BIND_ACTION;
import static dev.rico.integrationtests.qualifier.QualifierTestConstants.DUMMY_ACTION;
import static dev.rico.integrationtests.qualifier.QualifierTestConstants.QUALIFIER_CONTROLLER_NAME;
import static dev.rico.integrationtests.qualifier.QualifierTestConstants.UNBIND_ACTION;
import static org.testng.Assert.assertEquals;

@SpringBootTest(classes = TestConfiguration.class)
public class QualifierControllerTest extends SpringTestNGControllerTest {

    private ControllerUnderTest<QualifierTestBean> controller;

    @BeforeMethod
    public void init() {
        controller = createController(QUALIFIER_CONTROLLER_NAME);
    }

    @AfterMethod
    public void destroy() {
        controller.destroy();
    }

    @Test
    public void testQualifier1() {

        //given:
        final QualifierTestSubBean qualifierTestSubBeanOne = controller.getModel().getQualifierTestSubBeanOneValue();
        final QualifierTestSubBean qualifierTestSubBeanTwo = controller.getModel().getQualifierTestSubBeanTwoValue();

        //when:
        setSubBeanValue(qualifierTestSubBeanOne, 42, true, "Test1");
        controller.invoke(DUMMY_ACTION);

        //then:
        assertSubBeanValue(qualifierTestSubBeanOne, 42, true, "Test1");
        assertSubBeanValue(qualifierTestSubBeanTwo, 42, true, "Test1");
    }

    @Test
    public void testQualifier1With2Controllers() {

        //given:
        final ControllerUnderTest<QualifierTestBean> controller2 = createController(QUALIFIER_CONTROLLER_NAME);
        final QualifierTestSubBean qualifierTestSubBeanOne = controller.getModel().getQualifierTestSubBeanOneValue();
        final QualifierTestSubBean qualifierTestSubBeanTwo = controller2.getModel().getQualifierTestSubBeanOneValue();

        //when:
        setSubBeanValue(qualifierTestSubBeanOne, 42, true, "Test1");

        //then:
        assertSubBeanValue(qualifierTestSubBeanOne, 42, true, "Test1");
        assertSubBeanValue(qualifierTestSubBeanTwo, 42, true, "Test1");

        controller2.destroy();
    }

    @Test
    public void testQualifier2With2Controllers() {
        //given:
        final ControllerUnderTest<QualifierTestBean> controller2 = createController(QUALIFIER_CONTROLLER_NAME);
        final QualifierTestSubBean qualifierTestSubBeanOne = controller.getModel().getQualifierTestSubBeanOneValue();
        final QualifierTestSubBean qualifierTestSubBeanTwo = controller2.getModel().getQualifierTestSubBeanOneValue();

        //when:
        setSubBeanValue(qualifierTestSubBeanTwo, 44, true, "Test2");

        //then:
        assertSubBeanValue(qualifierTestSubBeanOne, 44, true, "Test2");
        assertSubBeanValue(qualifierTestSubBeanTwo, 44, true, "Test2");

        controller2.destroy();

    }

    @Test
    public void testQualifier2() {
        //given:
        final QualifierTestSubBean qualifierTestSubBeanOne = controller.getModel().getQualifierTestSubBeanOneValue();
        final QualifierTestSubBean qualifierTestSubBeanTwo = controller.getModel().getQualifierTestSubBeanTwoValue();

        //when:
        setSubBeanValue(qualifierTestSubBeanTwo, 44, true, "Test2");
        controller.invoke(DUMMY_ACTION);

        //then:
        assertSubBeanValue(qualifierTestSubBeanOne, 44, true, "Test2");
        assertSubBeanValue(qualifierTestSubBeanTwo, 44, true, "Test2");
    }

    @Test
    public void testQualifierWithNullValue() {

        //given:
        final QualifierTestSubBean qualifierTestSubBeanOne = controller.getModel().getQualifierTestSubBeanOneValue();
        final QualifierTestSubBean qualifierTestSubBeanTwo = controller.getModel().getQualifierTestSubBeanTwoValue();

        //when:
        setSubBeanValue(qualifierTestSubBeanOne, 42, true, "Test1");
        setSubBeanValue(qualifierTestSubBeanOne, 42, true, null);

        controller.invoke(DUMMY_ACTION);

        //then:
        assertSubBeanValue(qualifierTestSubBeanOne, 42, true, null);
        assertSubBeanValue(qualifierTestSubBeanTwo, 42, true, null);
    }


    @Test
    public void testQualifierUnbind() {
        //given:
        final QualifierTestSubBean qualifierTestSubBeanOne = controller.getModel().getQualifierTestSubBeanOneValue();
        final QualifierTestSubBean qualifierTestSubBeanTwo = controller.getModel().getQualifierTestSubBeanTwoValue();

        //when:
        setSubBeanValue(qualifierTestSubBeanOne, 42, true, "Test1");
        controller.invoke(UNBIND_ACTION);
        setSubBeanValue(qualifierTestSubBeanOne, 44, false, "Test2");

        //then:
        assertSubBeanValue(qualifierTestSubBeanOne, 44, false, "Test2");
        assertSubBeanValue(qualifierTestSubBeanTwo, 42, true, "Test1");


    }

    @Test
    public void testQualifierUnbindWith2Controllers() {
        //given:
        final ControllerUnderTest<QualifierTestBean> controller2 = createController(QUALIFIER_CONTROLLER_NAME);
        final QualifierTestSubBean qualifierTestSubBeanOne = controller.getModel().getQualifierTestSubBeanOneValue();
        final QualifierTestSubBean qualifierTestSubBeanTwo = controller2.getModel().getQualifierTestSubBeanOneValue();

        //when:
        setSubBeanValue(qualifierTestSubBeanOne, 42, true, "Test1");
        controller.invoke(UNBIND_ACTION);
        setSubBeanValue(qualifierTestSubBeanOne, 44, false, "Test2");

        //then:
        assertSubBeanValue(qualifierTestSubBeanOne, 44, false, "Test2");
        assertSubBeanValue(qualifierTestSubBeanTwo, 42, true, "Test1");

        controller2.destroy();
    }

    @Test
    public void testQualifierNotBound() {
        //given:
        final QualifierTestSubBean qualifierTestSubBeanOne = controller.getModel().getQualifierTestSubBeanOneValue();
        final QualifierTestSubBean qualifierTestSubBeanTwo = controller.getModel().getQualifierTestSubBeanTwoValue();

        //when:
        controller.invoke(UNBIND_ACTION);
        setSubBeanValue(qualifierTestSubBeanOne, 42, true, "Test1");
        controller.invoke(DUMMY_ACTION);

        //then:
        assertSubBeanValue(qualifierTestSubBeanOne, 42, true, "Test1");
        assertSubBeanValue(qualifierTestSubBeanTwo, null, null, null);

    }

    @Test
    public void testQualifierNotBoundWith2Controllers() {
        //given:
        final ControllerUnderTest<QualifierTestBean> controller2 = createController(QUALIFIER_CONTROLLER_NAME);

        final QualifierTestSubBean qualifierTestSubBeanOne = controller.getModel().getQualifierTestSubBeanOneValue();
        final QualifierTestSubBean qualifierTestSubBeanTwo = controller2.getModel().getQualifierTestSubBeanOneValue();

        //when:
        controller.invoke(UNBIND_ACTION);
        setSubBeanValue(qualifierTestSubBeanOne, 42, true, "Test1");
        controller.invoke(DUMMY_ACTION);

        //then:
        assertSubBeanValue(qualifierTestSubBeanOne, 42, true, "Test1");
        assertSubBeanValue(qualifierTestSubBeanTwo, null, null, null);
        controller2.destroy();
    }

    @Test
    public void testQualifierRebind() {
        //given:
        final QualifierTestSubBean qualifierTestSubBeanOne = controller.getModel().getQualifierTestSubBeanOneValue();
        final QualifierTestSubBean qualifierTestSubBeanTwo = controller.getModel().getQualifierTestSubBeanTwoValue();

        //when:
        controller.invoke(UNBIND_ACTION);
        setSubBeanValue(qualifierTestSubBeanTwo, 42, true, "Test1");
        controller.invoke(BIND_ACTION);

        //then:
        assertSubBeanValue(qualifierTestSubBeanOne, null, null, null);
        assertSubBeanValue(qualifierTestSubBeanTwo, 42, true, "Test1");
    }

    @Test
    public void testQualifierRebindWith2Controllers() {
        //given:
        final ControllerUnderTest<QualifierTestBean> controller2 = createController(QUALIFIER_CONTROLLER_NAME);

        final QualifierTestSubBean qualifierTestSubBeanOne = controller.getModel().getQualifierTestSubBeanOneValue();
        final QualifierTestSubBean qualifierTestSubBeanTwo = controller2.getModel().getQualifierTestSubBeanOneValue();

        //when:
        controller.invoke(UNBIND_ACTION);
        setSubBeanValue(qualifierTestSubBeanTwo, 42, true, "Test1");
        controller.invoke(BIND_ACTION);

        //then:
        assertSubBeanValue(qualifierTestSubBeanOne, null, null, null);
        assertSubBeanValue(qualifierTestSubBeanTwo, 42, true, "Test1");

        controller2.destroy();
    }

    @Test
    public void testQualifierChangeAfterRebind() {
        //given:
        final QualifierTestSubBean qualifierTestSubBeanOne = controller.getModel().getQualifierTestSubBeanOneValue();
        final QualifierTestSubBean qualifierTestSubBeanTwo = controller.getModel().getQualifierTestSubBeanTwoValue();

        //when:
        controller.invoke(UNBIND_ACTION);
        setSubBeanValue(qualifierTestSubBeanOne, 42, true, "Test1");
        controller.invoke(BIND_ACTION);
        setSubBeanValue(qualifierTestSubBeanTwo, 44, false, "Test2");

        //then:
        assertSubBeanValue(qualifierTestSubBeanOne, 44, false, "Test2");
        assertSubBeanValue(qualifierTestSubBeanTwo, 44, false, "Test2");
    }

    @Test
    public void testQualifierChangeAfterRebindWith2Controllers() {
        //given:
        final ControllerUnderTest<QualifierTestBean> controller2 = createController(QUALIFIER_CONTROLLER_NAME);

        final QualifierTestSubBean qualifierTestSubBeanOne = controller.getModel().getQualifierTestSubBeanOneValue();
        final QualifierTestSubBean qualifierTestSubBeanTwo = controller2.getModel().getQualifierTestSubBeanOneValue();

        //when:
        controller.invoke(UNBIND_ACTION);
        setSubBeanValue(qualifierTestSubBeanOne, 42, true, "Test1");
        controller.invoke(BIND_ACTION);
        setSubBeanValue(qualifierTestSubBeanTwo, 44, false, "Test2");

        //then:
        assertSubBeanValue(qualifierTestSubBeanOne, 44, false, "Test2");
        assertSubBeanValue(qualifierTestSubBeanTwo, 44, false, "Test2");

        controller2.destroy();
    }

    private void setSubBeanValue(final QualifierTestSubBean qualifierTestSubBean, final int intValue, final boolean booleanValue, final String stringValue) {
        qualifierTestSubBean.setBooleanValue(booleanValue);
        qualifierTestSubBean.setStringValue(stringValue);
        qualifierTestSubBean.setIntegerValue(intValue);
    }

    private void assertSubBeanValue(final QualifierTestSubBean qualifierTestSubBean, final Integer intValue, final Boolean booleanValue, final String stringValue) {
        assertEquals(qualifierTestSubBean.getBooleanValue(), booleanValue);
        assertEquals(qualifierTestSubBean.getStringValue(), stringValue);
        assertEquals(qualifierTestSubBean.getIntegerValue(), intValue);
    }
}
