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
package dev.rico.integrationtests.remoting;

import dev.rico.integrationtests.AbstractIntegrationTest;
import dev.rico.integrationtests.remoting.qualifier.QualifierTestBean;
import dev.rico.integrationtests.remoting.qualifier.QualifierTestSubBean;
import dev.rico.client.remoting.ClientContext;
import dev.rico.client.remoting.ControllerProxy;
import org.testng.Assert;
import org.testng.annotations.Test;

import static dev.rico.integrationtests.remoting.qualifier.QualifierTestConstants.BIND_ACTION;
import static dev.rico.integrationtests.remoting.qualifier.QualifierTestConstants.DUMMY_ACTION;
import static dev.rico.integrationtests.remoting.qualifier.QualifierTestConstants.QUALIFIER_CONTROLLER_NAME;
import static dev.rico.integrationtests.remoting.qualifier.QualifierTestConstants.UNBIND_ACTION;
import static org.testng.Assert.assertEquals;

public class QualifierControllerTest extends AbstractRemotingIntegrationTest {

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Test if QualifierTestSubBeanTwo is sync when updating QualifierTestSubBean")
    public void testQualifier1(String containerType, String endpoint) {
        try {
            final ClientContext context = connect(endpoint);
            final ControllerProxy<QualifierTestBean> controller = createController(context, QUALIFIER_CONTROLLER_NAME);

            //given:
            final QualifierTestSubBean qualifierTestSubBeanOne = controller.getModel().getQualifierTestSubBeanOneValue();
            final QualifierTestSubBean qualifierTestSubBeanTwo = controller.getModel().getQualifierTestSubBeanTwoValue();

            //when:
            setSubBeanValue(qualifierTestSubBeanOne, 42, true, "Test1");

            invoke(controller, DUMMY_ACTION, containerType);

            //then:
            assertSubBeanValue(qualifierTestSubBeanOne, 42, true, "Test1");
            assertSubBeanValue(qualifierTestSubBeanTwo, 42, true, "Test1");

            //Destroy and Disconnect Controller
            destroy(controller, endpoint);
            disconnect(context, endpoint);

        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Test if QualifierTestSubBeanTwo is sync when updating QualifierTestSubBean")
    public void testQualifier1With2Controllers(String containerType, String endpoint) {
        try {
            final ClientContext context = connect(endpoint);
            final ControllerProxy<QualifierTestBean> controller1 = createController(context, QUALIFIER_CONTROLLER_NAME);
            final ControllerProxy<QualifierTestBean> controller2 = createController(context, QUALIFIER_CONTROLLER_NAME);

            //given:
            final QualifierTestSubBean qualifierTestSubBeanOne = controller1.getModel().getQualifierTestSubBeanOneValue();
            final QualifierTestSubBean qualifierTestSubBeanTwo = controller2.getModel().getQualifierTestSubBeanOneValue();

            //when:
            setSubBeanValue(qualifierTestSubBeanOne, 42, true, "Test1");

            invoke(controller1, DUMMY_ACTION, containerType);

            //then:
            assertSubBeanValue(qualifierTestSubBeanOne, 42, true, "Test1");
            assertSubBeanValue(qualifierTestSubBeanTwo, 42, true, "Test1");

            //Destroy and Disconnect Controller
            destroy(controller1, endpoint);
            destroy(controller2, endpoint);
            disconnect(context, endpoint);

        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Test if QualifierTestSubBeanTwo is sync when updating QualifierTestSubBean")
    public void testQualifierNullValue(String containerType, String endpoint) {
        try {
            final ClientContext context = connect(endpoint);
            final ControllerProxy<QualifierTestBean> controller = createController(context, QUALIFIER_CONTROLLER_NAME);

            //given:
            final QualifierTestSubBean qualifierTestSubBeanOne = controller.getModel().getQualifierTestSubBeanOneValue();
            final QualifierTestSubBean qualifierTestSubBeanTwo = controller.getModel().getQualifierTestSubBeanTwoValue();

            //when:
            setSubBeanValue(qualifierTestSubBeanOne, 42, true, "Test1");
            setSubBeanValue(qualifierTestSubBeanOne, 42, true, null);

            invoke(controller, DUMMY_ACTION, containerType);

            //then:
            assertSubBeanValue(qualifierTestSubBeanOne, 42, true, null);
            assertSubBeanValue(qualifierTestSubBeanTwo, 42, true, null);

            //Destroy and Disconnect Controller
            destroy(controller, endpoint);
            disconnect(context, endpoint);

        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Test if QualifierTestSubBean is sync when updating QualifierTestSubBeanTwo")
    public void testQualifier2(String containerType, String endpoint) {
        try {

            final ClientContext context = connect(endpoint);
            final ControllerProxy<QualifierTestBean> controller = createController(context, QUALIFIER_CONTROLLER_NAME);

            //given:
            final QualifierTestSubBean qualifierTestSubBeanOne = controller.getModel().getQualifierTestSubBeanOneValue();
            final QualifierTestSubBean qualifierTestSubBeanTwo = controller.getModel().getQualifierTestSubBeanTwoValue();

            //when:
            setSubBeanValue(qualifierTestSubBeanTwo, 44, true, "Test2");

            invoke(controller, DUMMY_ACTION, containerType);

            //then:
            assertSubBeanValue(qualifierTestSubBeanOne, 44, true, "Test2");
            assertSubBeanValue(qualifierTestSubBeanTwo, 44, true, "Test2");

            //Destroy and Disconnect Controller
            destroy(controller, endpoint);
            disconnect(context, endpoint);

        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Test Qualifier Unbind")
    public void testQualifierUnbind(String containerType, String endpoint) {
        try {
            final ClientContext context = connect(endpoint);
            final ControllerProxy<QualifierTestBean> controller = createController(context, QUALIFIER_CONTROLLER_NAME);

            //given:
            final QualifierTestSubBean qualifierTestSubBeanOne = controller.getModel().getQualifierTestSubBeanOneValue();
            final QualifierTestSubBean qualifierTestSubBeanTwo = controller.getModel().getQualifierTestSubBeanTwoValue();

            //when:
            setSubBeanValue(qualifierTestSubBeanOne, 42, true, "Test1");
            invoke(controller, UNBIND_ACTION, containerType);
            setSubBeanValue(qualifierTestSubBeanOne, 44, false, "Test2");

            //then:
            assertSubBeanValue(qualifierTestSubBeanOne, 44, false, "Test2");
            assertSubBeanValue(qualifierTestSubBeanTwo, 42, true, "Test1");

            //Destroy and Disconnect Controller
            destroy(controller, endpoint);
            disconnect(context, endpoint);

        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Test Qualifier Unbind")
    public void testQualifierUnbindWith2Controlers(String containerType, String endpoint) {
        try {
            final ClientContext context = connect(endpoint);
            final ControllerProxy<QualifierTestBean> controller1 = createController(context, QUALIFIER_CONTROLLER_NAME);
            final ControllerProxy<QualifierTestBean> controller2 = createController(context, QUALIFIER_CONTROLLER_NAME);

            //given:
            final QualifierTestSubBean qualifierTestSubBeanOne = controller1.getModel().getQualifierTestSubBeanOneValue();
            final QualifierTestSubBean qualifierTestSubBeanTwo = controller2.getModel().getQualifierTestSubBeanOneValue();

            //when:
            setSubBeanValue(qualifierTestSubBeanOne, 42, true, "Test1");
            invoke(controller1, UNBIND_ACTION, containerType);
            setSubBeanValue(qualifierTestSubBeanOne, 44, false, "Test2");

            //then:
            assertSubBeanValue(qualifierTestSubBeanOne, 44, false, "Test2");
            assertSubBeanValue(qualifierTestSubBeanTwo, 42, true, "Test1");

            //Destroy and Disconnect Controller
            destroy(controller1, endpoint);
            destroy(controller2, endpoint);
            disconnect(context, endpoint);

        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Test Qualifier Not Bound")
    public void testQualifierNotBound(String containerType, String endpoint) {
        try {
            final ClientContext context = connect(endpoint);
            final ControllerProxy<QualifierTestBean> controller = createController(context, QUALIFIER_CONTROLLER_NAME);

            //given:
            final QualifierTestSubBean qualifierTestSubBeanOne = controller.getModel().getQualifierTestSubBeanOneValue();
            final QualifierTestSubBean qualifierTestSubBeanTwo = controller.getModel().getQualifierTestSubBeanTwoValue();

            //when:
            invoke(controller, UNBIND_ACTION, containerType);
            setSubBeanValue(qualifierTestSubBeanOne, 42, true, "Test1");
            invoke(controller, DUMMY_ACTION, containerType);

            //then:
            assertSubBeanValue(qualifierTestSubBeanOne, 42, true, "Test1");
            assertSubBeanValue(qualifierTestSubBeanTwo, null, null, null);

            //Destroy and Disconnect Controller
            destroy(controller, endpoint);
            disconnect(context, endpoint);

        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Test Qualifier Not Bound")
    public void testQualifierNotBoundWith2Controllers(String containerType, String endpoint) {
        try {
            final ClientContext context = connect(endpoint);
            final ControllerProxy<QualifierTestBean> controller1 = createController(context, QUALIFIER_CONTROLLER_NAME);
            final ControllerProxy<QualifierTestBean> controller2 = createController(context, QUALIFIER_CONTROLLER_NAME);

            //given:
            final QualifierTestSubBean qualifierTestSubBeanOne = controller1.getModel().getQualifierTestSubBeanOneValue();
            final QualifierTestSubBean qualifierTestSubBeanTwo = controller2.getModel().getQualifierTestSubBeanOneValue();

            //when:
            invoke(controller1, UNBIND_ACTION, containerType);
            setSubBeanValue(qualifierTestSubBeanOne, 42, true, "Test1");
            invoke(controller1, DUMMY_ACTION, containerType);

            //then:
            assertSubBeanValue(qualifierTestSubBeanOne, 42, true, "Test1");
            assertSubBeanValue(qualifierTestSubBeanTwo, null, null, null);

            //Destroy and Disconnect Controller
            destroy(controller1, endpoint);
            destroy(controller2, endpoint);
            disconnect(context, endpoint);

        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Test Qualifier Rebind")
    public void testQualifierRebind(String containerType, String endpoint) {
        try {
            final ClientContext context = connect(endpoint);
            final ControllerProxy<QualifierTestBean> controller = createController(context, QUALIFIER_CONTROLLER_NAME);

            //given:
            final QualifierTestSubBean qualifierTestSubBeanOne = controller.getModel().getQualifierTestSubBeanOneValue();
            final QualifierTestSubBean qualifierTestSubBeanTwo = controller.getModel().getQualifierTestSubBeanTwoValue();

            //when:
            invoke(controller, UNBIND_ACTION, containerType);
            setSubBeanValue(qualifierTestSubBeanTwo, 42, true, "Test1");
            invoke(controller, BIND_ACTION, containerType);

            //then:
            assertSubBeanValue(qualifierTestSubBeanOne, null, null, null);
            assertSubBeanValue(qualifierTestSubBeanTwo, 42, true, "Test1");

            //Destroy and Disconnect Controller
            destroy(controller, endpoint);
            disconnect(context, endpoint);

        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Test Qualifier Rebind")
    public void testQualifierRebindWith2Controllers(String containerType, String endpoint) {
        try {
            final ClientContext context = connect(endpoint);
            final ControllerProxy<QualifierTestBean> controller1 = createController(context, QUALIFIER_CONTROLLER_NAME);
            final ControllerProxy<QualifierTestBean> controller2 = createController(context, QUALIFIER_CONTROLLER_NAME);

            //given:
            final QualifierTestSubBean qualifierTestSubBeanOne = controller1.getModel().getQualifierTestSubBeanOneValue();
            final QualifierTestSubBean qualifierTestSubBeanTwo = controller2.getModel().getQualifierTestSubBeanOneValue();

            //when:
            invoke(controller1, UNBIND_ACTION, containerType);
            setSubBeanValue(qualifierTestSubBeanTwo, 42, true, "Test1");
            invoke(controller1, BIND_ACTION, containerType);

            //then:
            assertSubBeanValue(qualifierTestSubBeanOne, null, null, null);
            assertSubBeanValue(qualifierTestSubBeanTwo, 42, true, "Test1");

            //Destroy and Disconnect Controller
            destroy(controller1, endpoint);
            destroy(controller2, endpoint);
            disconnect(context, endpoint);

        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Test Qualifier Change After Rebind")
    public void testQualifierChangeAfterRebind(String containerType, String endpoint) {
        try {
            final ClientContext context = connect(endpoint);
            final ControllerProxy<QualifierTestBean> controller = createController(context, QUALIFIER_CONTROLLER_NAME);

            //given:
            final QualifierTestSubBean qualifierTestSubBeanOne = controller.getModel().getQualifierTestSubBeanOneValue();
            final QualifierTestSubBean qualifierTestSubBeanTwo = controller.getModel().getQualifierTestSubBeanTwoValue();

            //when:
            invoke(controller, UNBIND_ACTION, containerType);
            setSubBeanValue(qualifierTestSubBeanOne, 42, true, "Test1");
            invoke(controller, BIND_ACTION, containerType);
            setSubBeanValue(qualifierTestSubBeanTwo, 44, false, "Test2");

            //then:
            assertSubBeanValue(qualifierTestSubBeanOne, 44, false, "Test2");
            assertSubBeanValue(qualifierTestSubBeanTwo, 44, false, "Test2");

            //Destroy and Disconnect Controller
            destroy(controller, endpoint);
            disconnect(context, endpoint);

        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
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