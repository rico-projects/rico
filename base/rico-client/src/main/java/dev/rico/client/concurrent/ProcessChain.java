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
package dev.rico.client.concurrent;

import javafx.concurrent.Task;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ProcessChain<T> {

    <V> ProcessChain<V> addUiFunction(final Function<T, V> function);

    <V> ProcessChain<V> addBackgroundFunction(final Function<T, V> function);


    ProcessChain<Void> addUiRunnable(final Runnable runnable);

    ProcessChain<Void> addBackgroundRunnable(final Runnable runnable);


    ProcessChain<Void> addUiConsumer(final Consumer<T> consumer);

    ProcessChain<Void> addBackgroundConsumer(final Consumer<T> consumer);


    <V> ProcessChain<V> addUiSupplier(final Supplier<V> supplier);

    <V> ProcessChain<V> addBackgroundSupplier(final Supplier<V> supplier);


    ProcessChain<T> onException(final Consumer<Throwable> consumer);

    ProcessChain<T> withUiFinal(final Runnable finalRunnable);


    Task<T> run();
}
