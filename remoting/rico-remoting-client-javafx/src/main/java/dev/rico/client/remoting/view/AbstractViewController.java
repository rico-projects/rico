/*
 * Copyright 2018 Karakun AG.
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
package dev.rico.client.remoting.view;

import dev.rico.internal.core.Assert;
import dev.rico.remoting.RemotingBean;
import dev.rico.client.remoting.ClientContext;
import dev.rico.client.remoting.ControllerProxy;
import dev.rico.client.remoting.Param;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.Node;
import javafx.scene.Parent;
import org.apiguardian.api.API;

import java.util.concurrent.CompletableFuture;

import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * A abstract JavaFX view controller that can be used as a basic for a JavaFX based view. Each instance will automatically
 * trigger Rico to createList a controller instance on the server that is bound to the view instance and shares
 * a model (see {@link RemotingBean}) with the view.
 *
 * @param <M> type of the model
 */
@API(since = "0.x", status = MAINTAINED)
public abstract class AbstractViewController<M> {

    private ControllerProxy<M> controllerProxy;

    private final ReadOnlyBooleanWrapper actionInProcess = new ReadOnlyBooleanWrapper(false);

    private final ReadOnlyObjectWrapper<M> model = new ReadOnlyObjectWrapper<>();

    private final ClientContext clientContext;

    /**
     * Constructor that internally starts the remoting workflow and triggers the controller creation on the server.
     *
     * @param clientContext  the client context
     * @param controllerName name of the controller (see annotation RemotingController in the Java server lib).
     */
    public AbstractViewController(ClientContext clientContext, String controllerName) {
        Assert.requireNonBlank(controllerName, "controllerName");
        this.clientContext = Assert.requireNonNull(clientContext, "clientContext");
        clientContext.<M>createController(controllerName).whenComplete((c, e) -> {
            if (e != null) {
                onInitializationException(e);
            } else {
                try {
                    controllerProxy = c;
                    model.set(c.getModel());
                    init();
                } catch (Exception exception) {
                    onInitializationException(exception);
                }
            }
        });
    }

    /**
     * This method will automatically be called after the controller instance has been created on the server and the initial
     * model is snychronized between client and server. When this method is called the model can be accessed (by calling {@link #getModel()})
     * and actions can be triggered on the server controller instance (by calling {@link #invoke(String, Param...)}).
     */
    protected abstract void init();

    /**
     * By calling this method the MVC group will be destroyed. This means that the controller instance on the server will
     * be removed and the model that is managed and synchronized between client and server will be detached. After this method
     * is called the view should not be used anymore. It's important to call this method to removePresentationModel all the unneeded references on
     * the server.
     *
     * @return a future can be used to react on the destroy
     */
    public CompletableFuture<Void> destroy() {
        CompletableFuture<Void> ret;
        if (controllerProxy != null) {
            ret = controllerProxy.destroy();
            controllerProxy = null;
        } else {
            ret = new CompletableFuture<>();
            ret.complete(null);
        }
        return ret;
    }

    /**
     * This invokes a action on the server side controller. For more information how an action can be defined in the
     * controller have a look at the RemotingAction annotation in the server module.
     * This method don't block and can be called from the Platform thread. To check if an server
     *
     * @param actionName name of the action
     * @param params     any parameters that should be passed to the action
     * @return a future can be used to check if the action invocation is still running
     */
    protected CompletableFuture<Void> invoke(String actionName, Param... params) {
        Assert.requireNonBlank(actionName, "actionName");
        actionInProcess.set(true);
        return controllerProxy.invoke(actionName, params).whenComplete((v, e) -> {
            try {
                if (e != null) {
                    onInvocationException(e);
                }
            } finally {
                actionInProcess.set(false);
            }
        });
    }

    /**
     * Returns true if an action invocation is running (see {@link #invoke(String, Param...)})
     *
     * @return true if an action invocation is running
     */
    public boolean isActionInProcess() {
        return actionInProcess.get();
    }

    /**
     * Returns a read only property that can be used to check if an action invocation is running (see {@link #invoke(String, Param...)})
     *
     * @return read only property
     */
    public ReadOnlyBooleanProperty actionInProcessProperty() {
        return actionInProcess.getReadOnlyProperty();
    }

    /**
     * Returns the model that is synchronized between client and server. For more information see {@link RemotingBean}
     *
     * @return the model
     */
    public M getModel() {
        return model.get();
    }

    /**
     * Returns a read only property that contains the model that is synchronized between client and server.
     * For more information see {@link RemotingBean}
     *
     * @return read only property
     */
    public ReadOnlyObjectProperty<M> modelProperty() {
        return model.getReadOnlyProperty();
    }

    /**
     * This method will be called if an exception is thrown in the initialization of this view.
     *
     * @param t the exception
     */
    protected void onInitializationException(Throwable t) {

    }

    /**
     * This method will be called if an exception is thrown in an action invocation.
     *
     * @param e the exception
     */
    protected void onInvocationException(Throwable e) {

    }

    /**
     * Returns the client context
     *
     * @return the client context
     */
    public ClientContext getClientContext() {
        return clientContext;
    }

    /**
     * Returns the root node of the view.
     *
     * @return the root node.
     */
    public abstract Node getRootNode();

    /**
     * Usefull helper method that returns the root node (see {@link #getRootNode()}) as a {@link Parent} if the root node
     * extends {@link Parent} or throws an runtime exception. This can be used to simply add a {@link AbstractFXMLViewController}
     * based view to a scene that needs a {@link Parent} as a root node.
     *
     * @return the root node
     */
    public Parent getParent() {
        Node rootNode = getRootNode();
        if (rootNode == null) {
            throw new NullPointerException("The root node is null");
        }
        if (!(rootNode instanceof Parent)) {
            throw new IllegalStateException("The root node of this view is not a Parent");
        }
        return (Parent) rootNode;
    }

    protected ControllerProxy<M> getControllerProxy() {
        return controllerProxy;
    }
}
