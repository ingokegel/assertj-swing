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
package org.assertj.swing.assertions.data;

import org.jspecify.annotations.NonNull;

/**
 * A positive offset to tolerate differences when comparing values.
 *
 * @param <T> the type of the offset value.
 *
 * @author Yvonne Wang
 * @author Alex Ruiz
 */
public final class Offset<T extends Number> {

  /**
   * The value of the offset, never negative.
   */
  public final @NonNull T value;

  /**
   * Creates a new {@code Offset}.
   *
   * @param <T> the type of the offset value.
   * @param value the value of the offset.
   * @return the created {@code Offset}.
   * @throws NullPointerException if the given value is {@code null}.
   * @throws IllegalArgumentException if the given value is negative.
   */
  public static <T extends Number> @NonNull Offset<T> offset(@NonNull T value) {
    if (value.doubleValue() < 0d)
      throw new IllegalArgumentException("The offset value should not be negative");
    return new Offset<>(value);
  }

  /**
   * Creates a new {@code Offset} with the given int value.
   *
   * @param value the value of the offset.
   * @return the created {@code Offset}.
   * @throws IllegalArgumentException if the given value is negative.
   */
  public static @NonNull Offset<Integer> offset(int value) {
    return offset(Integer.valueOf(value));
  }

  private Offset(@NonNull T value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;
    Offset<?> other = (Offset<?>) obj;
    return value.equals(other.value);
  }

  @Override
  public int hashCode() {
    return 31 + value.hashCode();
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
