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


import dev.rico.internal.server.bootstrap.ConfigurationProviderAdapter;
import java.util.HashMap;
import java.util.Map;

import static dev.rico.internal.server.bootstrap.BasicConfigurationProvider.USE_CROSS_SITE_ORIGIN_FILTER;

public class TestConfigurationProvider extends ConfigurationProviderAdapter {

    public static final String PROPERTY_1_NAME = "testProperty1";

    public static final String PROPERTY_2_NAME = "testProperty2";

    public static final String PROPERTY_3_NAME = "testProperty3";

    public static final String PROPERTY_1_VALUE = "YEAH!";

    public static final String PROPERTY_2_VALUE = "JUHU";

    public static final String PROPERTY_3_VALUE = null;

    @Override
    public Map<String, String> getStringProperties() {
        Map<String, String> ret = new HashMap<>();
        ret.put(PROPERTY_1_NAME, PROPERTY_1_VALUE);
        ret.put(PROPERTY_2_NAME, PROPERTY_2_VALUE);
        ret.put(PROPERTY_3_NAME, PROPERTY_3_VALUE);

        //This should not be overwritten from rico.properties
        ret.put(USE_CROSS_SITE_ORIGIN_FILTER, "true");
        return ret;
    }
}
