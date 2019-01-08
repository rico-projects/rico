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
package dev.rico.remoting;

import org.apiguardian.api.API;

import java.util.EventListener;

import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * A change listener that supports generics.
 * Usage in the remoting:
 * Listener instances can be registered to {@link Property}
 * instances to observe the internal value of the {@link Property}. Whenever
 * the internal value of the {@link Property} is changed the {@link #valueChanged(ValueChangeEvent)}
 * method will be called for all registered {@link ValueChangeListener} instances.
 *
 * @param <T> type of the value that is observed by this listener
 * @author Hendrik Ebbers
 */
@API(since = "0.x", status = MAINTAINED)
public interface ValueChangeListener<T> extends EventListener {

    /**
     * This method is called whenever the observed value has been changed.
     *
     * @param evt the event that defines the change of the observed value.
     */
    void valueChanged(ValueChangeEvent<? extends T> evt);

}
