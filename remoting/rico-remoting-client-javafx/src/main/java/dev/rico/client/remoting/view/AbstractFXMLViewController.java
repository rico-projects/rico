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
import dev.rico.client.remoting.ClientContext;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import org.apiguardian.api.API;

import java.net.URL;

import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * Defines an abstract class that can be used to create a FXML based view that is bound to a remoting controller
 * on the server and shares a model with the controller.
 *
 * By creating new instances of classes that extend this class a JavaFX view will be automatically created by using the
 * given FXML file. In addition the {@link javafx.fxml.FXML} annotation can be used in that classes.
 *
 * For information about the behavior of this class see {@link AbstractViewController}.
 *
 * @param <M> type of the model.
 */
@API(since = "0.x", status = MAINTAINED)
public abstract class AbstractFXMLViewController<M> extends AbstractViewController<M> {

    private final Node rootNode;

    /**
     * Constructor
     * @param clientContext the client context
     * @param controllerName the controller name of the controller definition on the server that should
     *                       be used with this view.
     * @param fxmlLocation the location (url) of the FXML file that defines the layout of the view.
     */
    public AbstractFXMLViewController(ClientContext clientContext, String controllerName, URL fxmlLocation) {
        super(clientContext, controllerName);
        Assert.requireNonNull(fxmlLocation, "fxmlLocation");
        try {
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            loader.setController(this);
            rootNode = loader.load();
        } catch (Exception e) {
            throw new RuntimeException("Can not create view based on FXML location " + fxmlLocation, e);
        }
    }

    @Override
    public Node getRootNode() {
        return rootNode;
    }
}
