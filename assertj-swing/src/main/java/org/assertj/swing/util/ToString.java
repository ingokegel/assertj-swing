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
import java.lang.reflect.Array;

/**
 * Formats values for display in error messages.
 *
 * @author Alex Ruiz
 */
public final class ToString {

  /**
   * Returns a readable text representation of the given object: {@code null} is rendered as {@code null}, strings are
   * double-quoted and arrays are rendered as {@code [e1, e2, ...]} with formatted elements.
   *
   * @param o the object to format.
   * @return a text representation of the given object.
   */
  public static @Nonnull String toStringOf(@Nullable Object o) {
    if (o == null)
      return "null";
    if (o instanceof String)
      return "\"" + o + "\"";
    if (o.getClass().isArray())
      return formatArray(o);
    if (o instanceof Iterable)
      return formatIterable((Iterable<?>) o);
    return o.toString();
  }

  private static String formatIterable(Iterable<?> iterable) {
    StringBuilder sb = new StringBuilder("[");
    boolean first = true;
    for (Object e : iterable) {
      if (!first)
        sb.append(", ");
      first = false;
      sb.append(toStringOf(e));
    }
    return sb.append("]").toString();
  }

  private static String formatArray(Object array) {
    int length = Array.getLength(array);
    StringBuilder sb = new StringBuilder("[");
    for (int i = 0; i < length; i++) {
      sb.append(toStringOf(Array.get(array, i)));
      if (i < length - 1)
        sb.append(", ");
    }
    return sb.append("]").toString();
  }

  private ToString() {
  }
}
