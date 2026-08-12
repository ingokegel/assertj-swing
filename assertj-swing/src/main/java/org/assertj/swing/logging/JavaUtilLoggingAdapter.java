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
package org.assertj.swing.logging;

import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;

/**
 * {@link Logger} implementation backed by {@code java.util.logging}, used on Java 8.
 */
final class JavaUtilLoggingAdapter extends Logger {

  private final java.util.logging.Logger delegate;

  JavaUtilLoggingAdapter(String name) {
    delegate = java.util.logging.Logger.getLogger(name);
  }

  @Override
  public void finer(String message) {
    delegate.finer(message);
  }

  @Override
  public void info(String message) {
    delegate.info(message);
  }

  @Override
  public void warning(String message, Throwable thrown) {
    delegate.log(WARNING, message, thrown);
  }

  @Override
  public void severe(String message, Throwable thrown) {
    delegate.log(SEVERE, message, thrown);
  }
}
