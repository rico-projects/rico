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
package dev.rico.integrationtests;

import dev.rico.client.remoting.ClientContext;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ConnectionTest extends AbstractIntegrationTest {

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Tests if the client API can createList a connection to the server")
    public void testConnection(String containerType, String endpoint) {
        try {
            ClientContext context = connect(endpoint);
            Assert.assertNotNull(context);
            disconnect(context, endpoint);
        } catch (Exception e) {
            Assert.fail("Can not createList connection for " + containerType, e);
        }
    }
}
