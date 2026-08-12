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

import static java.lang.System.Logger.Level.DEBUG;
import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.INFO;
import static java.lang.System.Logger.Level.WARNING;

/**
 * Java 9+ version of {@code SystemLoggerAdapter} that uses {@code java.lang.System.Logger} directly. Packaged under
 * {@code META-INF/versions/9} of the multi-release jar, it shadows the reflection-based base version when the jar is
 * loaded on Java 9 or later.
 */
final class SystemLoggerAdapter extends Logger {

  private final System.Logger delegate;

  static Logger create(String name) {
    return new SystemLoggerAdapter(System.getLogger(name));
  }

  private SystemLoggerAdapter(System.Logger delegate) {
    this.delegate = delegate;
  }

  @Override
  public void finer(String message) {
    delegate.log(DEBUG, message);
  }

  @Override
  public void info(String message) {
    delegate.log(INFO, message);
  }

  @Override
  public void warning(String message, Throwable thrown) {
    delegate.log(WARNING, message, thrown);
  }

  @Override
  public void severe(String message, Throwable thrown) {
    delegate.log(ERROR, message, thrown);
  }
}
