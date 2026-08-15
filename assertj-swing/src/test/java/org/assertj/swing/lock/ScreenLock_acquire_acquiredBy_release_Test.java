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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

/**
 * Tests for {@link ScreenLock#acquire(Object)} blocking other owners until the lock is released.
 *
 * @author Alex Ruiz
 */
public class ScreenLock_acquire_acquiredBy_release_Test {

  @Test
  public void should_Acquire_Lock_And_Queue_Others_Wanting_Lock() throws Exception {
    final ScreenLock lock = new ScreenLock();
    final Object owner1 = new LockOwner("Owner #1");
    final Object owner2 = new LockOwner("Owner #2");

    final CountDownLatch acquiredByOwner1 = new CountDownLatch(1);
    final CountDownLatch owner2Done = new CountDownLatch(1);
    final AtomicReference<Throwable> failureInThread2 = new AtomicReference<>();

    Thread thread1 = new Thread(() -> {
      lock.acquire(owner1);
      acquiredByOwner1.countDown();
    });
    Thread thread2 = new Thread(() -> {
      try {
        lock.acquire(owner2);
      } catch (Throwable t) {
        failureInThread2.set(t);
      } finally {
        owner2Done.countDown();
      }
    });

    thread1.start();
    assertThat(acquiredByOwner1.await(10, TimeUnit.SECONDS)).isTrue();

    // while the lock is held by owner1, a second acquisition blocks
    thread2.start();
    assertThat(lock.acquiredBy(owner1)).isTrue();
    assertThat(owner2Done.await(200, TimeUnit.MILLISECONDS)).isFalse();

    // releasing lets the queued owner2 acquire the lock
    lock.release(owner1);
    assertThat(owner2Done.await(10, TimeUnit.SECONDS)).isTrue();

    assertThat(lock.acquiredBy(owner2)).isTrue();
    lock.release(owner2);
    assertThat(lock.acquired()).isFalse();
    assertThat(failureInThread2.get()).isNull();
  }

  private static class LockOwner {
    private final String name;

    LockOwner(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return name;
    }
  }
}
