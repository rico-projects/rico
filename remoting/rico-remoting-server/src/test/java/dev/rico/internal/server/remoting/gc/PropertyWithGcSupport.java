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
package dev.rico.internal.server.remoting.gc;

public class PropertyWithGcSupport<T> extends MockedProperty<T> {

    private final GarbageCollector garbageCollector;

    public PropertyWithGcSupport(final GarbageCollector garbageCollector) {
        this.garbageCollector = garbageCollector;
    }

    @Override
    public void set(final T value) {
        final T oldValue = get();
        super.set(value);
        garbageCollector.onPropertyValueChanged(this, oldValue, value);
    }
}
