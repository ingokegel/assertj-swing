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
package org.assertj.swing.edt;

import org.assertj.swing.annotation.RunsInCurrentThread;

import org.jspecify.annotations.NonNull;
import java.util.function.Supplier;

import static org.assertj.swing.edt.GuiActionRunner.execute;
import static org.assertj.swing.util.Preconditions.checkNotNull;

/**
 * Supplier of text that is loaded lazily, in the event dispatch thread (EDT). Useful for descriptions that must read
 * state of GUI components, which is only allowed on the EDT.
 *
 * @author Alex Ruiz
 * @author Yvonne Wang
 */
public abstract class GuiLazyLoadingDescription implements Supplier<String> {
  /**
   * Executes {@link #loadDescription()} in the event dispatch thread (EDT).
   *
   * @return the text loaded in the event dispatch thread (EDT).
   */
  @Override
  public final @NonNull String get() {
    String result = execute(() -> loadDescription());
    return checkNotNull(result);
  }

  /**
   * <p>
   * Returns the lazily-loaded text.
   * </p>
   *
   * <p>
   * <b>Note:</b> This method is accessed in the current executing thread. Such thread may or may not be the event
   * dispatch thread (EDT). Client code must call this method from the EDT.
   * </p>
   *
   * @return the lazily-loaded text.
   */
  @RunsInCurrentThread
  protected abstract @NonNull String loadDescription();
}
