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
package dev.rico.internal.client.projection.projection;

import dev.rico.internal.client.projection.graph.GraphComponent;
import dev.rico.internal.projection.graph.GraphDataBean;
import dev.rico.client.remoting.ClientContext;
import dev.rico.client.remoting.ControllerProxy;
import javafx.scene.Parent;

public class GraphFactory implements ProjectionFactory<GraphDataBean> {

    @Override
    public Parent createProjection(Projector projector, ClientContext clientContext, ControllerProxy controllerProxy, GraphDataBean projectable) {
        return new GraphComponent(projectable);
    }
}

