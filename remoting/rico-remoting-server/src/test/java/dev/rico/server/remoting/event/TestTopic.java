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
package dev.rico.server.remoting.event;

import dev.rico.server.remoting.event.Topic;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestTopic {

    @Test
    public void testUniqueId() {
        Topic<String> topicA = Topic.create();
        for (int i = 0; i < 1000; i++) {
            Topic<String> topicB = Topic.create();
            Assert.assertNotEquals(topicA, topicB);
        }
    }

    @Test
    public void testEquals() {
        Topic<String> topicA = Topic.create("TopicA");
        Topic<String> topicB = Topic.create("TopicA");
        Assert.assertEquals(topicA, topicB);
    }

    @Test
    public void testName() {
        Topic<String> topicA = Topic.create("TopicA");
        Topic<String> topicB = Topic.create("TopicB");
        Assert.assertEquals(topicA.getName(), "TopicA");
        Assert.assertEquals(topicB.getName(), "TopicB");
    }


}
