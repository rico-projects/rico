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

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import io.micrometer.core.instrument.noop.NoopCounter;
import io.micrometer.core.instrument.noop.NoopDistributionSummary;
import io.micrometer.core.instrument.noop.NoopFunctionCounter;
import io.micrometer.core.instrument.noop.NoopFunctionTimer;
import io.micrometer.core.instrument.noop.NoopGauge;
import io.micrometer.core.instrument.noop.NoopLongTaskTimer;
import io.micrometer.core.instrument.noop.NoopTimer;

import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

public class NoopMeterRegistry extends MeterRegistry {

    public NoopMeterRegistry() {
        super(Clock.SYSTEM);
    }

    @Override
    protected <T> Gauge newGauge(final Meter.Id id, final T obj, final ToDoubleFunction<T> valueFunction) {
        return new NoopGauge(id);
    }

    @Override
    protected Counter newCounter(final Meter.Id id) {
        return new NoopCounter(id);
    }

    @Override
    protected LongTaskTimer newLongTaskTimer(final Meter.Id id) {
        return new NoopLongTaskTimer(id);
    }

    @Override
    protected Timer newTimer(final Meter.Id id, final DistributionStatisticConfig distributionStatisticConfig, final PauseDetector pauseDetector) {
        return new NoopTimer(id);
    }

    @Override
    protected DistributionSummary newDistributionSummary(final Meter.Id id, final DistributionStatisticConfig distributionStatisticConfig, final double scale) {
        return new NoopDistributionSummary(id);
    }

    @Override
    protected Meter newMeter(final Meter.Id id, final Meter.Type type, final Iterable<Measurement> measurements) {
        return new NoopCounter(id);
    }

    @Override
    protected <T> FunctionTimer newFunctionTimer(final Meter.Id id, final T obj, final ToLongFunction<T> countFunction, final ToDoubleFunction<T> totalTimeFunction, final TimeUnit totalTimeFunctionUnits) {
        return new NoopFunctionTimer(id);
    }

    @Override
    protected <T> FunctionCounter newFunctionCounter(final Meter.Id id, final T obj, final ToDoubleFunction<T> countFunction) {
        return new NoopFunctionCounter(id);
    }

    @Override
    protected TimeUnit getBaseTimeUnit() {
        return TimeUnit.DAYS;
    }

    @Override
    protected DistributionStatisticConfig defaultHistogramConfig() {
        return new DistributionStatisticConfig();
    }
}
