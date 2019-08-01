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
package dev.rico.core.context;

import dev.rico.internal.core.context.ContextImpl;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashSet;

public class ContextImplTest {

    @Test(expectedExceptions = RuntimeException.class)
    public void testNullKey() {
        // when:
        final Context context1 = new ContextImpl(null, "SOME_VALUE");
    }

    @Test
    public void testNullValue() {
        // when:
        final Context context1 = new ContextImpl("KEY", null);

        // then:
        Assert.assertNull(context1.getValue());
    }

    @Test
    public void testEquals() {
        // when:
        final Context context1 = new ContextImpl("KEY", "SOME_VALUE");
        final Context context2 = new ContextImpl("KEY", "SOME_OTHER_VALUE");
        final Context context3 = new ContextImpl("OTHER_KEY", "SOME_OTHER_VALUE");

        // then:
        Assert.assertTrue(context1.equals(context2));
        Assert.assertTrue(context2.equals(context1));
        Assert.assertFalse(context1.equals(context3));
        Assert.assertFalse(context2.equals(context3));
        Assert.assertFalse(context3.equals(context1));
        Assert.assertFalse(context3.equals(context2));
    }

    @Test
    public void testHash() {
        // given:
        final Context context1 = new ContextImpl("KEY", "SOME_VALUE");
        final Context context2 = new ContextImpl("KEY", "SOME_OTHER_VALUE");
        final Context context3 = new ContextImpl("OTHER_KEY", "SOME_OTHER_VALUE");
        final HashSet<Context> set = new HashSet<>();

        // when:
        set.add(context1);
        set.add(context2);
        set.add(context3);

        // then:
        Assert.assertEquals(set.size(), 2);
    }

}
