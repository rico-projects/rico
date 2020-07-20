/*
 * Copyright 2018-2019 Karakun AG.
 * Copyright 2015-2018 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.rico.remoting.client.javafx;

import dev.rico.client.Client;
import dev.rico.client.concurrent.BackgroundExecutor;
import dev.rico.client.concurrent.UiExecutor;
import dev.rico.client.javafx.FxToolkit;
import dev.rico.core.logging.Logger;
import dev.rico.core.logging.LoggerFactory;
import dev.rico.internal.core.Assert;
import dev.rico.remoting.client.ClientContext;
import dev.rico.remoting.client.ClientContextFactory;
import dev.rico.remoting.client.ClientInitializationException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.apiguardian.api.API;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * Defines a basic application class for remoting based applications that can be used like the {@link Application}
 * class. Next to the general {@link Application} class of JavaFX this class supports the remoting lifecycle.
 */
@API(since = "0.x", status = MAINTAINED)
public abstract class AbstractRemotingApplication extends Application {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractRemotingApplication.class);

    private ClientContext clientContext;

    private ClientInitializationException initializationException;

    private final List<RuntimeException> runtimeExceptionsAtInitialization = new CopyOnWriteArrayList<>();

    private Stage primaryStage;

    private final AtomicBoolean connectInProgress = new AtomicBoolean(false);

    /**
     * Returns the server url of the remoting server endpoint.
     *
     * @return the server url
     */
    protected abstract URL getServerEndpoint() throws MalformedURLException;

    /**
     * Returns the configuration for the client. As long as all the default configurations can be used
     * this method don't need to be overridden. The URL of the server will be configured by the {@link AbstractRemotingApplication#getServerEndpoint()}
     * method.
     *
     * @return The configuration for this client
     */
//    protected JavaFXConfiguration createClientConfiguration() {
//        try {
//            configuration.addRemotingExceptionHandler(e -> {
//                if (connectInProgress.get()) {
//                    runtimeExceptionsAtInitialization.add(new RuntimeException("Remoting error", e));
//                } else {
//                    onRuntimeError(primaryStage, new RuntimeException("Remoting error!", e));
//                }
//            });
//            return configuration;
//        } catch (MalformedURLException e) {
//            throw new ClientInitializationException("Client configuration cannot be created", e);
//        }
//    }
    private ClientContext createClientContext() throws Exception {
        return Client.getService(ClientContextFactory.class).create(Client.getClientConfiguration(), getServerEndpoint().toURI());
    }

    /**
     * Creates the connection to the remoting server endpoint. If this method will be overridden always call the super method.
     *
     * @throws Exception a exception if the connection can't be created
     */
    @Override
    public final void init() throws Exception {
        FxToolkit.init();

        applicationInit();

        Client.getClientConfiguration().setUncaughtExceptionHandler((Thread thread, Throwable exception) -> {
            Client.getService(UiExecutor.class).execute(() -> {
                Assert.requireNonNull(thread, "thread");
                Assert.requireNonNull(exception, "exception");

                if (connectInProgress.get()) {
                    runtimeExceptionsAtInitialization.add(new RuntimeException("Unhandled error in background thread", exception));
                } else {
                    onRuntimeError(primaryStage, new RuntimeException("Unhandled error in background thread", exception));
                }
            });
        });

        try {
            clientContext = createClientContext();
            Assert.requireNonNull(clientContext, "clientContext");

            connectInProgress.set(true);
            clientContext.connect().get(30_000, TimeUnit.MILLISECONDS);
        } catch (ClientInitializationException e) {
            initializationException = e;
        } catch (Exception e) {
            initializationException = new ClientInitializationException("Can not initialize client context", e);
        } finally {
            connectInProgress.set(false);
        }
    }

    protected void applicationInit() throws Exception {

    }

    /**
     * This method must be defined by each application to create the initial view. The method will be called on
     * the JavaFX Platform thread after the connection to the server has been created.
     *
     * @param primaryStage  the primary stage
     * @param clientContext the client context
     * @throws Exception in case of an error
     */
    protected abstract void start(Stage primaryStage, ClientContext clientContext) throws Exception;

    /**
     * This methods defines parts of the remoting lifecycle and is therefore defined as final.
     * Use the {@link AbstractRemotingApplication#start(Stage, ClientContext)} method instead.
     *
     * @param primaryStage the primary stage
     * @throws Exception in case of an error
     */
    @Override
    public final void start(final Stage primaryStage) throws Exception {
        Assert.requireNonNull(primaryStage, "primaryStage");

        this.primaryStage = primaryStage;

        if (initializationException == null) {
            if (clientContext != null) {
                try {
                    start(primaryStage, clientContext);
                } catch (Exception e) {
                    handleInitializationError(primaryStage, new ClientInitializationException("Error in application start!", e));
                }
            } else {
                handleInitializationError(primaryStage, new ClientInitializationException("No clientContext was created!"));
            }
        } else {
            handleInitializationError(primaryStage, initializationException);
        }
    }

    protected final CompletableFuture<Void> disconnect() {
        if (clientContext != null) {
            return clientContext.disconnect();
        } else {
            final CompletableFuture<Void> result = new CompletableFuture<>();
            result.complete(null);
            return result;
        }
    }

    /**
     * Whenever JavaFX calls the stop method the connection to the server will be closed.
     *
     * @throws Exception an error
     */
    @Override
    public final void stop() throws Exception {
        disconnect();
    }

    protected final CompletableFuture<Void> reconnect(final Stage primaryStage) {
        Assert.requireNonNull(primaryStage, "primaryStage");
        final CompletableFuture<Void> result = new CompletableFuture<>();
        final BackgroundExecutor backgroundExecutor = Client.getService(BackgroundExecutor.class);
        backgroundExecutor.execute(() -> {
            try {
                disconnect().get(1_000, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                LOG.warn("Can not disconnect. Trying to reconnect anyway.");
            }

            try {
                if (clientContext == null) {
                    clientContext = createClientContext();
                }
                Assert.requireNonNull(clientContext, "clientContext");

                connectInProgress.set(true);
                clientContext.connect().get(3_000, TimeUnit.MILLISECONDS);

                Platform.runLater(() -> {
                    try {
                        start(primaryStage, clientContext);
                    } catch (Exception e) {
                        handleInitializationError(primaryStage, new ClientInitializationException("Error in application reconnect", e));
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> handleInitializationError(primaryStage, new ClientInitializationException("Error in application reconnect", e)));
            } finally {
                connectInProgress.set(false);
            }
            result.complete(null);
        });
        return result;
    }

    protected void onInitializationError(Stage primaryStage, ClientInitializationException initializationException, Iterable<RuntimeException> possibleCauses) {
        LOG.error("Initialization error", initializationException);
        for (RuntimeException cause : possibleCauses) {
            LOG.error("Possible cause", cause);
        }
        Platform.exit();
    }

    private void handleInitializationError(final Stage primaryStage, final ClientInitializationException initializationException) {
        final Iterable<RuntimeException> possibleCauses = Collections.unmodifiableList(runtimeExceptionsAtInitialization);
        runtimeExceptionsAtInitialization.clear();
        onInitializationError(primaryStage, initializationException, possibleCauses);
    }

    /**
     * This method is called if the connection to the server throws an exception at runtime. This can
     * for example happen if the server is shut down while the client is still running or if the server responses with
     * an error code.
     *
     * @param primaryStage     the primary stage
     * @param runtimeException the exception
     */
    protected void onRuntimeError(final Stage primaryStage, final RuntimeException runtimeException) {
        Assert.requireNonNull(runtimeException, "runtimeException");
        LOG.error("Runtime error", runtimeException);
        Platform.exit();
    }
}
