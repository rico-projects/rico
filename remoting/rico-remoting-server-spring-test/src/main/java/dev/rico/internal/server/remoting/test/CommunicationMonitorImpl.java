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
package dev.rico.internal.server.remoting.test;

import dev.rico.internal.core.Assert;
import dev.rico.server.remoting.test.CommunicationMonitor;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class CommunicationMonitorImpl implements CommunicationMonitor {

    private final static long SLEEP_TIME = 100;

    private final TestClientContext clientContext;

    private final AtomicBoolean ping = new AtomicBoolean();

    public CommunicationMonitorImpl(final TestClientContext clientContext) {
        this.clientContext = Assert.requireNonNull(clientContext, "clientContext");
    }

    private void sendPing() {
        clientContext.sendPing();
    }

    private void sendPing(final long time, final TimeUnit unit) {
        clientContext.sendPing(time, unit);
    }

    @Override
    public void await(final long time, final TimeUnit unit) throws InterruptedException, TimeoutException {
        Assert.requireNonNull(unit, "unit");
        final long endTime = System.currentTimeMillis() + unit.toMillis(time);
        ping.set(true);
        while (ping.get() && System.currentTimeMillis() < endTime) {
            final long leftTime = endTime - System.currentTimeMillis();
            sendPing(Math.max(0, leftTime - SLEEP_TIME), TimeUnit.MILLISECONDS);
            Thread.sleep(SLEEP_TIME);
        }
        if(ping.get()) {
            throw new TimeoutException("Timeout!");
        }
    }

    @Override
    public void signal() {
        ping.set(false);
    }
}
