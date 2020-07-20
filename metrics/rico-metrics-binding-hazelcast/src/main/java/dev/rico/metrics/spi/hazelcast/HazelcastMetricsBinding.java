package dev.rico.metrics.spi.hazelcast;

import com.hazelcast.config.MapConfig;
import com.hazelcast.config.TopicConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import dev.rico.internal.core.Assert;
import dev.rico.internal.core.context.RicoApplicationContextImpl;
import dev.rico.metrics.spi.MetricsBinder;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HazelcastMetricsBinding implements MetricsBinder {

    private final HazelcastInstance hazelcastInstance;

    public HazelcastMetricsBinding(final HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = Assert.requireNonNull(hazelcastInstance, "hazelcastInstance");
    }

    @Override
    public void init(final MeterRegistry registry, final Iterable<Tag> tags) {

        final String memberId = hazelcastInstance.getCluster().getLocalMember().getUuid();
        RicoApplicationContextImpl.getInstance().setGlobalAttribute("hazelcast.cluster.member.id", memberId);

        Gauge.builder("hazelcast.cluster.count", hazelcastInstance, i -> i.getCluster().getMembers().size())
                .tags(tags)
                .description("The number of machines in the hazelcast cluster")
                .register(registry);

        Gauge.builder("hazelcast.client.count", hazelcastInstance, i -> i.getClientService().getConnectedClients().size())
                .tags(tags)
                .description("The number of hazelcast clients")
                .register(registry);

        final Map<String, TopicConfig> topicConfigs = hazelcastInstance.getConfig().getTopicConfigs();
        Gauge.builder("hazelcast.topics.count", topicConfigs, c -> c.size())
                .tags(tags)
                .description("The number of hazelcast topics")
                .register(registry);

        topicConfigs.forEach((name, config) -> {
            final List<Tag> mergedTags = new ArrayList<>();
            tags.forEach(t -> mergedTags.add(t));
            mergedTags.add(Tag.of("hazelcast.topic.name", name));
            Gauge.builder("hazelcast.topics.listeners", config, c -> c.getMessageListenerConfigs().size())
                    .tags(mergedTags)
                    .description("The number of hazelcast topic listeners")
                    .register(registry);
        });

        final Map<String, MapConfig> mapConfigs = hazelcastInstance.getConfig().getMapConfigs();
        Gauge.builder("hazelcast.maps.count", mapConfigs, c -> c.size())
                .tags(tags)
                .description("The number of hazelcast maps")
                .register(registry);
        mapConfigs.forEach((name, config) -> {
            final List<Tag> mergedTags = new ArrayList<>();
            tags.forEach(t -> mergedTags.add(t));
            mergedTags.add(Tag.of("hazelcast.map.name", name));

            Gauge.builder("hazelcast.maps.listeners", config, c -> c.getEntryListenerConfigs().size())
                    .tags(mergedTags)
                    .description("The number of hazelcast map listeners")
                    .register(registry);

            final IMap<Object, Object> map = hazelcastInstance.getMap(name);
            Gauge.builder("hazelcast.maps.size", map, m -> m.size())
                    .tags(mergedTags)
                    .description("The number of entries in a hazelcast map")
                    .register(registry);
        });
    }
}
