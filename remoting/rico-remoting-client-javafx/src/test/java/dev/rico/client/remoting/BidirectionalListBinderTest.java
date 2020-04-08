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
package dev.rico.client.remoting;

import dev.rico.remoting.ObservableList;
import dev.rico.internal.remoting.collections.ObservableArrayList;
import javafx.collections.FXCollections;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;

public class BidirectionalListBinderTest {

    //////////////////
    // Initialisation
    //////////////////
    @Test
    public void shouldClearElements() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        javaFXList.addAll("1", "2", "3");
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();

        // when:
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);

        // then:
        assertThat(javaFXList, empty());
    }

    @Test
    public void shouldAddElements() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        remotingList.addAll(1, 2, 3);

        // when:
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);

        // then:
        assertThat(javaFXList, contains("1", "2", "3"));
    }

    @Test
    public void shouldReplaceElements() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        javaFXList.addAll("41", "42");
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        remotingList.addAll(1, 2, 3);

        // when:
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);

        // then:
        assertThat(javaFXList, contains("1", "2", "3"));
    }



    ////////////////////////////////
    // Parameter check
    ////////////////////////////////
    @SuppressWarnings("unchecked")
    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowNPEIfJavaFXListIsNull() {
        // when:
        FXBinder.bind((javafx.collections.ObservableList)null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowNPEIfremotingListIsNull() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();

        // when:
        FXBinder.bind(javaFXList).bidirectionalTo(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowNPEIfremotingListIsNull_WithConverters() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();

        // when:
        FXBinder.bind(javaFXList).bidirectionalTo(null, Function.identity(), Function.identity());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowNPEIfConverterIsNull() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<String> remotingList = new ObservableArrayList<>();

        // when:
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, null, Function.identity());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowNPEIfBackconverterIsNull() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<String> remotingList = new ObservableArrayList<>();

        // when:
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Function.identity(), null);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void shouldThrowIAEIfJavaFXListIsBound() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<String> remotingList1 = new ObservableArrayList<>();
        final ObservableList<String> remotingList2 = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).to(remotingList1, Function.identity());

        // when:
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList2, Function.identity(), Function.identity());
    }



    ////////////////////////////////
    // Add elements to List
    ////////////////////////////////

    @Test
    public void shouldAddSingleElementToEmptyremotingList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);

        // when:
        javaFXList.add("1");

        // then:
        assertThat(remotingList, contains(1));
    }

    @Test
    public void shouldAddMultipleElementsToEmptyremotingList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);

        // when:
        javaFXList.addAll("1", "2", "3");

        // then:
        assertThat(remotingList, contains(1, 2, 3));
    }

    @Test
    public void shouldAddSingleElementAtBeginningOfremotingList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        remotingList.addAll(41, 42, 43);

        // when:
        javaFXList.add(0, "1");

        // then:
        assertThat(remotingList, contains(1, 41, 42, 43));
    }

    @Test
    public void shouldAddMultipleElementsAtBeginningOfremotingList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        remotingList.addAll(41, 42, 43);

        // when:
        javaFXList.addAll(0, Arrays.asList("1", "2", "3"));

        // then:
        assertThat(remotingList, contains(1, 2, 3, 41, 42, 43));
    }

    @Test
    public void shouldAddSingleElementInMiddleOfremotingList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        remotingList.addAll(41, 42, 43);

        // when:
        javaFXList.add(1, "1");

        // then:
        assertThat(remotingList, contains(41, 1, 42, 43));
    }

    @Test
    public void shouldAddMultipleElementsInMiddleOfremotingList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        remotingList.addAll(41, 42, 43);

        // when:
        javaFXList.addAll(1, Arrays.asList("1", "2", "3"));

        // then:
        assertThat(remotingList, contains(41, 1, 2, 3, 42, 43));
    }

    @Test
    public void shouldAddSingleElementAtEndOfremotingList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        remotingList.addAll(41, 42, 43);

        // when:
        javaFXList.add("1");

        // then:
        assertThat(remotingList, contains(41, 42, 43, 1));
    }

    @Test
    public void shouldAddMultipleElementsAtEndOfremotingList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        remotingList.addAll(41, 42, 43);

        // when:
        javaFXList.addAll(Arrays.asList("1", "2", "3"));

        // then:
        assertThat(remotingList, contains(41, 42, 43, 1, 2, 3));
    }



    /////////////////////////////////////
    // Remove elements from List
    /////////////////////////////////////

    @Test
    public void shouldRemoveSingleElementFromSingleElementremotingList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        remotingList.add(1);

        // when:
        javaFXList.remove("1");

        // then:
        assertThat(remotingList, empty());
    }

    @Test
    public void shouldRemoveAllElementsFromremotingList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        remotingList.addAll(1, 2, 3);

        // when:
        javaFXList.clear();

        // then:
        assertThat(remotingList, empty());
    }

    @Test
    public void shouldRemoveSingleElementAtBeginningOfremotingList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        remotingList.addAll(1, 2, 3);

        // when:
        javaFXList.remove("1");

        // then:
        assertThat(remotingList, contains(2, 3));
    }

    @Test
    public void shouldRemoveMultipleElementsAtBeginningOfremotingList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        remotingList.addAll(1, 2, 3, 4, 5);

        // when:
        javaFXList.subList(0, 3).clear();

        // then:
        assertThat(remotingList, contains(4, 5));
    }

    @Test
    public void shouldRemoveSingleElementInMiddleOfremotingList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        remotingList.addAll(1, 2, 3);

        // when:
        javaFXList.remove("2");

        // then:
        assertThat(remotingList, contains(1, 3));
    }

    @Test
    public void shouldRemoveMultipleElementsInMiddleOfremotingList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        remotingList.addAll(1, 2, 3, 4, 5);

        // when:
        javaFXList.subList(1, 4).clear();

        // then:
        assertThat(remotingList, contains(1, 5));
    }

    @Test
    public void shouldRemoveSingleElementAtEndOfremotingList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        remotingList.addAll(1, 2, 3);

        // when:
        javaFXList.remove("3");

        // then:
        assertThat(remotingList, contains(1, 2));
    }

    @Test
    public void shouldRemoveMultipleElementsAtEndOfremotingList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        remotingList.addAll(1, 2, 3, 4, 5);

        // when:
        javaFXList.subList(2, 5).clear();

        // then:
        assertThat(remotingList, contains(1, 2));
    }



    ////////////////////////////////////
    // Replace elements in List
    ////////////////////////////////////

    @Test
    public void shouldReplaceSingleElementInSingleElementremotingList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        remotingList.add(1);

        // when:
        javaFXList.set(0, "42");

        // then:
        assertThat(remotingList, contains(42));
    }

    @Test
    public void shouldReplaceAllElementsInremotingList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        remotingList.addAll(1, 2, 3);

        // when:
        javaFXList.setAll("41", "42", "43");

        // then:
        assertThat(remotingList, contains(41, 42, 43));
    }

    @Test
    public void shouldReplaceSingleElementAtBeginningOfremotingList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        remotingList.addAll(1, 2, 3);

        // when:
        javaFXList.set(0, "42");

        // then:
        assertThat(remotingList, contains(42, 2, 3));
    }

    @Test
    public void shouldReplaceSingleElementInMiddleOfremotingList() {
        // given:
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        remotingList.addAll(1, 2, 3);

        // when:
        javaFXList.set(1, "42");

        // then:
        assertThat(remotingList, contains(1, 42, 3));
    }

    @Test
    public void shouldReplaceSingleElementAtEndOfremotingList() {
        // given:
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        remotingList.addAll(1, 2, 3);

        // when:
        javaFXList.set(2, "42");

        // then:
        assertThat(remotingList, contains(1, 2, 42));
    }



    ///////////////////////////////
    // Add elements to JavaFX List
    ///////////////////////////////

    @Test
    public void shouldAddSingleElementToEmptyJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);

        // when:
        remotingList.add(1);

        // then:
        assertThat(javaFXList, contains("1"));
    }

    @Test
    public void shouldAddMultipleElementsToEmptyJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);

        // when:
        remotingList.addAll(1, 2, 3);

        // then:
        assertThat(javaFXList, contains("1", "2", "3"));
    }

    @Test
    public void shouldAddSingleElementAtBeginningOfJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        javaFXList.addAll("41", "42", "43");

        // when:
        remotingList.add(0, 1);

        // then:
        assertThat(javaFXList, contains("1", "41", "42", "43"));
    }

    @Test
    public void shouldAddMultipleElementsAtBeginningOfJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        javaFXList.addAll("41", "42", "43");

        // when:
        remotingList.addAll(0, Arrays.asList(1, 2, 3));

        // then:
        assertThat(javaFXList, contains("1", "2", "3", "41", "42", "43"));
    }

    @Test
    public void shouldAddSingleElementInMiddleOfJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        javaFXList.addAll("41", "42", "43");

        // when:
        remotingList.add(1, 1);

        // then:
        assertThat(javaFXList, contains("41", "1", "42", "43"));
    }

    @Test
    public void shouldAddMultipleElementsInMiddleOfJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        javaFXList.addAll("41", "42", "43");

        // when:
        remotingList.addAll(1, Arrays.asList(1, 2, 3));

        // then:
        assertThat(javaFXList, contains("41", "1", "2", "3", "42", "43"));
    }

    @Test
    public void shouldAddSingleElementAtEndOfJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        javaFXList.addAll("41", "42", "43");

        // when:
        remotingList.add(1);

        // then:
        assertThat(javaFXList, contains("41", "42", "43", "1"));
    }

    @Test
    public void shouldAddMultipleElementsAtEndOfJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        javaFXList.addAll("41", "42", "43");

        // when:
        remotingList.addAll(Arrays.asList(1, 2, 3));

        // then:
        assertThat(javaFXList, contains("41", "42", "43", "1", "2", "3"));
    }



    /////////////////////////////////////
    // Remove elements from List
    /////////////////////////////////////

    @Test
    public void shouldRemoveSingleElementFromSingleElementJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        javaFXList.add("1");

        // when:
        remotingList.remove(Integer.valueOf(1));

        // then:
        assertThat(javaFXList, empty());
    }

    @Test
    public void shouldRemoveAllElementsFromJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        javaFXList.addAll("1", "2", "3");

        // when:
        remotingList.clear();

        // then:
        assertThat(javaFXList, empty());
    }

    @Test
    public void shouldRemoveSingleElementAtBeginningOfJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        javaFXList.addAll("1", "2", "3");

        // when:
        remotingList.remove(Integer.valueOf(1));

        // then:
        assertThat(javaFXList, contains("2", "3"));
    }

    // TODO: Enable once remotingList.subList() was implemented
    @Test (enabled = false)
    public void shouldRemoveMultipleElementsAtBeginningOfJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        javaFXList.addAll("1", "2", "3", "4", "5");

        // when:
        remotingList.subList(0, 3).clear();

        // then:
        assertThat(javaFXList, contains(4, 5));
    }

    @Test
    public void shouldRemoveSingleElementInMiddleOfJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        javaFXList.addAll("1", "2", "3");

        // when:
        remotingList.remove(Integer.valueOf(2));

        // then:
        assertThat(javaFXList, contains("1", "3"));
    }

    // TODO: Enable once remotingList.subList() was implemented
    @Test (enabled = false)
    public void shouldRemoveMultipleElementsInMiddleOfJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        javaFXList.addAll("1", "2", "3", "4", "5");

        // when:
        remotingList.subList(1, 4).clear();

        // then:
        assertThat(javaFXList, contains("1", 5));
    }

    @Test
    public void shouldRemoveSingleElementAtEndOfJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        javaFXList.addAll("1", "2", "3");

        // when:
        remotingList.remove(Integer.valueOf(3));

        // then:
        assertThat(javaFXList, contains("1", "2"));
    }

    // TODO: Enable once remotingList.subList() was implemented
    @Test (enabled = false)
    public void shouldRemoveMultipleElementsAtEndOfJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        javaFXList.addAll("1", "2", "3", "4", "5");

        // when:
        remotingList.subList(2, 5).clear();

        // then:
        assertThat(javaFXList, contains("1", "2"));
    }



    ////////////////////////////////////
    // Replace elements in List
    ////////////////////////////////////

    @Test
    public void shouldReplaceSingleElementInSingleElementJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        javaFXList.add("1");

        // when:
        remotingList.set(0, 42);

        // then:
        assertThat(javaFXList, contains("42"));
    }

    @Test
    public void shouldReplaceAllElementsInJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        javaFXList.addAll("1", "2", "3");

        // when:
        remotingList.setAll(41, 42, 43);

        // then:
        assertThat(javaFXList, contains("41", "42", "43"));
    }

    @Test
    public void shouldReplaceSingleElementAtBeginningOfJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        javaFXList.addAll("1", "2", "3");

        // when:
        remotingList.set(0, 42);

        // then:
        assertThat(javaFXList, contains("42", "2", "3"));
    }

    @Test
    public void shouldReplaceSingleElementInMiddleOfJavaFXList() {
        // given:
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        javaFXList.addAll("1", "2", "3");

        // when:
        remotingList.set(1, 42);

        // then:
        assertThat(javaFXList, contains("1", "42", "3"));
    }

    @Test
    public void shouldReplaceSingleElementAtEndOfJavaFXList() {
        // given:
        final ObservableList<Integer> remotingList = new ObservableArrayList<>();
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        FXBinder.bind(javaFXList).bidirectionalTo(remotingList, Object::toString, Integer::parseInt);
        javaFXList.addAll("1", "2", "3");

        // when:
        remotingList.set(2, 42);

        // then:
        assertThat(javaFXList, contains("1", "2", "42"));
    }
}
