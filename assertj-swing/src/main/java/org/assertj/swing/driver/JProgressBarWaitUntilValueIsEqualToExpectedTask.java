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
package org.assertj.swing.driver;

import org.assertj.swing.annotation.RunsInEDT;
import org.assertj.swing.edt.GuiLazyLoadingDescription;
import org.assertj.swing.timing.Condition;
import org.assertj.swing.timing.Timeout;

import org.jspecify.annotations.NonNull;
import javax.swing.*;
import java.util.function.Supplier;

import static org.assertj.swing.driver.JProgressBarValueQuery.valueOf;
import static org.assertj.swing.format.Formatting.format;
import static org.assertj.swing.timing.Pause.pause;

/**
 * Waits until the value of a {@code JProgressBar} is equal to the given expected value. This task is executed in the
 * event dispatch thread (EDT).
 * 
 * @author Alex Ruiz
 */
final class JProgressBarWaitUntilValueIsEqualToExpectedTask {
  @RunsInEDT
  static void waitUntilValueIsEqualToExpected(final @NonNull JProgressBar progressBar, final int expected,
      final @NonNull Timeout timeout) {
    pause(new Condition(untilValueIsEqualTo(progressBar, expected)) {
      @Override
      public boolean test() {
        return valueOf(progressBar) == expected;
      }
    }, timeout);
  }

  private static Supplier<String> untilValueIsEqualTo(final @NonNull JProgressBar progressBar, final int expected) {
    return new GuiLazyLoadingDescription() {
      @Override
      @NonNull protected String loadDescription() {
        return String.format("value of %s to be equal to %d", format(progressBar), expected);
      }
    };
  }

  private JProgressBarWaitUntilValueIsEqualToExpectedTask() {
  }
}
