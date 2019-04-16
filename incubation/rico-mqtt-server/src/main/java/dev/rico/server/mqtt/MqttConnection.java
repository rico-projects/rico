package dev.rico.server.mqtt;

import dev.rico.mqtt.MqttMessage;

import java.util.concurrent.CompletableFuture;

public interface MqttConnection {

    CompletableFuture<Void> publish(String topic, MqttMessage message);

    String getEndpoint();
}
