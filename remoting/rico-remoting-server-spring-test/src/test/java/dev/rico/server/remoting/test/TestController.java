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
package dev.rico.server.remoting.test;

import dev.rico.remoting.BeanManager;
import dev.rico.remoting.server.RemotingAction;
import dev.rico.remoting.server.RemotingController;
import dev.rico.remoting.server.RemotingModel;
import dev.rico.remoting.server.event.RemotingEventBus;
import dev.rico.remoting.server.event.MessageEvent;
import dev.rico.remoting.server.event.MessageListener;
import dev.rico.remoting.server.event.Topic;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@RemotingController("TestController")
public class TestController {

    @Autowired
    private BeanManager beanManager;

    @Autowired
    private RemotingEventBus eventBus;

    @RemotingModel
    private TestModel model;

    private static Topic<String> TEST_TOPIC = Topic.create();

    @PostConstruct
    public void init() {
        eventBus.subscribe(TEST_TOPIC, new MessageListener<>() {
            @Override
            public void onMessage(MessageEvent<String> message) {
                model.setValue(message.getData());
            }
        });
    }

    @RemotingAction("sendEvent")
    public void sendEvent() {
        eventBus.publish(TEST_TOPIC, "changed by eventBus!");
    }

    @RemotingAction("action")
    public void doSomeAction() {
        model.setValue("Hello Rico Test");
    }

    @RemotingAction("addToList")
    public void addToList() {
        model.getItems().add("Hallo");
    }

    @RemotingAction("addBeanToList")
    public void addBeanToList() {
        TestSubModel bean = beanManager.create(TestSubModel.class);
        bean.setValue("I'm a subbean");
        model.getInternModels().add(bean);
    }
}
