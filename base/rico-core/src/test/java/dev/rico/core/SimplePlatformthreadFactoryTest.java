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
package dev.rico.core;

import static dev.rico.internal.core.RicoConstants.THREAD_GROUP_NAME;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import dev.rico.internal.core.SimpleThreadFactory;

public class SimplePlatformthreadFactoryTest {

	@Test
	public void testPlatformThread() {

		SimpleThreadFactory sdptf = new SimpleThreadFactory();
		Runnable r = new SimpleTask("High Priority Task");
		assertEquals(sdptf.newThread(r).getThreadGroup().getName(), THREAD_GROUP_NAME);
	}
}

class SimpleTask implements Runnable {
	String s = null;

	public SimpleTask(String s) {
		this.s = s;
	}

	@Override
	public void run() {
	}
}