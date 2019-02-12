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
package dev.rico.internal.remoting.eventbus.distributed;

import dev.rico.internal.server.remoting.config.RemotingConfiguration;
import dev.rico.remoting.eventbus.distributed.HazelcastProvider;
import dev.rico.server.remoting.event.spi.EventBusProvider;
import dev.rico.server.remoting.event.RemotingEventBus;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.ServiceLoader;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class DistributedEventBusProvider implements EventBusProvider {

    private static final Logger LOG = LoggerFactory.getLogger(DistributedEventBusProvider.class);

    public static final String DISTRIBUTED_EVENTBUS_NAME = "distributed";

    @Override
    public String getType() {
        return DISTRIBUTED_EVENTBUS_NAME;
    }

    public RemotingEventBus create(final RemotingConfiguration configuration) {
        LOG.debug("creating distributed event bus");

        HazelcastProvider hazelcastProvider = null;
        final Iterator<HazelcastProvider> iterator = ServiceLoader.load(HazelcastProvider.class).iterator();

        //TODO: configurable
        if(iterator.hasNext()) {
            hazelcastProvider = iterator.next();
        }
        if(iterator.hasNext()) {
            throw new IllegalStateException("More than one service implementation for found for " + HazelcastProvider.class);
        }

        if(hazelcastProvider == null) {
            hazelcastProvider = new DefaultHazelcastProvider();
        }

        LOG.debug("Using Hazelcast provider {}", hazelcastProvider.getClass());

        return new DistributedEventBus(hazelcastProvider.getHazelcastInstance(new HazelcastConfig(configuration.getConfiguration())));
    }

}
