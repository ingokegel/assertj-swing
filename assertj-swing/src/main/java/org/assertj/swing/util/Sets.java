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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Utility methods related to {@code Set}s.
 *
 * @author Yvonne Wang
 * @author Alex Ruiz
 */
public final class Sets {

  /**
   * Creates a mutable {@code HashSet}.
   *
   * @param <T> the type of elements of the set to create.
   * @return the created {@code HashSet}.
   */
  public static <T> @Nonnull Set<T> newHashSet() {
    return new LinkedHashSet<>();
  }

  /**
   * Creates a mutable {@code HashSet} containing the given elements.
   *
   * @param <T> the type of elements of the set to create.
   * @param elements the elements to store in the created set.
   * @return the created {@code HashSet}.
   */
  @SafeVarargs
  public static <T> @Nonnull Set<T> newHashSet(T... elements) {
    Set<T> set = newHashSet();
    Collections.addAll(set, elements);
    return set;
  }

  /**
   * Creates a mutable {@code LinkedHashSet}.
   *
   * @param <T> the type of elements of the set to create.
   * @return the created {@code LinkedHashSet}.
   */
  public static <T> @Nonnull Set<T> newLinkedHashSet() {
    return new LinkedHashSet<>();
  }

  private Sets() {
  }
}
