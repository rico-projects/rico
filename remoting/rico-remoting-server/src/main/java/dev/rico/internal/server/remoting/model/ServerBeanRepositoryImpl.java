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
package dev.rico.internal.server.remoting.model;

import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.BeanRepositoryImpl;
import dev.rico.internal.remoting.EventDispatcher;
import dev.rico.internal.remoting.legacy.core.ModelStore;
import dev.rico.internal.server.remoting.gc.GarbageCollector;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ServerBeanRepositoryImpl extends BeanRepositoryImpl implements ServerBeanRepository{

    final GarbageCollector garbageCollector;

    public ServerBeanRepositoryImpl(final ModelStore modelStore, final EventDispatcher dispatcher, final GarbageCollector garbageCollector) {
        super(modelStore, dispatcher);
        this.garbageCollector = Assert.requireNonNull(garbageCollector, "garbageCollector");
    }

    @Override
    public <T> void delete(T bean) {
        super.delete(bean);
        garbageCollector.onBeanRemoved(bean);
    }

    @Override
    public <T> void onGarbageCollectionRejection(T bean) {
        super.delete(bean);
    }
}
