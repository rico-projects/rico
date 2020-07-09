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
package dev.rico.internal.server.timing;

import dev.rico.internal.core.Assert;
import dev.rico.server.timing.ServerTimer;
import dev.rico.server.timing.ServerTiming;

import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static dev.rico.internal.core.http.HttpHeaderConstants.SERVER_TIMING_HEADER;
import static dev.rico.internal.core.http.HttpHeaderConstants.SERVER_TIMING_HEADER_DESC;
import static dev.rico.internal.core.http.HttpHeaderConstants.SERVER_TIMING_HEADER_DUR;

public class ServerTimingImpl implements ServerTiming {

    private final List<ServerTimerImpl> timers = new ArrayList<>();

    public void clear() {
        timers.clear();
    }

    public void dump(final HttpServletResponse response) {
        Assert.requireNonNull(response, "response");
        //Sample:   serverTiming: 'A;dur=2521.46147;desc="/users/me",B;dur=102.022688;desc="getUser"',

        final String headerName = SERVER_TIMING_HEADER;
        final String content = timers.stream().map(m -> convert(m)).reduce("", (a, b) -> a + "," + b);

        if (content.length() > 0) {
            response.addHeader(headerName, content.substring(1));
        }

        clear();
    }

    private String convert(final ServerTimerImpl timer) {
        Assert.requireNonNull(timer, "timer");
        final Duration duration = timer.getDuration();
        final String description = timer.getDescription();

        final String durPart = Optional.ofNullable(duration)
                .map(d -> ";" + SERVER_TIMING_HEADER_DUR + (Math.max(1.0, d.toNanos()) / 1000000.0))
                .orElse("");
        final String descPart = Optional.ofNullable(description)
                .map(d -> ";" + SERVER_TIMING_HEADER_DESC + "\"" + d + "\"")
                .orElse("");

        return timer.getName() + durPart + descPart;
    }

    @Override
    public ServerTimer start(final String name, final String description) {
        final ServerTimerImpl timer = new ServerTimerImpl(name, description);
        timers.add(timer);
        return timer;
    }
}
