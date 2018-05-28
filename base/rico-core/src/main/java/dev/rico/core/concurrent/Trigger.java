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
package dev.rico.core.concurrent;

import java.time.LocalDateTime;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.SECONDS;

public interface Trigger {

    Trigger NEVER = t -> Optional.ofNullable(null);

    Trigger NOW = t -> Optional.of(LocalDateTime.now());

    Trigger IN_100_MS = t -> Optional.of(LocalDateTime.now().plus(100, MILLIS));

    Trigger IN_500_MS = t -> Optional.of(LocalDateTime.now().plus(500, MILLIS));

    Trigger IN_1_S = t -> Optional.of(LocalDateTime.now().plus(1, SECONDS));

    Optional<LocalDateTime> nextExecutionTime(TaskResult taskResult);
}
