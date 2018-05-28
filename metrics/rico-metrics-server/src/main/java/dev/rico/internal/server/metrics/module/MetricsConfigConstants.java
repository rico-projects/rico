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
package dev.rico.internal.server.metrics.module;

public interface MetricsConfigConstants {

    String MODULE_NAME = "MetricsModule";

    String METRICS_SERVLET_NAME = "metrics";

    String METRICS_SERVLET_FILTER_NAME = "metricsFilter";

    String METRICS_ACTIVE_PROPERTY = "metrics.active";

    boolean METRICS_ACTIVE_DEFAULT = true;

    String METRICS_ENDPOINT_PROPERTY = "metrics.endpoint";

    String METRICS_ENDPOINT_DEFAULT = "/metrics";

    String METRICS_NOOP_PROPERTY = "metrics.noop";

    boolean METRICS_NOOP_DEFAULT = false;

}
