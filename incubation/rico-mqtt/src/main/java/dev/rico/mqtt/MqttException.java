package dev.rico.mqtt;

/**
 * General exception for the mqtt module of Rico.
 */
public class MqttException extends Exception {

    /**
     * Constructor
     * @param message the message
     */
    public MqttException(final String message) {
        super(message);
    }

    /**
     * Constructor
     * @param message the message
     * @param cause the cause
     */
    public MqttException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor
     * @param cause the cause
     */
    public MqttException(final Throwable cause) {
        super(cause);
    }
}
