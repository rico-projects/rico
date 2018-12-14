package dev.rico.internal.mqtt;

import dev.rico.internal.core.Assert;
import dev.rico.mqtt.MqttMessage;
import dev.rico.mqtt.Qos;

public class MqttMessageImpl implements MqttMessage {

    private final byte[] payload;

    private final boolean retained;

    private final Qos qos;

    public MqttMessageImpl(final byte[] payload) {
        this(payload, false, Qos.Q1);
    }

    public MqttMessageImpl(final byte[] payload, final boolean retained) {
        this(payload, retained, Qos.Q1);
    }

    public MqttMessageImpl(final byte[] payload, final Qos qos) {
        this(payload, false, qos);
    }

    public MqttMessageImpl(final byte[] payload, final boolean retained, final Qos qos) {
        this.payload = Assert.requireNonNull(payload, "payload");
        this.retained = retained;
        this.qos = Assert.requireNonNull(qos, "qos");
    }

    @Override
    public byte[] getPayload() {
        return payload;
    }

    @Override
    public boolean isRetained() {
        return retained;
    }

    @Override
    public Qos getQos() {
        return qos;
    }
}
