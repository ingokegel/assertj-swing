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

import org.jspecify.annotations.NonNull;
import java.awt.*;
import java.util.function.Supplier;

import static org.assertj.swing.format.Formatting.format;
import static org.assertj.swing.query.ComponentEnabledQuery.isEnabled;
import static org.assertj.swing.util.Preconditions.checkNotNull;
import static org.assertj.swing.util.Strings.concat;

/**
 * Verifies that an AWT or Swing {@code Component} is enabled.
 * 
 * @author Yvonne Wang
 */
class ComponentEnabledCondition extends Condition {
  private Component c;

  static @NonNull ComponentEnabledCondition untilIsEnabled(@NonNull Component c) {
    return new ComponentEnabledCondition(c);
  }

  private ComponentEnabledCondition(@NonNull Component c) {
    super(description(c));
    this.c = c;
  }

  @NonNull private static Supplier<String> description(final @NonNull Component c) {
    return new GuiLazyLoadingDescription() {
      @Override
      @NonNull protected String loadDescription() {
        return concat(format(c), " to be enabled");
      }
    };
  }

  @RunsInEDT
  @Override
  public boolean test() {
    return isEnabled(checkNotNull(c));
  }

  @Override
  protected void done() {
    c = null;
  }
}
