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
package dev.rico.internal.server.servlet;

import dev.rico.internal.core.Assert;
import org.apiguardian.api.API;

import java.io.Serializable;
import java.util.concurrent.Callable;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class Mutex implements Serializable {

    public <T> T sync(final Callable<T> callable) throws Exception {
        Assert.requireNonNull(callable, "callable");
        synchronized (this) {
            return callable.call();
        }
    }

    public void sync(final Runnable runnable) throws Exception {
        Assert.requireNonNull(runnable, "runnable");
        sync(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                runnable.run();
                return null;
            }
        });
    }

}
