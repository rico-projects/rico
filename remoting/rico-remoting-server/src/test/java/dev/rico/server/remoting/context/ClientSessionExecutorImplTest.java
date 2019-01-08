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
package dev.rico.server.remoting.context;

import dev.rico.internal.server.remoting.context.ClientSessionExecutorImpl;
import dev.rico.server.remoting.ClientSessionExecutor;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.Executors;

public class ClientSessionExecutorImplTest {

    @Test
    public void testCreation() {
        try {
            ClientSessionExecutor executor = new ClientSessionExecutorImpl(Executors.newSingleThreadExecutor());
        } catch (Exception e) {
            Assert.fail("Can not create executor", e);
        }
    }

    @Test
    public void testInvalidCreation() {
        try {
            ClientSessionExecutor executor = new ClientSessionExecutorImpl(null);
            Assert.fail("Creating executor with null value should not be possible!");
        } catch (NullPointerException e) {

        }
    }

    @Test
    public void testPassingTask() {
        //given:
        ClientSessionExecutor executor = new ClientSessionExecutorImpl(Executors.newSingleThreadExecutor());

        //then:
        executor.runLaterInClientSession(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    @Test
    public void testPassingInvalidTask() {
        //given:
        ClientSessionExecutor executor = new ClientSessionExecutorImpl(Executors.newSingleThreadExecutor());

        //then:
        try {
            executor.runLaterInClientSession(null);
            Assert.fail("Passing a null value should not be possible!");
        } catch (NullPointerException e) {

        }
    }

}
