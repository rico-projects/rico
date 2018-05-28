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
package dev.rico.internal.server.remoting.controller;

import dev.rico.server.remoting.RemotingAction;
import dev.rico.server.remoting.RemotingController;
import dev.rico.server.remoting.RemotingModel;
import dev.rico.server.remoting.Param;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class ControllerValidatorTest {

    private ControllerValidator controllerValidator;

    @BeforeTest
    public void setUp(){
        controllerValidator = new ControllerValidator();
    }

    @Test
    public void testValidController() throws ControllerValidationException{
        controllerValidator.validate(ValidController.class);
    }

    @Test(expectedExceptions = ControllerValidationException.class)
    public void testControllerWithoutControllerAnnotation() throws ControllerValidationException{
        controllerValidator.validate(ControllerWithoutControllerAnnotation.class);
    }
    @Test(expectedExceptions = ControllerValidationException.class)
    public void testControllerCannotBeAnInterface() throws ControllerValidationException{
        controllerValidator.validate(ControllerAsInterface.class);
    }
    @Test(expectedExceptions = ControllerValidationException.class)
    public void testControllerCannotBeAbstract() throws ControllerValidationException{
        controllerValidator.validate(ControllerAsAbstract.class);
    }
    @Test(expectedExceptions = ControllerValidationException.class)
    public void testControllerCannotBeFinal() throws ControllerValidationException{
        controllerValidator.validate(ControllerAsFinal.class);
    }
    @Test(expectedExceptions = ControllerValidationException.class)
    public void testPostConstructCannotContainParameter() throws ControllerValidationException{
        controllerValidator.validate(ControllerPostConstructWithParam.class);
    }
    @Test(expectedExceptions = ControllerValidationException.class)
    public void testPreDestroyCannotContainParameter() throws ControllerValidationException{
        controllerValidator.validate(ControllerPreDestroyWithParam.class);
    }
    @Test(expectedExceptions = ControllerValidationException.class)
    public void testControllerCannotHaveMoreThanOnePostConstruct() throws ControllerValidationException{
        controllerValidator.validate(ControllerWithMoreThanOnePostConstruct.class);
    }
    @Test(expectedExceptions = ControllerValidationException.class)
    public void testControllerCannotHaveMoreThanOnePreDestroy() throws ControllerValidationException{
        controllerValidator.validate(ControllerWithMoreThanOnePreDestroy.class);
    }
    @Test(expectedExceptions = ControllerValidationException.class)
    public void testActionMustBeVoid() throws ControllerValidationException{
        controllerValidator.validate(ControllerWithNonVoidAction.class);
    }
    @Test(expectedExceptions = ControllerValidationException.class)
    public void testActionParameterAnnotation() throws ControllerValidationException{
        controllerValidator.validate(ControllerWithActionParam.class);
    }
    @Test(expectedExceptions = ControllerValidationException.class)
    public void testControllerCanNotHaveMoreThanOneModel() throws ControllerValidationException{
        controllerValidator.validate(ControllerWithMoreThanOneModel.class);
    }

}

@RemotingController
class ValidController {

    @RemotingModel
    private TestBean testBean;

    @PostConstruct
    public void postConstruct() {
    }

    @PreDestroy
    public void preDestroy() {
    }

    @RemotingAction
    public void action(@Param String test) {
    }

    @RemotingAction
    public void action2(@Param @Nonnull String test) {
    }

    @RemotingAction
    public void action3(@Nonnull @Param String test) {
    }
}

class ControllerWithoutControllerAnnotation {

}

@RemotingController
interface ControllerAsInterface{}
@RemotingController
abstract class ControllerAsAbstract{}
@RemotingController
final class ControllerAsFinal{}
@RemotingController
class ControllerPostConstructWithParam{

    @RemotingModel
    private TestBean testBean;

    @PostConstruct
    public void postConstruct(String param) {
    }
}
@RemotingController
class ControllerWithMoreThanOnePostConstruct{

    @RemotingModel
    private TestBean testBean;

    @PostConstruct
    public void postConstruct1() {
    }
    @PostConstruct
    public void postConstruct2() {
    }
}
@RemotingController
class ControllerPreDestroyWithParam{

    @RemotingModel
    private TestBean testBean;

    @PreDestroy
    public void preDestroy(String param) {
    }
}
@RemotingController
class ControllerWithMoreThanOnePreDestroy{

    @RemotingModel
    private TestBean testBean;

    @PreDestroy
    public void preDestroy1() {
    }
    @PreDestroy
    public void preDestroy2() {
    }
}
@RemotingController
class ControllerWithNonVoidAction {

    @RemotingModel
    private TestBean testBean;

    @RemotingAction
    public String action(@Param String test) {
        return "";
    }
}
@RemotingController
class ControllerWithActionParam {

    @RemotingModel
    private TestBean testBean;

    @RemotingAction
    public void action(String test) {
    }
}
@RemotingController
class ControllerWithMoreThanOneModel {
    @RemotingModel
    private TestBean testBean1;

    @RemotingModel
    private TestBean testBean2;
}

