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

/**
 * Fails a test with the given message.
 *
 * @author Alex Ruiz
 */
public final class Fail {

  /**
   * Throws an {@code AssertionError} with the given message.
   *
   * @param format the message format, as in {@code String#format}.
   * @param args the arguments referenced by the format specifiers in the message format.
   * @return never returns, the declared return type only allows {@code throw Fail.fail(...)}.
   * @throws AssertionError always.
   */
  public static @Nonnull AssertionError fail(@Nonnull String format, Object... args) {
    throw new AssertionError(String.format(format, args));
  }

  private Fail() {
  }
}
