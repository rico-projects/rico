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

import dev.rico.core.lang.StringPair;
import dev.rico.internal.core.Assert;
import dev.rico.internal.core.context.RicoApplicationContextImpl;
import dev.rico.metrics.Metrics;
import dev.rico.metrics.spi.MetricsBinder;
import dev.rico.metrics.types.Counter;
import dev.rico.metrics.types.Gauge;
import dev.rico.metrics.types.Timer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;


public class MetricsImpl implements Metrics {

    private static final Logger LOG = LoggerFactory.getLogger(MetricsImpl.class);

    private static final MetricsImpl INSTANCE = new MetricsImpl();

    private MeterRegistry registry;

    private final Lock registryLock = new ReentrantLock();

    private final List<MetricsBinder> binders = new ArrayList<>();

    private MetricsImpl() {
        init(new NoopMeterRegistry());
    }

    public void init(final MeterRegistry registry) {
        Assert.requireNonNull(registry, "registry");
        registryLock.lock();
        try {
            binders.forEach(b -> b.unregister(this.registry));
            this.registry = registry;
            binders.forEach(b -> b.init(this.registry, getTags()));
        } finally {
            registryLock.unlock();
        }
    }

    private MeterRegistry getRegistry() {
        registryLock.lock();
        try {
            return this.registry;
        } finally {
            registryLock.unlock();
        }
    }

    @Override
    public Counter getOrCreateCounter(final String name, final StringPair... tags) {
        final List<Tag> tagList = getTags(tags);
        final io.micrometer.core.instrument.Counter counter = getRegistry()
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
            public List<StringPair> getContext() {
                return counter.getId().getTags()
                        .stream()
                        .map(t -> StringPair.of(t.getKey(), t.getValue()))
                        .collect(Collectors.toList());
            }

            @Override
            public void close() {
                counter.close();
            }
        };
    }

    @Override
    public Timer getOrCreateTimer(final String name, final StringPair... tags) {
        final List<Tag> tagList = getTags(tags);
        io.micrometer.core.instrument.Timer timer = getRegistry().timer(name, tagList);
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
            public List<StringPair> getContext() {
                return timer.getId().getTags()
                        .stream()
                        .map(t -> StringPair.of(t.getKey(), t.getValue()))
                        .collect(Collectors.toList());
            }

            @Override
            public void close() {
                timer.close();
            }
        };
    }

    public List<Tag> getTags(final StringPair... tags) {
        final List<Tag> tagList = new ArrayList<>();
        tagList.addAll(TagUtil.convertTags(tags));
        tagList.addAll(TagUtil.convertTags(RicoApplicationContextImpl.getInstance().getGlobalAttributes()));
        return tagList;
    }

    @Override
    public Gauge getOrCreateGauge(final String name, final StringPair... tags) {
        final List<Tag> tagList = getTags(tags);
        final AtomicReference<Double> internalValue = new AtomicReference<>(0d);

        io.micrometer.core.instrument.Gauge gauge = io.micrometer.core.instrument.Gauge
                .builder("name", internalValue, r -> Optional.ofNullable(r.get()).orElse(0d))
                .tags(tagList)
                .register(getRegistry());

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
            public List<StringPair> getContext() {
                return gauge.getId().getTags()
                        .stream()
                        .map(t -> StringPair.of(t.getKey(), t.getValue()))
                        .collect(Collectors.toList());
            }

            @Override
            public void close() {
                gauge.close();
            }
        };
    }

    public static MetricsImpl getInstance() {
        return INSTANCE;
    }

    public void initializeBinder(final MetricsBinder binder) {
        Assert.requireNonNull(binder, "binder");
        registryLock.lock();
        try {
            binder.init(getRegistry(), getTags());
            binders.add(binder);
        } finally {
            registryLock.unlock();
        }
    }
}
