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
package dev.rico.remoting.impl.collections;

import dev.rico.internal.remoting.collections.ObservableArrayList;
import dev.rico.remoting.ListChangeEvent;
import dev.rico.remoting.ListChangeListener;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class TestObservableArrayList {

    @Test
    public void testCreation() {
        ObservableArrayList<String> list = new ObservableArrayList<>();

        Assert.assertTrue(list.isEmpty());
        Assert.assertEquals(list.size(), 0);

        list = new ObservableArrayList<>(12);
        Assert.assertTrue(list.isEmpty());
        Assert.assertEquals(list.size(), 0);

        list = new ObservableArrayList<>("1", "2", "3");
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(list.size(), 3);

        list = new ObservableArrayList<>(Arrays.asList("1", "2", "3"));
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(list.size(), 3);
    }

    @Test
    public void testSize() {
        final ObservableArrayList<String> list = new ObservableArrayList<>();
        Assert.assertTrue(list.isEmpty());
        Assert.assertEquals(list.size(), 0);

        list.add("HUHU");
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(list.size(), 1);

        list.add("TEST");
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(list.size(), 2);

        list.clear();
        Assert.assertTrue(list.isEmpty());
        Assert.assertEquals(list.size(), 0);
    }

    @Test
    public void testAddAndRemove() {
        final ObservableArrayList<String> list = new ObservableArrayList<>();
        Assert.assertTrue(list.isEmpty());
        Assert.assertEquals(list.size(), 0);

        list.add("HUHU");
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(list.size(), 1);
        assertSameContent(list, Arrays.asList("HUHU"));
        list.clear();

        list.addAll("1", "2", "3");
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(list.size(), 3);
        assertSameContent(list, Arrays.asList("1", "2", "3"));
        list.clear();

        list.addAll(Arrays.asList("1", "2", "3"));
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(list.size(), 3);
        assertSameContent(list, Arrays.asList("1", "2", "3"));
        list.clear();

        list.addAll(Arrays.asList("1", "2", "3"));
        list.remove(0);
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(list.size(), 2);
        assertSameContent(list, Arrays.asList("2", "3"));
        list.clear();

        list.addAll(Arrays.asList("1", "2", "3"));
        list.remove(1);
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(list.size(), 2);
        assertSameContent(list, Arrays.asList("1", "3"));
        list.clear();

        list.addAll(Arrays.asList("1", "2", "3"));
        list.remove("2");
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(list.size(), 2);
        assertSameContent(list, Arrays.asList("1", "3"));
        list.clear();

        //Implementation still missing
        list.addAll(Arrays.asList("1", "2", "3"));
        list.removeAll("1", "2");
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(list.size(), 1);
        assertSameContent(list, Arrays.asList("3"));
        list.clear();

        list.addAll(Arrays.asList("1", "2", "3"));
        list.removeAll(Arrays.asList("1", "3"));
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(list.size(), 1);
        assertSameContent(list, Arrays.asList("2"));
        list.clear();
    }

    @Test
    public void testRemoveAll(){

        final AtomicBoolean removed = new AtomicBoolean(false);
        final AtomicBoolean added = new AtomicBoolean(false);
        final AtomicInteger callCount = new AtomicInteger(0);

        final ObservableArrayList<String> list = new ObservableArrayList<>();

        Assert.assertTrue(list.isEmpty());
        Assert.assertEquals(list.size(), 0);

        addOnChangeListener(removed, added, callCount, list);


        list.addAll("1", "2", "3", "4", "5", "6", "7" ,"8" , "9", "10");
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(list.size(), 10);
        Assert.assertTrue(added.get());
        Assert.assertFalse(removed.get());

        list.removeAll(Arrays.asList("1", "2", "3", "4", "5"));
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(list.size(), 5);
        assertSameContent(list, Arrays.asList("6", "7" ,"8" , "9", "10"));
        Assert.assertTrue(removed.get());
        Assert.assertFalse(added.get());
        Assert.assertEquals(6, callCount.get());
        list.clear();

    }

    private void addOnChangeListener(final AtomicBoolean removed, final AtomicBoolean added, final AtomicInteger callCount, ObservableArrayList<String> list) {
        list.onChanged(new ListChangeListener<String>() {
            @Override
            public void listChanged(ListChangeEvent<? extends String> evt) {
                callCount.incrementAndGet();
                if(evt.getChanges().iterator().next().isAdded()){
                    added.set(true);
                    removed.set(false);
                }else{
                    removed.set(true);
                    added.set(false);
                }
            }
        });
    }

    @Test
    public void testRetainAll(){

        final AtomicBoolean removed = new AtomicBoolean(false);
        final AtomicBoolean added = new AtomicBoolean(false);
        final AtomicInteger callCount = new AtomicInteger(0);

        final ObservableArrayList<String> list = new ObservableArrayList<>();
        Assert.assertTrue(list.isEmpty());
        Assert.assertEquals(list.size(), 0);

        addOnChangeListener(removed, added, callCount, list);

        list.addAll("1", "2", "3", "4", "5", "6", "7" ,"8" , "9", "10");
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(list.size(), 10);
        Assert.assertTrue(added.get());
        Assert.assertFalse(removed.get());

        list.retainAll(Arrays.asList("1", "2", "3", "4", "5"));
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(list.size(), 5);
        assertSameContent(list, Arrays.asList("1", "2", "3", "4", "5"));
        Assert.assertTrue(removed.get());
        Assert.assertFalse(added.get());

        Assert.assertEquals(callCount.get(), 6);
        list.clear();

        list.addAll("1", "2", "3", "4", "5", "6", "7" ,"8" , "9", "10");
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(list.size(), 10);

        list.retainAll("1", "2", "3", "4", "5");
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(list.size(), 5);
        assertSameContent(list, Arrays.asList("1", "2", "3", "4", "5"));
        list.clear();

    }

    @Test
    public void testListIterator(){
        final AtomicBoolean removed = new AtomicBoolean(false);
        final AtomicBoolean added = new AtomicBoolean(false);
        final AtomicInteger callCount = new AtomicInteger(0);

        final ObservableArrayList<String> list = new ObservableArrayList<>();
        Assert.assertTrue(list.isEmpty());
        Assert.assertEquals(list.size(), 0);

        addOnChangeListener(removed, added, callCount, list);

        list.addAll("1", "2", "3", "4", "5");

        ListIterator<String> iterator = list.listIterator();
        Assert.assertTrue(iterator.nextIndex() == 0);

        String current = iterator.next();
        Assert.assertTrue(current.equals("1"));
        Assert.assertTrue(iterator.nextIndex() == 1);


        current = iterator.next();
        Assert.assertTrue(current.equals("2"));
        Assert.assertTrue(iterator.nextIndex() == 2);

        current = iterator.previous();
        Assert.assertTrue(current.equals("2"));
        Assert.assertTrue(iterator.previousIndex() == 0);

        //Add using iterator
        iterator.add("Test");
        Assert.assertTrue(added.get());
        Assert.assertFalse(removed.get());
        Assert.assertEquals(list.size(), 6);
        current = iterator.previous();
        Assert.assertTrue(current.equals("Test"));
        Assert.assertTrue(iterator.nextIndex() == 1);


        //Remove using iterator
        iterator.remove();
        Assert.assertFalse(added.get());
        Assert.assertTrue(removed.get());
        Assert.assertEquals(list.size(), 5);
        current = iterator.next();
        Assert.assertTrue(current.equals("2"));
        Assert.assertTrue(iterator.nextIndex() == 2);
    }

    @Test(expectedExceptions = {IllegalStateException.class})
    public void testListIteratorWrongStateRemoveAfterAdd(){
        final ObservableArrayList<String> list = new ObservableArrayList<>();
        Assert.assertTrue(list.isEmpty());
        Assert.assertEquals(list.size(), 0);

        list.addAll("1", "2", "3", "4", "5");

        ListIterator<String> iterator = list.listIterator();
        Assert.assertTrue(iterator.nextIndex() == 0);

        iterator.next();
        iterator.next();

        //Add using iterator
        iterator.add("Test");
        Assert.assertEquals(list.size(), 6);

        // you can't remove after add...call next or previous
        iterator.remove();
    }


    @Test
    public void testRemoveRange() {
        final ObservableArrayList<String> list = new ObservableArrayList<>();
        
        Assert.assertTrue(list.isEmpty());
        Assert.assertEquals(list.size(), 0);
        
        list.addAll("1", "2", "3", "4", "5", "6", "7" ,"8" , "9", "10");
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(list.size(), 10);
        
        list.remove(1, 5);
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(list.size(), 6);
        assertSameContent(list, Arrays.asList("1", "6", "7" ,"8" , "9", "10"));
        list.clear();
    }

    /** Start of SubList Unit Test*/

    @Test
    public void testSubListGet() {
        //given
        final ObservableArrayList<String> list  = new ObservableArrayList<>();
        list.addAll("1", "2", "3", "4", "5", "6", "7" ,"8" , "9", "10");

        //when
        final List<String> subList = list.subList(1, 3);

        //then
        Assert.assertEquals(subList.size(), 2);
        Assert.assertEquals(subList.get(0), "2");
        Assert.assertEquals(subList.get(1), "3");
    }

    @Test
    public void testSubListContains() {
        //given
        final ObservableArrayList<String> list  = new ObservableArrayList<>();
        list.addAll("1", "2", "3", "4", "5", "6", "7" ,"8" , "9", "10");

        //when
        final List<String> subList = list.subList(1, 3);

        //then
        Assert.assertEquals(subList.size(), 2);
        Assert.assertTrue(subList.contains("2"));
        Assert.assertTrue(subList.contains("3"));
        Assert.assertFalse(subList.contains("4"));

    }

    @Test
    public void testSubListContainsAll() {
        //given
        final ObservableArrayList<String> list  = new ObservableArrayList<>();
        list.addAll("1", "2", "3", "4", "5", "6", "7" ,"8" , "9", "10");

        //when
        final List<String> subList = list.subList(1, 3);

        //then
        Assert.assertEquals(subList.size(), 2);
        Assert.assertTrue(subList.containsAll(Arrays.asList("2", "3")));
        Assert.assertFalse(subList.containsAll(Arrays.asList("2", "3", "4")));

    }

    @Test
    public void testSubListClear() {
        //given
        final ObservableArrayList<String> list  = new ObservableArrayList<>();
        list.addAll("1", "2", "3", "4", "5", "6", "7" ,"8" , "9", "10");

        //when
        final List<String> subList = list.subList(1, 5);
        subList.clear();

        //then
        Assert.assertEquals(subList.size(), 0);
        Assert.assertTrue(subList.isEmpty());

        Assert.assertEquals(list.size(), 6);
        Assert.assertEquals(list.get(0), "1");
        Assert.assertEquals(list.get(1), "6");
        Assert.assertEquals(list.get(2), "7");
        Assert.assertEquals(list.get(3), "8");
        Assert.assertEquals(list.get(4), "9");
        Assert.assertEquals(list.get(5), "10");

    }

    @Test
    public void testSubListClearFromBaseList() {
        //given
        final ObservableArrayList<String> list  = new ObservableArrayList<>();
        list.addAll("1", "2", "3", "4", "5", "6", "7" ,"8" , "9", "10");

        //when
        final List<String> subList = list.subList(1, 5);
        list.clear();

        //then
        Assert.assertEquals(subList.size(), 0);
        Assert.assertEquals(list.size(), 0);
    }

    @Test
    public void testSubListAdd() {
        //given
        final ObservableArrayList<String> list = new ObservableArrayList<>();
        list.addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");

        //when
        final List<String> subList = list.subList(3, 8);
        subList.add("11");

        //then
        Assert.assertEquals(subList.size(), 6);
        Assert.assertEquals(list.size(), 11);
        Assert.assertEquals(list.get(8), "11");
        Assert.assertEquals(list.get(8), "11");
    }

    @Test
    public void testSubListSet() {
        //given
        final ObservableArrayList<String> list = new ObservableArrayList<>();
        list.addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");

        //when
        final List<String> subList = list.subList(3, 8);
        subList.set(0,"11");

        //then
        Assert.assertEquals(subList.size(), 5);
        Assert.assertEquals(subList.get(0), "11");

        Assert.assertEquals(list.size(), 10);
        Assert.assertEquals(list.get(3), "11");
    }

    @Test
    public void testSubListSetFromBaseList() {
        //given
        final ObservableArrayList<String> list = new ObservableArrayList<>();
        list.addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");

        //when
        final List<String> subList = list.subList(3, 8);
        list.set(0,"11");
        list.set(3,"12");

        //then
        Assert.assertEquals(subList.size(), 5);
        Assert.assertEquals(subList.get(0), "12");

        Assert.assertEquals(list.size(), 10);
        Assert.assertEquals(list.get(0), "11");
        Assert.assertEquals(list.get(3), "12");
    }

    @Test
    public void testSubListRemove() {
        //given
        final ObservableArrayList<String> list = new ObservableArrayList<>();
        list.addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");

        //when
        final List<String> subList = list.subList(3, 8);
        subList.remove("6");

        //then
        Assert.assertEquals(subList.size(), 4);
        Assert.assertEquals(list.size(), 9);
        Assert.assertFalse(list.contains("6"));
        Assert.assertFalse(subList.contains("6"));
        Assert.assertEquals(list.get(4), "5");
        Assert.assertEquals(list.get(5), "7");
    }

    @Test
    public void testSubListRemoveFromBaseList() {
        //given
        final ObservableArrayList<String> list = new ObservableArrayList<>();
        list.addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");

        //when
        final List<String> subList = list.subList(3, 8);
        list.remove("6");
        list.remove("9");

        //then
        Assert.assertEquals(subList.size(), 4);
        Assert.assertEquals(list.size(), 8);
        Assert.assertFalse(list.contains("6"));
        Assert.assertFalse(list.contains("9"));
        Assert.assertFalse(subList.contains("6"));
    }

    @Test
    public void testSubListAddAll() {
        //given
        final ObservableArrayList<String> list = new ObservableArrayList<>();
        list.addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");

        //when
        final List<String> subList = list.subList(0, 5);
        subList.addAll(Arrays.asList("11", "12", "13"));

        //then
        Assert.assertEquals(subList.size(), 8);
        Assert.assertEquals(list.size(), 13);

        Assert.assertEquals(subList.get(5), "11");
        Assert.assertEquals(subList.get(6), "12");
        Assert.assertEquals(subList.get(7), "13");

        Assert.assertEquals(list.get(5), "11");
        Assert.assertEquals(list.get(6), "12");
        Assert.assertEquals(list.get(7), "13");
    }

    @Test
    public void testSubListRemoveAll() {
        //given
        final ObservableArrayList<String> list = new ObservableArrayList<>();
        list.addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");

        //when
        final List<String> subList = list.subList(1, 5);
        subList.removeAll(Arrays.asList("2", "3"));

        //then
        Assert.assertEquals(subList.size(), 2);
        Assert.assertEquals(list.size(), 8);
        Assert.assertFalse(list.contains("2"));
        Assert.assertFalse(list.contains("3"));

        Assert.assertFalse(subList.contains("2"));
        Assert.assertFalse(subList.contains("3"));
    }

    @Test
    public void testSubListRemoveAllFromBaseList() {
        //given
        final ObservableArrayList<String> list = new ObservableArrayList<>();
        list.addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");

        //when
        final List<String> subList = list.subList(1, 5);
        list.removeAll(Arrays.asList("2", "3", "8", "9"));

        //then
        Assert.assertEquals(subList.size(), 2);
        Assert.assertFalse(subList.contains("2"));
        Assert.assertFalse(subList.contains("3"));

        Assert.assertEquals(list.size(), 6);
        Assert.assertFalse(list.contains("2"));
        Assert.assertFalse(list.contains("3"));
        Assert.assertFalse(list.contains("8"));
        Assert.assertFalse(list.contains("9"));

    }


    @Test
    public void testSubListRetainAll() {
        //given
        final ObservableArrayList<String> list = new ObservableArrayList<>();
        list.addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");

        //when
        final List<String> subList = list.subList(0, 5);
        subList.retainAll(Arrays.asList("2", "3"));

        //then
        Assert.assertEquals(subList.size(), 2);
        Assert.assertEquals(list.size(), 7);

        Assert.assertTrue(subList.contains("2"));
        Assert.assertTrue(subList.contains("3"));
        Assert.assertFalse(subList.contains("1"));
        Assert.assertFalse(subList.contains("4"));
        Assert.assertFalse(subList.contains("5"));
        Assert.assertEquals(subList.get(0), "2");
        Assert.assertEquals(subList.get(1), "3");

        Assert.assertTrue(list.contains("2"));
        Assert.assertTrue(list.contains("3"));
        Assert.assertFalse(list.contains("1"));
        Assert.assertFalse(list.contains("4"));
        Assert.assertFalse(list.contains("5"));

        Assert.assertEquals(list.get(0), "2");
        Assert.assertEquals(list.get(1), "3");
        Assert.assertEquals(list.get(2), "6");
    }


    @Test
    public void testRemoveByIndexPositions() {
        //given
        final ObservableArrayList<String> list = new ObservableArrayList<>();
        list.addAll("1", "2", "3", "4", "5");
        list.remove(0,3);
        Assert.assertEquals(list.size(), 2);

        Assert.assertFalse(list.contains("1"));
        Assert.assertFalse(list.contains("2"));
        Assert.assertFalse(list.contains("3"));

        Assert.assertTrue(list.contains("4"));
        Assert.assertTrue(list.contains("5"));

    }

    /** End of SubList Unit Test*/

    private <T> void assertSameContent(List<T> a, List<T> b) {
        Assert.assertTrue(a.size() == b.size());
        for(T t : a) {
            Assert.assertTrue(b.contains(t));
            Assert.assertTrue(a.indexOf(t) == b.indexOf(t));
        }
    }

}
