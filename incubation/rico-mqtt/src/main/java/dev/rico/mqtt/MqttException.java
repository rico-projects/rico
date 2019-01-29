package dev.rico.mqtt;

public class MqttException extends Exception {

    public MqttException(final String message) {
        super(message);
    }

    public MqttException(String message, Throwable cause) {
        super(message, cause);
    }

    public MqttException(Throwable cause) {
        super(cause);
    }
}
