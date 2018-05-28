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
package dev.rico.client.remoting.binding;

import dev.rico.core.functional.Binding;
import dev.rico.remoting.ObservableList;
import org.apiguardian.api.API;

import java.util.function.Function;

import static dev.rico.internal.core.Assert.*;
import static org.apiguardian.api.API.Status.MAINTAINED;

@API(since = "0.x", status = MAINTAINED)
public interface JavaFXListBinder<S> {

    default Binding to(ObservableList<? extends S> remotingList) {
        requireNonNull(remotingList, "remotingList");
        return to(remotingList, Function.<S>identity());
    }

    <T> Binding to(ObservableList<T> remotingList, Function<? super T, ? extends S> converter);

    default Binding bidirectionalTo(ObservableList<S> remotingList) {
        requireNonNull(remotingList, "remotingList");
        return bidirectionalTo(remotingList, Function.identity(), Function.identity());
    }

    <T> Binding bidirectionalTo(ObservableList<T> remotingList, Function<? super T, ? extends S> converter, Function<? super S, ? extends T> backConverter);

}
