package dev.rico.mqtt;

import java.util.Objects;

/**
 * Defines the quality of service (QoS) level. QoS is an agreement between the sender of a message and the receiver of a
 * message that defines the guarantee of delivery for a specific message. There are 3 QoS levels in MQTT:
 * At most once (0)
 * At least once (1)
 * Exactly once (2).
 */
public enum Qos {
    Q0, Q1, Q2;
}
