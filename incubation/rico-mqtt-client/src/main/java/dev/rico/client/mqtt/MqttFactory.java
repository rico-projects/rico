package dev.rico.client.mqtt;

import dev.rico.mqtt.MqttClient;
import dev.rico.mqtt.MqttException;

public interface MqttFactory {

    MqttClient getClient(String remoteUrl) throws MqttException;

}
