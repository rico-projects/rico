package dev.rico.mqtt;


import dev.rico.core.functional.Subscription;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * A mqtt client that can be used to communicate with a mqtt broker
 */
public interface MqttClient {

    /**
     * Connects the client
     * @return a {@link CompletableFuture} that can be used to observe the connection process
     */
    CompletableFuture<Void> connect();

    /**
     * Disconnects the client
     * @return a {@link CompletableFuture} that can be used to observe the disconnect process
     */
    CompletableFuture<Void> disconnect();

    /**
     * Publish a message to a specific topic
     * @param topic the topic
     * @param message the message
     * @return a {@link CompletableFuture} that can be used to observe the publish process
     */
    CompletableFuture<Void> publish(String topic, MqttMessage message);

    /**
     * Subscribes to topics that are defined by the topic filter.
     * @param topicFilter the topic filter
     * @param qos he qos
     * @param messageListener the listener
     * @return a {@link CompletableFuture} that can be used to observe the subscribe process
     */
    CompletableFuture<Subscription> subscribe(String topicFilter, Qos qos, BiConsumer<String, MqttMessage> messageListener);

}
