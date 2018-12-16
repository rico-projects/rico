package dev.rico.internal.server.mqtt;

import dev.rico.mqtt.MqttMessage;
import dev.rico.server.mqtt.MqttConnection;
import dev.rico.server.mqtt.MqttController;
import dev.rico.server.mqtt.MqttSubscription;

@MqttController
public class TestController {

    private final MqttConnection connection;

    //INJECT
    public TestController(final MqttConnection connection) {
        this.connection = connection;
        connection.publish("initializer", MqttMessage.of("huuh".getBytes()));
    }

    @MqttSubscription("temp-changed")
    public void onTemperatureChanged(final MqttMessage message) {

    }

}
