package dev.rico.mqtt;


import dev.rico.core.functional.Subscription;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public interface MqttClient {

    CompletableFuture<Void> connect();

    CompletableFuture<Void> disconnect();

    //TODO: Do we need this
    Optional<String> clientId();

    CompletableFuture<Void> publish(String topic, MqttMessage message);

    CompletableFuture<Subscription> subscribe(String topicFilter, Qos qos, BiConsumer<String, MqttMessage> messageListener);

}
