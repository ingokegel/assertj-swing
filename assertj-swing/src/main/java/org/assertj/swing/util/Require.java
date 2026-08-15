/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2018 the original author or authors.
 */
package org.assertj.swing.util;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import static org.assertj.swing.util.Objects.areEqual;
import static org.assertj.swing.util.ToString.toStringOf;

/**
 * The assertions used by drivers and fixtures to verify the state of GUI components. This is a self-contained,
 * minimal replacement for a full-blown assertions library: it only supports the checks that assertj-swing performs
 * internally.
 *
 * @author Alex Ruiz
 */
public final class Require {

  /**
   * Creates a new assertion on the given value.
   *
   * @param actual the value to verify.
   * @return the created assertion.
   */
  public static @NonNull Requirement assertThat(@Nullable Object actual) {
    return new Requirement(actual);
  }

  /**
   * Creates a new assertion on the given value. Alias of {@link #assertThat(Object)}, used when the value is not an
   * assertion, but a verification.
   *
   * @param actual the value to verify.
   * @return the created assertion.
   */
  public static @NonNull Requirement verifyThat(@Nullable Object actual) {
    return assertThat(actual);
  }

  private Require() {
  }

  /**
   * A single verification of a value.
   */
  public static final class Requirement {

    private final Object actual;
    private Supplier<String> description;

    private Requirement(Object actual) {
      this.actual = actual;
    }

    /**
     * Sets the description of this verification, to be included in the error message if the verification fails.
     *
     * @param description the description.
     * @return this verification.
     */
    public @NonNull Requirement as(@NonNull String description) {
      return as(() -> description);
    }

    /**
     * Sets the lazily evaluated description of this verification, to be included in the error message if the
     * verification fails.
     *
     * @param description supplies the description.
     * @return this verification.
     */
    public @NonNull Requirement as(@NonNull Supplier<String> description) {
      this.description = description;
      return this;
    }

    /**
     * Sets the description of this verification, or no description if the given supplier is {@code null}.
     *
     * @param description supplies the description, may be {@code null}.
     * @return this verification.
     */
    public @NonNull Requirement describedAs(@Nullable Supplier<String> description) {
      this.description = description;
      return this;
    }

    /**
     * Verifies that the actual value is equal to the given one.
     *
     * @param expected the given value to compare the actual value to.
     * @throws AssertionError if the actual value is not equal to the given one.
     */
    public void isEqualTo(@Nullable Object expected) {
      if (areEqual(actual, expected))
        return;
      String actualText = toStringOf(actual);
      String expectedText = toStringOf(expected);
      if (expectedText.equals(actualText)) {
        throw failure(prefix() + "expected:<" + expectedText + "> but was:<" + actualText + ">");
      }
      String prefixText = commonPrefix(actualText, expectedText);
      String suffixText = commonSuffix(actualText, expectedText);
      String expectedDiff = compactPrefix(prefixText) + "[" + diff(expectedText, prefixText, suffixText) + "]"
          + compactSuffix(suffixText);
      String actualDiff = compactPrefix(prefixText) + "[" + diff(actualText, prefixText, suffixText) + "]"
          + compactSuffix(suffixText);
      throw failure(prefix() + "expected:<" + expectedDiff + "> but was:<" + actualDiff + ">");
    }

    private static final int CONTEXT_LENGTH = 20;

    private static String commonPrefix(String actual, String expected) {
      int length = Math.min(actual.length(), expected.length());
      int at = 0;
      while (at < length && actual.charAt(at) == expected.charAt(at))
        at++;
      return actual.substring(0, at);
    }

    private static String commonSuffix(String actual, String expected) {
      int max = Math.min(actual.length(), expected.length());
      int at = 0;
      while (at < max && actual.charAt(actual.length() - 1 - at) == expected.charAt(expected.length() - 1 - at))
        at++;
      return actual.substring(actual.length() - at);
    }

    private static String diff(String text, String prefix, String suffix) {
      return text.substring(prefix.length(), text.length() - suffix.length());
    }

    private static String compactPrefix(String prefix) {
      return prefix.length() <= CONTEXT_LENGTH ? prefix : "..." + prefix.substring(prefix.length() - CONTEXT_LENGTH);
    }

    private static String compactSuffix(String suffix) {
      return suffix.length() <= CONTEXT_LENGTH ? suffix : suffix.substring(0, CONTEXT_LENGTH) + "...";
    }

    /**
     * Verifies that the actual value is not equal to the given one.
     *
     * @param other the given value to compare the actual value to.
     * @throws AssertionError if the actual value is equal to the given one.
     */
    public void isNotEqualTo(@Nullable Object other) {
      if (!areEqual(actual, other))
        return;
      throw failure(prefix() + "expecting actual not to be equal to:<" + toStringOf(other) + ">");
    }

    /**
     * Verifies that the actual value is {@code true}.
     *
     * @throws AssertionError if the actual value is not {@code true}.
     */
    public void isTrue() {
      isEqualTo(Boolean.TRUE);
    }

    /**
     * Verifies that the actual value is {@code false}.
     *
     * @throws AssertionError if the actual value is not {@code false}.
     */
    public void isFalse() {
      isEqualTo(Boolean.FALSE);
    }

    /**
     * Verifies that the actual value, expected to be a {@code String}, is empty.
     *
     * @throws AssertionError if the actual value is not an empty {@code String}.
     */
    public void isEmpty() {
      String s = (String) actual;
      if (s == null || s.isEmpty())
        return;
      throw failure(prefix() + "\nExpecting empty but was: " + toStringOf(s));
    }

    /**
     * Verifies that the actual value is an instance of the given type.
     *
     * @param type the type to check the actual value against.
     * @throws AssertionError if the actual value is not an instance of the given type.
     */
    public void isInstanceOf(@NonNull Class<?> type) {
      if (type.isInstance(actual))
        return;
      throw failure(prefix() + "\nExpecting actual:\n  " + toStringOf(actual) + "\nto be an instance of:\n  "
          + type.getName() + "\nbut was instance of:\n  "
          + (actual == null ? "null" : actual.getClass().getName()));
    }

    /**
     * Verifies that the actual value, expected to be a {@code String}, matches the given regular expression.
     *
     * @param pattern the regular expression to match.
     * @throws AssertionError if the actual value does not match the given regular expression.
     */
    public void matches(@NonNull Pattern pattern) {
      if (Strings.match(pattern, (String) actual))
        return;
      throw failure(prefix() + "\nExpecting actual:\n  " + toStringOf(actual) + "\nto match pattern:\n  \""
          + pattern.pattern() + "\"");
    }

    /**
     * Verifies that the actual value, expected to be a {@code String}, is equal to the given one or matches it as a
     * regular expression.
     *
     * @param expected the value to compare the actual value to, which can be a regular expression.
     * @throws AssertionError if the actual value is not equal to the given one and does not match it as a regular
     *           expression.
     */
    public void isEqualOrMatches(@Nullable String expected) {
      if (Strings.areEqualOrMatch(expected, (String) actual))
        return;
      throw failure(prefix() + "\nExpecting actual:\n  " + toStringOf(actual) + "\nto match pattern:\n  \""
          + expected + "\"");
    }

    /**
     * Verifies that the actual value, expected to be an array, contains the given values, in any order.
     *
     * @param values the values the actual value is expected to contain.
     * @throws AssertionError if the actual value does not contain all the given values.
     */
    public void contains(Object... values) {
      for (Object value : values)
        if (!arrayContains(actual, value))
          throw failure(prefix() + "expecting actual:\n  " + toStringOf(actual) + "\nto contain:\n  "
              + toStringOf(values));
    }

    /**
     * Verifies that the actual value, expected to be an array, contains the given values, in any order.
     *
     * @param values the values the actual value is expected to contain.
     * @throws AssertionError if the actual value does not contain all the given values.
     */
    public void contains(int... values) {
      Integer[] boxed = new Integer[values.length];
      for (int i = 0; i < values.length; i++)
        boxed[i] = values[i];
      contains((Object[]) boxed);
    }

    private static boolean arrayContains(Object array, Object value) {
      if (array == null || !array.getClass().isArray())
        return false;
      int length = java.lang.reflect.Array.getLength(array);
      for (int i = 0; i < length; i++)
        if (areEqual(java.lang.reflect.Array.get(array, i), value))
          return true;
      return false;
    }

    private String prefix() {
      if (description == null)
        return "";
      String text = description.get();
      return "[" + text + "] ";
    }

    private static AssertionError failure(String message) {
      return new AssertionError(message);
    }
  }
}
