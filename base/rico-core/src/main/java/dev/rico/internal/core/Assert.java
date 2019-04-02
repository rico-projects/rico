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
package dev.rico.internal.core;

import org.apiguardian.api.API;

import java.util.Arrays;
import java.util.List;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * A collection of utility methods that can assert the state of an instance.
 */
@API(since = "0.x", status = INTERNAL)
public final class Assert {
    private static final String NOT_NULL_MSG_FORMAT = "Argument '%s' may not be null";
    private static final String NOT_EMPTY_MSG_FORMAT = "Argument '%s' may not be empty";
    private static final String NOT_NULL_ENTRIES_MSG_FORMAT = "Argument '%s' may not contain null values";

    private Assert() {
        // intentionally private and blank
    }

    /**
     * Checks that the specified {@code value} is null and throws {@link java.lang.NullPointerException} with a customized error message if it is.
     * @param value the value to be checked.
     * @param argumentName the name of the argument to be used in the error message.
     * @param <T> type of the value
     * @return the {@code value}.
     */
    public static <T> T requireNonNull(final T value, final String argumentName) {
        if (argumentName == null) {
            throw new NullPointerException(String.format(NOT_NULL_MSG_FORMAT, "argumentName"));
        }
        if (value == null) {
            throw new NullPointerException(String.format(NOT_NULL_MSG_FORMAT, argumentName));
        }
        return value;
    }

    /**
     * Checks that the specified {@code str} {@code blank}, throws {@link IllegalArgumentException} with a customized error message if it is.
     *
     * @param str          the value to be checked.
     * @param argumentName the name of the argument to be used in the error message.
     * @return the {@code str}.
     * @throws java.lang.NullPointerException     if {@code str} is null.
     * @throws java.lang.IllegalArgumentException if {@code str} is blank.
     * @see #requireNonNull(Object, String)
     * @see #isBlank(String)
     */

    public static String requireNonBlank(final String str, final String argumentName) {
        requireNonNull(str, argumentName);
        if (isBlank(str)) {
            throw new IllegalArgumentException(String.format(NOT_EMPTY_MSG_FORMAT, argumentName));
        }
        return str;
    }

    /**
     * <p>Determines whether a given string is <code>null</code>, empty,
     * or only contains whitespace. If it contains anything other than
     * whitespace then the string is not considered to be blank and the
     * method returns <code>false</code>.</p>
     *
     * @param str The string to test.
     * @return <code>true</code> if the string is <code>null</code>, or
     * blank.
     */
    public static boolean isBlank(final String str) {
        if (str == null || str.length() == 0) {
            return true;
        }
        for (final char c : str.toCharArray()) {
            if (!Character.isWhitespace(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether the specified {@code collection} contains null values, throws {@link IllegalStateException} with a customized error message if it has.
     *
     * @param collection the collection to be checked.
     * @param argumentName the name of the argument to be used in the error message.
     * @param <T> content type of the list
     * @param <L> type of the list
     * @return the {@code collection}.
     */
    public static <T, L extends List<T>> L requireNonNullEntries(final L collection, final String argumentName) {
        requireNonNull(collection, argumentName);
        for (final Object value : collection) {
            requireState(value != null, NOT_NULL_ENTRIES_MSG_FORMAT, argumentName);
        }
        return collection;
    }

    public static <T> T[] requireNonNullEntries(final T[] array, final String argumentName) {
        requireNonNull(array, "array");
        requireNonNullEntries(Arrays.asList(array), argumentName);
        return array;
    }

    public static <T> T[] requireNotEmpty(final T[] array, final String argumentName) {
        requireNonNull(array, "array");
        if(array.length == 0) {
            throw new IllegalArgumentException(String.format(NOT_EMPTY_MSG_FORMAT, array));
        }
        return array;
    }

    /**
     * Checks that the specified condition is met and throws a customized
     * {@link IllegalStateException} if it is.
     *
     * @param condition the condition to check
     * @param unformatedMessage the unformated message of the possible {@code IllegalStateException}.
     * {@code String.format(unformatedMessage, messageArgs)} will be called to create the message of the exception.
     * @param messageArgs the arguments of the message of the possible {@code IllegalStateException}.
     * {@code String.format(unformatedMessage, messageArgs)} will be called to create the message of the exception.
     * @throws IllegalStateException if {@code condition} evaluates to false
     */
    public static void requireState(final boolean condition, final String unformatedMessage, final Object... messageArgs) {
        if (!condition) {
            throw new IllegalStateException(requireNonBlank(String.format(unformatedMessage, messageArgs), "message"));
        }
    }

}