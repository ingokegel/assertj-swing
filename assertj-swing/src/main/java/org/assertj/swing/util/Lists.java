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
import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods related to {@code List}s.
 *
 * @author Yvonne Wang
 * @author Alex Ruiz
 */
public final class Lists {

  /**
   * Creates a mutable {@code ArrayList}.
   *
   * @param <T> the type of elements of the list to create.
   * @return the created {@code ArrayList}.
   */
  public static <T> @NonNull List<T> newArrayList() {
    return new ArrayList<>();
  }

  /**
   * Creates a mutable {@code ArrayList} containing the given elements.
   *
   * @param <T> the type of elements of the list to create.
   * @param elements the elements to store in the created list.
   * @return the created {@code ArrayList}.
   */
  @SafeVarargs
  public static <T> @NonNull List<T> newArrayList(T... elements) {
    if (elements == null)
      return newArrayList();
    List<T> list = newArrayList();
    for (T e : elements)
      list.add(e);
    return list;
  }

  /**
   * Creates a mutable {@code ArrayList} containing the elements of the given iterable.
   *
   * @param <T> the type of elements of the list to create.
   * @param iterable the source of elements to store in the created list.
   * @return the created {@code ArrayList}.
   */
  public static <T> @NonNull List<T> newArrayList(@NonNull Iterable<? extends T> iterable) {
    List<T> list = newArrayList();
    for (T e : iterable)
      list.add(e);
    return list;
  }

  /**
   * Returns an empty, immutable list.
   *
   * @param <T> the type of elements of the list.
   * @return an empty list.
   */
  public static <T> @NonNull List<T> emptyList() {
    return java.util.Collections.emptyList();
  }

  private Lists() {
  }
}
