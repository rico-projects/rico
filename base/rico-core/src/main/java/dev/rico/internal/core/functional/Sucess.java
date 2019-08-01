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
package dev.rico.internal.core.functional;

import dev.rico.core.functional.ResultWithInput;

/**
 * Implementation of a {@link dev.rico.core.functional.Result} that is based
 * on a sucessfully executed function
 * @param <T> type of the input
 * @param <R> type of the output
 */
public class Sucess<T, R> implements ResultWithInput<T, R> {

    private final T input;

    private final R result;

    public Sucess(final T input, final R result) {
        this.input = input;
        this.result = result;
    }

    public Sucess(final R result) {
        this.input = null;
        this.result = result;
    }

    @Override
    public boolean isSuccessful() {
        return true;
    }

    @Override
    public T getInput() {
        return input;
    }

    @Override
    public Exception getException() {
        throw new IllegalStateException("No exception since call was sucessfull");
    }

    @Override
    public R getResult() {
        return result;
    }
}

