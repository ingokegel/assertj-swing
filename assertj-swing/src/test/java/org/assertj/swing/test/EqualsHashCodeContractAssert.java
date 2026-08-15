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
package org.assertj.swing.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Assertions for the {@code equals}/{@code hashCode} contract, inlining the contract asserts previously provided by
 * the removed {@code fest-test} test dependency.
 *
 * @author Alex Ruiz
 */
public final class EqualsHashCodeContractAssert {

  /**
   * Verifies that {@code equals} is reflexive: an object is equal to itself.
   *
   * @param o the object to verify.
   */
  public static void assertEqualsIsReflexive(Object o) {
    assertEquals(o, o);
  }

  /**
   * Verifies that {@code equals} is symmetric: if {@code a} equals {@code b}, then {@code b} equals {@code a}.
   *
   * @param a first object to verify.
   * @param b second object to verify.
   */
  public static void assertEqualsIsSymmetric(Object a, Object b) {
    assertEquals(a, b);
    assertEquals(b, a);
    assertEquals(a.hashCode(), b.hashCode());
  }

  /**
   * Verifies that {@code equals} is transitive: if {@code a} equals {@code b} and {@code b} equals {@code c}, then
   * {@code a} equals {@code c}.
   *
   * @param a first object to verify.
   * @param b second object to verify.
   * @param c third object to verify.
   */
  public static void assertEqualsIsTransitive(Object a, Object b, Object c) {
    assertEquals(a, b);
    assertEquals(b, c);
    assertEquals(a, c);
  }

  /**
   * Verifies that equal objects have equal hash codes.
   *
   * @param a first object to verify.
   * @param b second object to verify.
   */
  public static void assertMaintainsEqualsAndHashCodeContract(Object a, Object b) {
    assertEquals(a, b);
    assertTrue(a.hashCode() == b.hashCode());
  }

  private EqualsHashCodeContractAssert() {
  }
}
