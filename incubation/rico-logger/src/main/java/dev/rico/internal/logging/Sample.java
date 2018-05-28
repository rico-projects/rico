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
package dev.rico.internal.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;
import org.slf4j.event.Level;

import java.net.URI;

public class Sample {

    private static final Logger LOG = LoggerFactory.getLogger(Sample.class);

    public static void main(String[] args) throws Exception {
        RicoLogger.addMarker(LOG, "Marker1");
        LOG.info("huhu");


        LoggerConfiguration configuration = new LoggerConfiguration();
        configuration.setRemoteUrl(new URI("http://localhost:12201/gelf"));
        configuration.setMaxMessagesPerRequest(1);
        configuration.setRemotingQueueCheckSleepTime(1_000);
        configuration.setGlobalLevel(Level.TRACE);
        RicoLoggerFactory.applyConfiguration(configuration);

        LOG.info(MarkerFactory.getMarker("Marker2"), "huhu2");

        while (true) {
            Thread.sleep(100);
            LOG.info(MarkerFactory.getMarker("MyMarker"), "Zeit: " + System.currentTimeMillis());
            LOG.error("HILFE", new RuntimeException("Ich bin der Fehler"));
        }
    }

}
