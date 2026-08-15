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

import org.jspecify.annotations.Nullable;
import java.lang.reflect.Array;

/**
 * Utility methods related to object equality and hash codes.
 *
 * @author Yvonne Wang
 * @author Alex Ruiz
 */
public final class Objects {

  /**
   * Prime number used in hash code calculation.
   */
  public static final int HASH_CODE_PRIME = 31;

  /**
   * Indicates whether the given objects are equal to each other. Array equality is element-wise, both for object and
   * primitive arrays.
   *
   * @param o1 the first object to compare.
   * @param o2 the second object to compare.
   * @return {@code true} if the given objects are equal to each other, {@code false} otherwise.
   */
  public static boolean areEqual(@Nullable Object o1, @Nullable Object o2) {
    if (o1 == null)
      return o2 == null;
    if (o1.equals(o2))
      return true;
    if (o1.getClass().isArray() && o2 != null && o2.getClass().isArray())
      return areEqualArrays(o1, o2);
    return false;
  }

  private static boolean areEqualArrays(Object o1, Object o2) {
    int length1 = Array.getLength(o1);
    int length2 = Array.getLength(o2);
    if (length1 != length2)
      return false;
    for (int i = 0; i < length1; i++)
      if (!areEqual(Array.get(o1, i), Array.get(o2, i)))
        return false;
    return true;
  }

  private Objects() {
  }
}
