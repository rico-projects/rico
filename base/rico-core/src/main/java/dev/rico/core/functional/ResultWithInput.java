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
package dev.rico.core.functional;

/**
 * Extension for the {@link Result} interface that provides access to the input
 * of the function on that the result is based.
 *
 * @param <V> type of the input
 * @param <R> type of the output
 */
public interface ResultWithInput<V, R> extends Result<R> {

    /**
     * Returns the input of the based function
     *
     * @return the input of the based function
     */
    V getInput();
}
