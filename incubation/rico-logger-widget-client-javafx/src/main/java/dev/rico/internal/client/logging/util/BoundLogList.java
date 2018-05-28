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
package dev.rico.internal.client.logging.util;

import dev.rico.internal.core.Assert;
import dev.rico.core.functional.Subscription;
import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by hendrikebbers on 29.12.17.
 */
public class BoundLogList<E> implements ObservableList<E>, Subscription {

    private final Subscription subscription;

    private final ObservableList<E> observableList;

    public BoundLogList(final Subscription subscription, final ObservableList<E> observableList) {
        this.subscription = Assert.requireNonNull(subscription, "subscription");
        this.observableList = Assert.requireNonNull(observableList, "observableList");
    }

    @Override
    public void unsubscribe() {
        subscription.unsubscribe();
    }

    @Override
    public void addListener(final ListChangeListener<? super E> listener) {
        observableList.addListener(listener);
    }

    @Override
    public void removeListener(final ListChangeListener<? super E> listener) {
        observableList.removeListener(listener);
    }

    @Override
    public boolean addAll(final E[] elements) {
        throw new RuntimeException("Can not mutate " + getClass());
    }

    @Override
    public boolean setAll(final E[] elements) {
        throw new RuntimeException("Can not mutate " + getClass());
    }

    @Override
    public boolean setAll(final Collection<? extends E> col) {
        throw new RuntimeException("Can not mutate " + getClass());
    }

    @Override
    public boolean removeAll(final E[] elements) {
        throw new RuntimeException("Can not mutate " + getClass());
    }

    @Override
    public boolean retainAll(final E[] elements) {
        throw new RuntimeException("Can not mutate " + getClass());
    }

    @Override
    public void remove(final int from, final int to) {
        throw new RuntimeException("Can not mutate " + getClass());
    }

    @Override
    public int size() {
        return observableList.size();
    }

    @Override
    public boolean isEmpty() {
        return observableList.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        return observableList.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return observableList.iterator();
    }

    @Override
    public Object[] toArray() {
        return observableList.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        return observableList.toArray(a);
    }

    @Override
    public boolean add(final E e) {
        throw new RuntimeException("Can not mutate " + getClass());
    }

    @Override
    public boolean remove(final Object o) {
        throw new RuntimeException("Can not mutate " + getClass());
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return observableList.containsAll(c);
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {
        throw new RuntimeException("Can not mutate " + getClass());
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends E> c) {
        throw new RuntimeException("Can not mutate " + getClass());
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        throw new RuntimeException("Can not mutate " + getClass());
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        throw new RuntimeException("Can not mutate " + getClass());
    }

    @Override
    public void clear() {
        throw new RuntimeException("Can not mutate " + getClass());
    }

    @Override
    public E get(final int index) {
        return observableList.get(index);
    }

    @Override
    public E set(final int index, final E element) {
        throw new RuntimeException("Can not mutate " + getClass());
    }

    @Override
    public void add(final int index, final E element) {
        throw new RuntimeException("Can not mutate " + getClass());
    }

    @Override
    public E remove(final int index) {
        throw new RuntimeException("Can not mutate " + getClass());
    }

    @Override
    public int indexOf(final Object o) {
        return observableList.indexOf(o);
    }

    @Override
    public int lastIndexOf(final Object o) {
        return observableList.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return observableList.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(final int index) {
        return observableList.listIterator(index);
    }

    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        return observableList.subList(fromIndex, toIndex);
    }

    @Override
    public void addListener(final InvalidationListener listener) {
        observableList.addListener(listener);
    }

    @Override
    public void removeListener(final InvalidationListener listener) {
        observableList.removeListener(listener);
    }
}
