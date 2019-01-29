package dev.rico.mqtt;

import dev.rico.internal.mqtt.MqttMessageImpl;

/**
 * Definition of a mqtt message.
 */
public interface MqttMessage {

    byte[] getPayload();

    boolean isRetained();

    Qos getQos();

    static MqttMessage of(final byte[] payload) {
        return new MqttMessageImpl(payload);
    }

    static MqttMessage of(final byte[] payload, final boolean retained) {
        return new MqttMessageImpl(payload, retained);
    }

    static MqttMessage of(final byte[] payload, final Qos qos) {
        return new MqttMessageImpl(payload, qos);
    }

    static MqttMessage of(final byte[] payload, final boolean retained, final Qos qos) {
        return new MqttMessageImpl(payload, retained, qos);
    }
}
