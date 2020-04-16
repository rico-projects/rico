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
package dev.rico.internal.server.remoting.legacy.action;

import dev.rico.internal.remoting.legacy.communication.CreatePresentationModelCommand;
import dev.rico.internal.remoting.legacy.communication.ValueChangedCommand;
import dev.rico.internal.remoting.server.legacy.DTO;
import dev.rico.internal.remoting.server.legacy.ServerAttribute;
import dev.rico.internal.remoting.server.legacy.action.AbstractServerAction;
import dev.rico.internal.remoting.server.legacy.communication.ActionRegistry;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;

public class AbstractServerActionTests {

    @BeforeMethod
    protected void setUp() throws Exception {
        action = new AbstractServerAction() {
            @Override
            public void registerIn(ActionRegistry registry) {

            }

        };
        action.setResponse(new ArrayList<>());
    }

    @Test
    public void testCreatePresentationModel() {
        action.presentationModel("p1", "person", new DTO());
        Assert.assertEquals(1, action.getResponse().size());
        Assert.assertEquals(CreatePresentationModelCommand.class, action.getResponse().get(0).getClass());
        Assert.assertEquals("p1", ((CreatePresentationModelCommand) action.getResponse().get(0)).getPmId());
        Assert.assertEquals("person", ((CreatePresentationModelCommand) action.getResponse().get(0)).getPmType());
    }

    public void testChangeValue() {
        action.changeValue(new ServerAttribute("attr", "initial"), "newValue");
        Assert.assertEquals(1, action.getResponse().size());
        Assert.assertEquals(ValueChangedCommand.class, action.getResponse().get(0).getClass());
    }

    private AbstractServerAction action;
}
