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
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.assertj.swing.util.Objects.areEqual;

/**
 * Utility methods related to {@code String}s.
 * 
 * @author Alex Ruiz
 * @author Uli Schrempp
 */
public final class Strings {
  /**
   * Indicates whether the given {@code String} is the default {@code toString()} implementation of an {@code Object}.
   * 
   * @param s the given {@code String}.
   * @return {@code true} if the given {@code String} is the default {@code toString()} implementation, {@code false}
   *         otherwise.
   */
  public static boolean isDefaultToString(@Nullable String s) {
    if (s == null || s.isEmpty()) {
      return false;
    }
    int at = s.indexOf("@");
    if (at == -1) {
      return false;
    }
    String hash = s.substring(at + 1, s.length());
    try {
      Integer.parseInt(hash, 16);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  /**
   * Indicates if the given {@code String}s match. To match, one of the following conditions needs to be true:
   * <ul>
   * <li>both {@code String}s have to be equal</li>
   * <li>{@code s} matches the regular expression in {@code pattern}</li>
   * </ul>
   * 
   * @param pattern a {@code String} to match (it can be a regular expression).
   * @param s the {@code String} to verify.
   * @return {@code true} if the given {@code String}s match, {@code false} otherwise.
   */
  public static boolean areEqualOrMatch(@Nullable String pattern, @Nullable String s) {
    if (areEqual(pattern, s)) {
      return true;
    }
    if (pattern != null && s != null) {
      try {
        return s.matches(pattern);
      } catch (PatternSyntaxException invalidRegex) {
        return s.contains(pattern);
      }
    }
    return false;
  }

  /**
   * Indicates if the given {@code String} matches the given regular expression pattern.
   * 
   * @param p the given regular expression pattern.
   * @param s the {@code String} to evaluate.
   * @return {@code true} if the given {@code String} matches the given regular expression pattern, {@code false}
   *         otherwise. It also returns {@code false} if the given {@code String} is {@code null}.
   * @throws NullPointerException if the given regular expression pattern is {@code null}.
   */
  public static boolean match(@NonNull Pattern p, @Nullable String s) {
    return match(p, (CharSequence) s);
  }

  /**
   * Indicates if the given {@code CharSequence} matches the given regular expression pattern.
   * 
   * @param p the given regular expression pattern.
   * @param s the {@code CharSequence} to evaluate.
   * @return {@code true} if the given {@code CharSequence} matches the given regular expression pattern, {@code false}
   *         otherwise. It also returns {@code false} if the given {@code CharSequence} is {@code null}.
   * @throws NullPointerException if the given regular expression pattern is {@code null}.
   */
  public static boolean match(@NonNull Pattern p, @Nullable CharSequence s) {
    Preconditions.checkNotNull(p);
    if (s == null) {
      return false;
    }
    return p.matcher(s).matches();
  }

  /**
   * Indicates whether the given {@code String} is {@code null} or empty.
   *
   * @param s the {@code String} to check.
   * @return {@code true} if the given {@code String} is {@code null} or empty, otherwise {@code false}.
   */
  public static boolean isNullOrEmpty(String s) {
    return s == null || s.isEmpty();
  }

  /**
   * Concatenates the given objects into a single {@code String}.
   *
   * @param objects the objects to concatenate.
   * @return a {@code String} containing the given objects, or an empty {@code String} if the array of objects is
   *         {@code null}.
   */
  public static @Nullable String concat(Object... objects) {
    if (objects == null)
      return null;
    StringBuilder sb = new StringBuilder();
    for (Object o : objects)
      sb.append(o);
    return sb.toString();
  }

  /**
   * Returns the given object surrounded by single quotes, if it is a {@code String}. Any other object is returned
   * unchanged.
   *
   * @param o the given object.
   * @return the given object surrounded by single quotes if it is a {@code String}, the object itself otherwise.
   */
  public static @Nullable Object quote(@Nullable Object o) {
    return o instanceof String ? quote((String) o) : o;
  }

  /**
   * Returns the given {@code String} surrounded by single quotes.
   *
   * @param s the given {@code String}.
   * @return the given {@code String} surrounded by single quotes.
   */
  public static @Nullable String quote(@Nullable String s) {
    return s == null ? null : "'" + s + "'";
  }

  /**
   * Joins the given objects, to be separated by a delimiter.
   *
   * @param objects the objects to join.
   * @return an object that joins the given objects using the delimiter specified with {@code Join#with(String)}.
   */
  public static @Nullable Join join(Object... objects) {
    return objects == null ? null : new Join(objects);
  }

  /**
   * Joins formatted objects using a delimiter.
   */
  public static final class Join {
    private final Object[] objects;

    Join(Object... objects) {
      this.objects = objects;
    }

    /**
     * Specifies the delimiter to use to join the objects.
     *
     * @param delimiter the delimiter to use.
     * @return the {@code String} containing the joined objects.
     */
    public @Nullable String with(String delimiter) {
      if (delimiter == null)
        return null;
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < objects.length; i++) {
        sb.append(objects[i] != null ? objects[i].toString() : "null");
        if (i < objects.length - 1)
          sb.append(delimiter);
      }
      return sb.toString();
    }
  }

  private Strings() {
  }
}
