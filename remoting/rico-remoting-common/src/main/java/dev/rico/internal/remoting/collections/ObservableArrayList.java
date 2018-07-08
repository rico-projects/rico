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
package dev.rico.internal.remoting.collections;

import dev.rico.internal.core.Assert;
import dev.rico.remoting.ListChangeEvent;
import dev.rico.remoting.ListChangeListener;
import dev.rico.remoting.ObservableList;
import dev.rico.core.functional.Subscription;
import org.apiguardian.api.API;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ObservableArrayList<E> implements ObservableList<E> {

    private final ArrayList<E> list;
    private final List<ListChangeListener<? super E>> listeners = new CopyOnWriteArrayList<>();

    public ObservableArrayList() {
        list = new ArrayList<>();
    }

    public ObservableArrayList(final int initialCapacity) {
        list = new ArrayList<>(initialCapacity);
    }

    public ObservableArrayList(final Collection<? extends E> c) {
        list = new ArrayList<>(c);
    }

    @SafeVarargs
    public ObservableArrayList(final E... elements) {
        this(Arrays.asList(elements));
    }

    protected void fireListChanged(final ListChangeEvent<E> event) {
        notifyInternalListeners(event);
        notifyExternalListeners(event);
    }

    protected void notifyInternalListeners(final ListChangeEvent<E> event) {

    }

    protected void notifyExternalListeners(final ListChangeEvent<E> event) {
        for (final ListChangeListener<? super E> listener : listeners) {
            listener.listChanged(event);
        }
    }

    public void internalSplice(final int from, final int to, final Collection<? extends E> newElements) {
        final List<E> slice = list.subList(from, to);
        final List<E> removedElements = new ArrayList<>(slice);
        slice.clear();
        list.addAll(from, newElements);
        notifyExternalListeners(new ListChangeEventImpl<E>(this, from, from + newElements.size(), removedElements));
    }

    @Override
    public Subscription onChanged(final ListChangeListener<? super E> listener) {
        listeners.add(listener);
        return new Subscription() {
            @Override
            public void unsubscribe() {
                listeners.remove(listener);
            }
        };
    }

    @Override
    public boolean addAll(final E... elements) {
        return addAll(Arrays.asList(elements));
    }

    @Override
    public boolean setAll(final E... elements) {
        return setAll(Arrays.asList(elements));
    }

    @Override
    public boolean removeAll(final E... elements) {
        return removeAll(Arrays.asList(elements));
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return new ListIteratorWrapper(list.listIterator());
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(final E e) {
        add(list.size(), e);
        return true;
    }

    @Override
    public boolean remove(final Object o) {
        final int index = list.indexOf(o);
        if (index >= 0) {
            remove(index);
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {
        return addAll(list.size(), c);
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends E> c) {
        if (c.isEmpty()) {
            return false;
        }
        list.addAll(index, c);
        fireListChanged(new ListChangeEventImpl<>(this, index, index + c.size(), Collections.<E>emptyList()));
        return true;
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return batchRemove(c, true);
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return batchRemove(c, false);
    }

    @Override
    public boolean retainAll(final E... elements) {
        return batchRemove(Arrays.asList(elements), false);
    }

    private boolean batchRemove(final Collection<?> c, boolean isRemove){
        if (null != c && c.isEmpty()) {
            return false;
        }
        final List<E> listElement =  new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            E element;
            if (c.contains(list.get(i)) && isRemove) {
                element = list.get(i);
                listElement.add(element);
            }
            if (!c.contains(list.get(i)) && !isRemove) {
                element = list.get(i);
                listElement.add(element);
            }
        }
        if(!listElement.isEmpty()){
            for(E e:listElement){
                remove(e);
            }
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        if (isEmpty()) {
            return;
        }
        final ArrayList<E> removed = new ArrayList<>(list);
        list.clear();
        fireListChanged(new ListChangeEventImpl<>(this, 0, 0, removed));
    }

    @Override
    public E get(final int index) {
        return list.get(index);
    }

    @Override
    public E set(final int index, final E element) {
        final E oldElement = list.set(index, element);
        fireListChanged(new ListChangeEventImpl<>(this, index, index + 1, Collections.singletonList(oldElement)));
        return oldElement;
    }

    @Override
    public void add(final int index, final E element) {
        list.add(index, element);
        fireListChanged(new ListChangeEventImpl<>(this, index, index + 1, Collections.<E>emptyList()));
    }

    @Override
    public E remove(final int index) {
        final E oldElement = list.remove(index);
        fireListChanged(new ListChangeEventImpl<>(this, index, index, Collections.singletonList(oldElement)));
        return oldElement;
    }

    @Override
    public void remove(final int from, final int to)
    {
        final List<E> toRemove = new ArrayList<>(list.subList(from, to));
        toRemove.forEach(e -> remove(e));
    }

    @Override
    public int indexOf(final Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(final Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return new ListIteratorWrapper(list.listIterator());
    }

    @Override
    public ListIterator<E> listIterator(final int index) {
        return new ListIteratorWrapper(list.listIterator(index));
    }

    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        final ObservableArrayList subList = new ObservableArrayList(list.subList(fromIndex, toIndex));
        subList.onChanged(new ListChangeListener() {
            @Override
            public void listChanged(ListChangeEvent evt) {
                final List<ListChangeEvent.Change<E>> changes = Assert.requireNonNull(evt, "evt").getChanges();
                for (final ListChangeEvent.Change<E> change : changes) {
                    if (change.isAdded()) {
                        final int fromIndex = list.indexOf(evt.getSource().get(change.getFrom() - 1)) + 1;
                        list.addAll(fromIndex, subList.subList(change.getFrom(), change.getTo()));
                    } else if (change.isReplaced()) {
                        if (list.contains(change.getRemovedElements().get(0))) {
                            final int index = list.indexOf(change.getRemovedElements().get(0));
                            list.set(index, (E) subList.get(change.getFrom()));
                        }
                    } else if (change.isRemoved()) {
                        list.removeAll(change.getRemovedElements());
                    }
                }
            }
        });

        onChanged(new ListChangeListener<E>() {
            @Override
            public void listChanged(ListChangeEvent<? extends E> evt) {
                final List<? extends ListChangeEvent.Change<? extends E>> changes = Assert.requireNonNull(evt, "evt").getChanges();
                for (final ListChangeEvent.Change<? extends E> change : changes) {
                    if (change.isAdded()) {
                        //TODO Add the element to sublist when adding element in base list
                    } else if (change.isReplaced()) {
                        if (subList.contains(change.getRemovedElements().get(0))) {
                            final int index = subList.indexOf(change.getRemovedElements().get(0));
                            subList.set(index, (E) list.get(change.getFrom()));
                        }
                    } else if (change.isRemoved()) {
                        subList.removeAll(change.getRemovedElements());
                    }
                }
            }
        });
        return subList;
    }

    private class ListIteratorWrapper implements ListIterator<E> {

        private final ListIterator<E> iterator;
        int lastRet = -1; // index of last element returned;

        private ListIteratorWrapper (ListIterator<E> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public E next() {
            final E e = iterator.next();
            lastRet = ObservableArrayList.this.list.indexOf(e);
            return e;
        }

        @Override
        public boolean hasPrevious() {
            return iterator.hasPrevious();
        }

        @Override
        public E previous() {
            final E e = iterator.previous();
            lastRet = ObservableArrayList.this.list.indexOf(e);
            return e;
        }

        @Override
        public int nextIndex() {
            return iterator.nextIndex();
        }

        @Override
        public int previousIndex() {
            return iterator.previousIndex();
        }

        @Override
        public void remove() {
            E oldElement = null;
            if(lastRet >=0){
                oldElement = ObservableArrayList.this.get(lastRet);
            }// do not throw any exception...it will be thrown by next line
            iterator.remove();
            int removedIndex = iterator.nextIndex();
            fireListChanged(new ListChangeEventImpl<>(ObservableArrayList.this, removedIndex, removedIndex, Collections.singletonList(oldElement)));
            lastRet = -1;
        }

        @Override
        public void set(final E e) {
            int replacedIndex = iterator.nextIndex();
            final E oldElement = ObservableArrayList.this.get(replacedIndex);
            iterator.set(e);
            fireListChanged(new ListChangeEventImpl<>(ObservableArrayList.this, replacedIndex, replacedIndex, Collections.singletonList(oldElement)));
        }

        @Override
        public void add(final E e) {
            int addedIndex = iterator.nextIndex();
            iterator.add(e);
            lastRet = -1;
            fireListChanged(new ListChangeEventImpl<>(ObservableArrayList.this, addedIndex, addedIndex + 1, Collections.<E>emptyList()));
        }
    }

    @Override
    public boolean setAll(final Collection<? extends E> col) {
        clear();
        return addAll(col);
    }

    @Override
    public boolean equals(final Object o) {
        return list.equals(o);
    }

    @Override
    public int hashCode() {
        return list.hashCode();
    }

    @Override
    public String toString() {
        return list.toString();
    }
}