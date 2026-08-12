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

/**
 * Logging facade that uses {@code java.lang.System.Logger} on Java 9 and later and falls back to
 * {@code java.util.logging} on Java 8. On Java 9+, this avoids a hard dependency on the {@code java.logging} module.
 */
public abstract class Logger {

  private static final boolean SYSTEM_LOGGER_AVAILABLE = checkSystemLoggerAvailable();

  public static Logger getLogger(Class<?> type) {
    return getLogger(type.getName());
  }

  public static Logger getLogger(String name) {
    if (SYSTEM_LOGGER_AVAILABLE) {
      return SystemLoggerAdapter.create(name);
    }
    return new JavaUtilLoggingAdapter(name);
  }

  private static boolean checkSystemLoggerAvailable() {
    try {
      Class.forName("java.lang.System$Logger");
      return true;
    } catch (Throwable ignored) {
      return false;
    }
  }

  Logger() {
  }

  public abstract void finer(String message);

  public abstract void info(String message);

  public abstract void warning(String message, Throwable thrown);

  public abstract void severe(String message, Throwable thrown);
}
