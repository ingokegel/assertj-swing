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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Verifies values satisfied some given conditions, similar to {@code com.google.common.base.Preconditions}.
 *
 * @author Yvonne Wang
 */
public final class Preconditions {

  /**
   * Verifies that the given {@code String} is not {@code null} or empty.
   *
   * @param s the {@code String} to check.
   * @return the given {@code String} if it is not {@code null} or not empty.
   * @throws NullPointerException if the given {@code String} is {@code null}.
   * @throws IllegalArgumentException if the given {@code String} is empty.
   */
  public static @Nonnull String checkNotNullOrEmpty(@Nullable String s) {
    checkNotNull(s);
    if (s.isEmpty())
      throw new IllegalArgumentException("The given String should not be empty");
    return s;
  }

  /**
   * Verifies that the given array is not {@code null} or empty.
   *
   * @param <T> the type of elements of the array.
   * @param array the array to check.
   * @return the given array if it is not {@code null} or not empty.
   * @throws NullPointerException if the given array is {@code null}.
   * @throws IllegalArgumentException if the given array is empty.
   */
  public static <T> T[] checkNotNullOrEmpty(T[] array) {
    checkNotNull(array);
    if (array.length == 0)
      throw new IllegalArgumentException("The given array should not be empty");
    return array;
  }

  /**
   * Verifies that the given object reference is not {@code null}.
   *
   * @param <T> the type of the given object.
   * @param reference the object to check.
   * @return the given object if it is not {@code null}.
   * @throws NullPointerException if the given object is {@code null}.
   */
  public static <T> @Nonnull T checkNotNull(@Nullable T reference) {
    if (reference == null)
      throw new NullPointerException();
    return reference;
  }

  private Preconditions() {
  }
}
