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
package org.assertj.swing.lock;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * Tests for {@link ScreenLock#getOwner()}.
 *
 * @author Alex Ruiz
 */
public class ScreenLock_getOwner_Test {

  @Test
  public void should_Return_Null_If_Not_Acquired() {
    ScreenLock lock = new ScreenLock();
    assertThat(lock.getOwner()).isNull();
  }

  @Test
  public void should_Return_Owner_When_Acquired() {
    final ScreenLock lock = new ScreenLock();
    final Object owner = new Object();
    lock.acquire(owner);
    try {
      assertThat(lock.getOwner()).isSameAs(owner);
      assertThat(lock.acquiredBy(lock.getOwner())).isTrue();
    } finally {
      lock.release(owner);
      assertThat(lock.getOwner()).isNull();
    }
  }

  @Test
  public void should_Not_Block_If_Current_Owner_Tries_To_Acquire_Lock_Again() {
    final ScreenLock lock = new ScreenLock();
    final Object owner = new Object();
    lock.acquire(owner);
    lock.acquire(owner);
    try {
      assertThat(lock.getOwner()).isSameAs(owner);
      assertThat(lock.acquiredBy(owner)).isTrue();
    } finally {
      lock.release(owner);
      assertThat(lock.getOwner()).isNull();
    }
  }
}
