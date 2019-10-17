package dev.rico.event;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TopicTest {

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void checkNonBlank() {
        Topic.create("");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void checkNonNull() {
        Topic.create(null);
    }

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
