package dev.rico.internal.server.mqtt;

import dev.rico.mqtt.MqttMessage;
import dev.rico.server.ServerListener;
import dev.rico.server.mqtt.MqttConnection;
import dev.rico.server.mqtt.MqttConnectionListener;

@ServerListener
public class SampleMqttConnectionListener implements MqttConnectionListener {

    @Override
    public void onConnected(final MqttConnection connection) {
        System.out.println("Connection created to mqtt broker with endpoint " + connection.getEndpoint());

        connection.publish("init-topic", MqttMessage.of("Hello broker".getBytes()));
    }

    @Override
    public void onDisconnected(final MqttConnection connection) {
        System.out.println("Disconnected to mqtt broker with endpoint " + connection.getEndpoint());
    }
}
