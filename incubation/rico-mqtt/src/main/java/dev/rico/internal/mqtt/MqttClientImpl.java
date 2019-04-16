package dev.rico.internal.mqtt;

import dev.rico.core.Configuration;
import dev.rico.core.functional.Subscription;
import dev.rico.internal.core.Assert;
import dev.rico.mqtt.MqttClient;
import dev.rico.mqtt.MqttException;
import dev.rico.mqtt.MqttMessage;
import dev.rico.mqtt.Qos;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttPingSender;
import org.eclipse.paho.client.mqttv3.TimerPingSender;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.BiConsumer;

public class MqttClientImpl implements MqttClient {

    private final MqttAsyncClient internalClient;

    private final MqttConnectOptions connectOptions;

    private final Executor subscriptionCaller;

    public MqttClientImpl(final String serverURI, final Configuration configuration, final Executor subscriptionCaller) throws MqttException {
        Assert.requireNonNull(configuration, "configuration");
        this.subscriptionCaller = Assert.requireNonNull(subscriptionCaller, "subscriptionCaller");
        //TODO: configure based on config
        connectOptions = new MqttConnectOptions();
        final String clientId = UUID.randomUUID().toString();
        final MqttClientPersistence clientPersistence = new MqttDefaultFilePersistence();
        final MqttPingSender pingSender = new TimerPingSender();
        final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);
        try {
            internalClient = new MqttAsyncClient(serverURI, clientId, clientPersistence, pingSender, executorService);
        } catch (final Exception e) {
            throw new MqttException(e);
        }
    }

    @Override
    public CompletableFuture<Void> connect() {
        final CompletableFuture<Void> result = new CompletableFuture<>();
        final IMqttActionListener listener = new IMqttActionListener() {
            @Override
            public void onSuccess(final IMqttToken asyncActionToken) {
                result.complete(null);
            }

            @Override
            public void onFailure(final IMqttToken asyncActionToken, final Throwable exception) {
                result.completeExceptionally(exception);
            }
        };
        try {
            internalClient.connect(connectOptions, null, listener);
        } catch (final Exception e) {
            result.completeExceptionally(new MqttException("MQTT error", e));
        }
        return result;
    }

    @Override
    public CompletableFuture<Void> disconnect() {
        final CompletableFuture<Void> result = new CompletableFuture<>();
        final IMqttActionListener listener = new IMqttActionListener() {
            @Override
            public void onSuccess(final IMqttToken asyncActionToken) {
                result.complete(null);
            }

            @Override
            public void onFailure(final IMqttToken asyncActionToken, final Throwable exception) {
                result.completeExceptionally(exception);
            }
        };
        try {
            internalClient.disconnect(null, listener);
        } catch (final Exception e) {
            result.completeExceptionally(new MqttException("MQTT error", e));
        }
        return result;
    }

    public Optional<String> clientId() {
        return Optional.ofNullable(internalClient.getClientId());
    }

    @Override
    public CompletableFuture<Void> publish(final String topic, final MqttMessage message) {
        Assert.requireNonNull(message, "message");
        final CompletableFuture<Void> result = new CompletableFuture<>();
        final IMqttActionListener listener = new IMqttActionListener() {
            @Override
            public void onSuccess(final IMqttToken asyncActionToken) {
                result.complete(null);
            }

            @Override
            public void onFailure(final IMqttToken asyncActionToken, final Throwable exception) {
                result.completeExceptionally(exception);
            }
        };
        final org.eclipse.paho.client.mqttv3.MqttMessage innerMessage = new org.eclipse.paho.client.mqttv3.MqttMessage();
        innerMessage.setPayload(message.getPayload());
        innerMessage.setRetained(message.isRetained());
        innerMessage.setQos(toInt(message.getQos()));
        try {
            internalClient.publish(topic, innerMessage, null, listener);
        } catch (final Exception e) {
            result.completeExceptionally(new MqttException("MQTT error", e));
        }
        return result;
    }

    private int toInt(final Qos qos) {
        if(Objects.equals(qos, Qos.Q0)) {
            return 0;
        }
        if(Objects.equals(qos, Qos.Q1)) {
            return 1;
        }
        if(Objects.equals(qos, Qos.Q2)) {
            return 2;
        }
        throw new IllegalArgumentException("Illegal QoS: " + qos);
    }

    private Qos of(int qos) {
        if(qos == 0) {
            return Qos.Q0;
        }
        if(qos == 1) {
            return Qos.Q1;
        }
        if(qos == 2) {
            return Qos.Q2;
        }
        throw new IllegalArgumentException("Illegal QoS: " + qos);
    }

    @Override
    public CompletableFuture<Subscription> subscribe(final String topic, final Qos qos, final BiConsumer<String, MqttMessage> messageListener) {
        Assert.requireNonNull(messageListener, "messageListener");
        final CompletableFuture<Subscription> result = new CompletableFuture<>();
        final IMqttActionListener actionListener = new IMqttActionListener() {
            @Override
            public void onSuccess(final IMqttToken asyncActionToken) {
                result.complete(() -> {
                    try {
                        internalClient.unsubscribe(topic);
                    } catch (final org.eclipse.paho.client.mqttv3.MqttException e) {
                        throw new RuntimeException("MQQT error", e);
                    }
                });
            }

            @Override
            public void onFailure(final IMqttToken asyncActionToken, final Throwable exception) {
                result.completeExceptionally(exception);
            }
        };
        final IMqttMessageListener internalMessageListener = new IMqttMessageListener() {
            @Override
            public void messageArrived(final String topic, final org.eclipse.paho.client.mqttv3.MqttMessage message) throws Exception {
                Assert.requireNonNull(message, "message");
                final MqttMessage receivedMessage = MqttMessage.of(message.getPayload(), message.isRetained(), of(message.getQos()));
                subscriptionCaller.execute(() -> messageListener.accept(topic, receivedMessage));
            }
        };
        try {
            internalClient.subscribe(topic, toInt(qos), null, actionListener, internalMessageListener);
        } catch (final org.eclipse.paho.client.mqttv3.MqttException e) {
           result.completeExceptionally(new MqttException("MQTT error", e));
        }
        return null;
    }
}
