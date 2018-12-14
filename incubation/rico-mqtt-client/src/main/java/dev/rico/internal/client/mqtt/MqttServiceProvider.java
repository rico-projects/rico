package dev.rico.internal.client.mqtt;

import dev.rico.client.ClientConfiguration;
import dev.rico.client.mqtt.MqttFactory;
import dev.rico.internal.client.AbstractServiceProvider;

public class MqttServiceProvider extends AbstractServiceProvider<MqttFactory> {

    public MqttServiceProvider() {
        super(MqttFactory.class);
    }

    @Override
    protected MqttFactory createService(final ClientConfiguration configuration) {
        return new MqttFactoryImpl(configuration);
    }
}
