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

import dev.rico.internal.server.remoting.event.MessageEventImpl;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.core.HazelcastInstance;
import dev.rico.remoting.eventbus.distributed.HazelcastProvider;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class DefaultHazelcastProvider implements HazelcastProvider {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultHazelcastProvider.class);

    private static final String LOGGER_PROPERTY_NAME = "hazelcast.logging.type";

    private static final String LOGGER_PROPERTY_SLF4J_TYPE = "slf4j";


    private HazelcastInstance hazelcastInstance;

    public synchronized HazelcastInstance getHazelcastInstance(final HazelcastConfig configuration) {
        if (hazelcastInstance == null) {
            final String serverName = configuration.getServerName();
            final String serverPort = configuration.getServerPort();
            final String groupName = configuration.getGroupName();

            LOG.debug("Hazelcast server name: {}", serverName);
            LOG.debug("Hazelcast server port: {}", serverPort);
            LOG.debug("Hazelcast group name: {}", groupName);

            final ClientConfig clientConfig = new ClientConfig();
            clientConfig.getNetworkConfig().setConnectionAttemptLimit(configuration.getConnectionAttemptLimit());
            clientConfig.getNetworkConfig().setConnectionAttemptPeriod(configuration.getConnectionAttemptPeriod());
            clientConfig.getNetworkConfig().setConnectionTimeout(configuration.getConnectionTimeout());
            clientConfig.getNetworkConfig().addAddress(serverName + ":" + serverPort);
            clientConfig.getGroupConfig().setName(groupName);
            clientConfig.setProperty(LOGGER_PROPERTY_NAME, LOGGER_PROPERTY_SLF4J_TYPE);

            final SerializerConfig eventSerializerConfig = new SerializerConfig().
                    setImplementation(new EventStreamSerializer()).setTypeClass(MessageEventImpl.class);

            clientConfig.getSerializationConfig().getSerializerConfigs().add(eventSerializerConfig);


            hazelcastInstance = HazelcastClient.newHazelcastClient(clientConfig);
        }
        return hazelcastInstance;
    }
}
