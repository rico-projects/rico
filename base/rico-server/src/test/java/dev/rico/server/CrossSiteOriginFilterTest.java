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
package dev.rico.server;

import dev.rico.internal.server.remoting.config.ServerConfiguration;
import dev.rico.internal.server.remoting.servlet.CrossSiteOriginFilter;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.assertEquals;

public class CrossSiteOriginFilterTest {

    @Test
    public void testCommaSeparatedStringWithValidList(){
        final CrossSiteOriginFilter crossSiteOriginFilter = new CrossSiteOriginFilter(new ServerConfiguration());
        final String commaSeparatedList = crossSiteOriginFilter.getAsCommaSeparatedList(Arrays.asList("origin", "authorization", "accept"));
        assertEquals("origin,authorization,accept",commaSeparatedList);
    }
    @Test(expectedExceptions = NullPointerException.class)
    public void testCommaSeparatedList(){
        final CrossSiteOriginFilter crossSiteOriginFilter = new CrossSiteOriginFilter(new ServerConfiguration());
        crossSiteOriginFilter.getAsCommaSeparatedList(null);
    }
}
