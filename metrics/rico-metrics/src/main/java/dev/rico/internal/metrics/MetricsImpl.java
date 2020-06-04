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
package dev.rico.internal.metrics;

import dev.rico.core.context.Context;
import dev.rico.internal.core.Assert;
import dev.rico.internal.core.context.ContextImpl;
import dev.rico.internal.core.context.ContextManagerImpl;
import dev.rico.metrics.Metrics;
import dev.rico.metrics.types.Counter;
import dev.rico.metrics.types.Gauge;
import dev.rico.metrics.types.Timer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


public class MetricsImpl implements Metrics {

    private static final Logger LOG = LoggerFactory.getLogger(MetricsImpl.class);

    private static final MetricsImpl INSTANCE = new MetricsImpl();

    private final AtomicReference<MeterRegistry> registry;

    private MetricsImpl() {
        registry = new AtomicReference<>(new NoopMeterRegistry());
    }

    public void init(final MeterRegistry registry) {
        Assert.requireNonNull(registry, "registry");
        this.registry.set(registry);
    }

    @Override
    public Counter getOrCreateCounter(final String name, final Context... tags) {
        final List<Tag> tagList = new ArrayList<>();
        tagList.addAll(TagUtil.convertTags(tags));
        tagList.addAll(TagUtil.convertTags(ContextManagerImpl.getInstance().getGlobalContexts()));
        final io.micrometer.core.instrument.Counter counter = registry.get()
                .counter(name, tagList);
        return new Counter() {
            @Override
            public void increment(final long amount) {
                if (amount < 0) {
                    LOG.warn("Counter metric can not be incremented with negative value!");
                }
                counter.increment(amount);
            }

            @Override
            public String getName() {
                return counter.getId().getName();
            }

            @Override
            public List<Context> getContext() {
                return counter.getId().getTags()
                        .stream()
                        .map(t -> new ContextImpl(t.getKey(), t.getValue()))
                        .collect(Collectors.toList());
            }

            @Override
            public void close() throws Exception {
                counter.close();
            }
        };
    }

    @Override
    public Timer getOrCreateTimer(final String name, final Context... tags) {
        final List<io.micrometer.core.instrument.Tag> tagList = new ArrayList<>();
        tagList.addAll(TagUtil.convertTags(tags));
        tagList.addAll(TagUtil.convertTags(ContextManagerImpl.getInstance().getGlobalContexts()));
        io.micrometer.core.instrument.Timer timer = registry.get().timer(name, tagList);
        return new Timer() {
            @Override
            public void record(final long amount, final TimeUnit unit) {
                timer.record(amount, unit);
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public List<Context> getContext() {
                return Collections.unmodifiableList(Arrays.asList(tags));
            }

            @Override
            public void close() throws Exception {
                timer.close();
            }
        };
    }

    @Override
    public Gauge getOrCreateGauge(final String name, final Context... tags) {
        final List<io.micrometer.core.instrument.Tag> tagList = new ArrayList<>();
        tagList.addAll(TagUtil.convertTags(tags));
        tagList.addAll(TagUtil.convertTags(ContextManagerImpl.getInstance().getGlobalContexts()));
        final AtomicReference<Double> internalValue = new AtomicReference<>(0d);

        io.micrometer.core.instrument.Gauge gauge = io.micrometer.core.instrument.Gauge
                .builder("name", internalValue, r -> Optional.ofNullable(r.get()).orElse(0d))
                .tags(tagList)
                .register(registry.get());

        return new Gauge() {
            @Override
            public void setValue(final double value) {
                internalValue.set(value);
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public List<Context> getContext() {
                return Collections.unmodifiableList(Arrays.asList(tags));
            }

            @Override
            public void close() throws Exception {
            }
        };
    }

    public static MetricsImpl getInstance() {
        return INSTANCE;
    }
}
