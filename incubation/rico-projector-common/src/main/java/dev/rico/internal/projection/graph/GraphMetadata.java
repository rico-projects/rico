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
package dev.rico.internal.projection.graph;

import dev.rico.internal.projection.metadata.MetadataUtilities;
import dev.rico.remoting.BeanManager;

import java.util.Optional;

public interface GraphMetadata {

    String JAVAFX_LAYOUT_GRAPH_TYPE = "type";

    String JAVAFX_LAYOUT_GRAPH_TYPE_VALUE_PIE = GraphType.PIE.name();

    String JAVAFX_LAYOUT_GRAPH_TYPE_VALUE_BAR = GraphType.BARCHART.name();

    static void setGraphType(GraphDataBean content, BeanManager beanManager, GraphType type) {
        MetadataUtilities.getOrCreateStringBasedMetadata(JAVAFX_LAYOUT_GRAPH_TYPE, content, beanManager).setValue(type.name());
    }

    static GraphType getGraphType(GraphDataBean content) {
        return MetadataUtilities.getMetadata(JAVAFX_LAYOUT_GRAPH_TYPE, content).map(key -> GraphType.valueOf(Optional.ofNullable(key.getValue()).orElse("").toString())).orElse(GraphType.PIE);
    }
}
