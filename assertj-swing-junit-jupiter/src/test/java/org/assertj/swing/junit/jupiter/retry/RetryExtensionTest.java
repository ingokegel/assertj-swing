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
/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.assertj.swing.junit.jupiter.retry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineTestKit;
import org.junit.platform.testkit.engine.Events;

class RetryExtensionTest {

    @Test
    void passes_when_first_attempt_succeeds() {
        Events testEvents = execute(AlwaysPassesTest.class);
        testEvents.assertStatistics(stats -> stats.started(1).succeeded(1).aborted(0).failed(0));
    }

    @Test
    void passes_when_a_later_attempt_succeeds() {
        FailsTwiceThenPassesTest.attempts.set(0);
        Events testEvents = execute(FailsTwiceThenPassesTest.class);
        testEvents.assertStatistics(stats -> stats.started(3).succeeded(1).aborted(2).failed(0));
        assertThat(FailsTwiceThenPassesTest.attempts.get()).isEqualTo(3);
    }

    @Test
    void fails_when_all_attempts_fail() {
        Events testEvents = execute(AlwaysFailsTest.class);
        testEvents.assertStatistics(stats -> stats.started(2).succeeded(0).aborted(1).failed(1));
    }

    @Test
    void runs_before_each_again_for_every_attempt() {
        SetUpCountingTest.attempts.set(0);
        SetUpCountingTest.setUps.set(0);
        Events testEvents = execute(SetUpCountingTest.class);
        testEvents.assertStatistics(stats -> stats.started(2).succeeded(1).aborted(1).failed(0));
        assertThat(SetUpCountingTest.setUps.get()).isEqualTo(2);
    }

    private static Events execute(Class<?> testClass) {
        return EngineTestKit.engine("junit-jupiter")
                .selectors(selectClass(testClass))
                .execute()
                .testEvents();
    }

    static class AlwaysPassesTest {
        @RetryOnFailure
        void test() {
        }
    }

    static class FailsTwiceThenPassesTest {
        static final AtomicInteger attempts = new AtomicInteger();

        @RetryOnFailure(maxAttempts = 5)
        void test() {
            if (attempts.incrementAndGet() < 3) {
                throw new AssertionError("failing on purpose");
            }
        }
    }

    static class AlwaysFailsTest {
        @RetryOnFailure(maxAttempts = 2)
        void test() {
            throw new AssertionError("failing on purpose");
        }
    }

    static class SetUpCountingTest {
        static final AtomicInteger attempts = new AtomicInteger();
        static final AtomicInteger setUps = new AtomicInteger();

        @org.junit.jupiter.api.BeforeEach
        void setUp() {
            setUps.incrementAndGet();
        }

        @RetryOnFailure(maxAttempts = 2)
        void test() {
            if (attempts.incrementAndGet() < 2) {
                throw new AssertionError("failing on purpose");
            }
        }
    }
}
