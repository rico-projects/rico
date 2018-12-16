package dev.rico.server.mqtt;

public interface MqttConnectionListener {

    void onConnected(MqttConnection connection);

    void onDisconnected(MqttConnection connection);

}
