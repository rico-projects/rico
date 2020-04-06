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
package dev.rico.server.remoting.binding;

import dev.rico.server.remoting.binding.Qualifier;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestQualifier {

    @Test
    public void testUniqueId() {
        Qualifier<String> qualifierA = Qualifier.create();
        for (int i = 0; i < 1000; i++) {
            Qualifier<String> qualifierB = Qualifier.create();
            Assert.assertNotEquals(qualifierA, qualifierB);
        }
    }

    @Test
    public void testEquals() {
        Qualifier<String> qualifierA = Qualifier.create("QualifierA");
        Qualifier<String> qualifierB = Qualifier.create("QualifierA");
        Assert.assertEquals(qualifierA, qualifierB);
    }

    @Test
    public void testName() {
        Qualifier<String> qualifierA = Qualifier.create("QualifierA");
        Qualifier<String> qualifierB = Qualifier.create("QualifierB");
        Assert.assertEquals(qualifierA.getIdentifier(), "QualifierA");
        Assert.assertEquals(qualifierB.getIdentifier(), "QualifierB");
    }
}
