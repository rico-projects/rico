package dev.rico.internal.client.mqtt;

import dev.rico.client.Client;
import dev.rico.client.ClientConfiguration;
import dev.rico.client.concurrent.UiExecutor;
import dev.rico.client.mqtt.MqttFactory;
import dev.rico.core.Configuration;
import dev.rico.internal.mqtt.MqttClientImpl;
import dev.rico.mqtt.MqttClient;
import dev.rico.mqtt.MqttException;

public class MqttFactoryImpl implements MqttFactory {

    private final ClientConfiguration configuration;

    public MqttFactoryImpl(final ClientConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public MqttClient getClient(final String remoteUrl) throws MqttException {
        return new MqttClientImpl(remoteUrl, configuration, Client.getService(UiExecutor.class));
    }
}
