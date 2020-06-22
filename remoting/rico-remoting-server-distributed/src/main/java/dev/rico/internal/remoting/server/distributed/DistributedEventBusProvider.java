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
package dev.rico.internal.remoting.server.distributed;

import dev.rico.internal.core.lang.StreamUtils;
import dev.rico.internal.remoting.server.config.RemotingConfiguration;
import dev.rico.remoting.server.distributed.HazelcastProvider;
import dev.rico.remoting.server.event.RemotingEventBus;
import dev.rico.remoting.server.event.spi.EventBusProvider;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

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

        final List<HazelcastProvider> providers = StreamUtils.loadServiceAsStream(HazelcastProvider.class)
                .collect(Collectors.toList());

        if (providers.isEmpty()) {
            providers.add(new DefaultHazelcastProvider());
        }
        if (providers.size() > 1) {
            throw new IllegalStateException("More than one service implementation for found for " + HazelcastProvider.class);
        }

        final HazelcastProvider provider = providers.get(0);

        LOG.debug("Using Hazelcast provider {}", provider.getClass());

        return new DistributedEventBus(provider.getHazelcastInstance(new DefaultHazelcastConfig(configuration.getConfiguration())));
    }
}
