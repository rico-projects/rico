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
package dev.rico.internal.server.remoting.gc;

import dev.rico.internal.remoting.server.gc.GarbageCollector;
import dev.rico.remoting.RemotingBean;
import dev.rico.remoting.Property;

@RemotingBean
public class BeanWithProperties {

    private final Property<String> stringProperty;

    private final Property<Boolean> booleanProperty;

    private final Property<Double> doubleProperty;

    private final Property<BeanWithProperties> beanProperty;

    private final Property<BeanWithLists> listBeanProperty;

    public BeanWithProperties(GarbageCollector garbageCollector) {
        this.stringProperty = new PropertyWithGcSupport<>(garbageCollector);
        this.booleanProperty = new PropertyWithGcSupport<>(garbageCollector);
        this.doubleProperty = new PropertyWithGcSupport<>(garbageCollector);
        this.beanProperty = new PropertyWithGcSupport<>(garbageCollector);
        this.listBeanProperty = new PropertyWithGcSupport<>(garbageCollector);
    }

    public Property<String> stringProperty() {
        return stringProperty;
    }

    public Property<Boolean> booleanProperty() {
        return booleanProperty;
    }

    public Property<Double> doubleProperty() {
        return doubleProperty;
    }

    public Property<BeanWithProperties> beanProperty() {
        return beanProperty;
    }

    public Property<BeanWithLists> listBeanProperty() {
        return listBeanProperty;
    }
}
