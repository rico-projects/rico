package dev.rico.client.mqtt;

import dev.rico.mqtt.MqttClient;
import dev.rico.mqtt.MqttException;

/**
 * Factory for {@link MqttClient}
 */
public interface MqttFactory {

    /**
     * Returns a {@link MqttClient} based on the given url.
     * @param remoteUrl the url of the broker endpoint
     * @return the client
     * @throws MqttException if the client can not be created
     */
    MqttClient getClient(String remoteUrl) throws MqttException;

}
