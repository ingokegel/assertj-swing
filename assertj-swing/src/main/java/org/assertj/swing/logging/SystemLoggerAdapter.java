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

import java.lang.reflect.Method;

/**
 * {@link Logger} implementation backed by {@code java.lang.System.Logger}, used on Java 9 and later. All references
 * to {@code System.Logger} are reflective so that this class can be compiled for Java 8 and never touches the
 * {@code java.logging} module on Java 9+.
 * <p>
 * The jar also contains a Java 9 version of this class under {@code META-INF/versions/9} that uses
 * {@code System.Logger} directly; it shadows this class when the jar is loaded on Java 9+. This version remains as a
 * fallback for environments where multi-release jar versioning is not in effect, e.g. when running from an exploded
 * classes directory in tests or IDEs.
 */
final class SystemLoggerAdapter extends Logger {

  private static final Class<?> LEVEL = classForName("java.lang.System$Logger$Level");
  private static final Method GET_LOGGER = getMethod(System.class, "getLogger", String.class);
  private static final Method LOG = getMethod(classForName("java.lang.System$Logger"), "log", LEVEL, String.class);
  private static final Method LOG_THROWABLE = getMethod(classForName("java.lang.System$Logger"), "log", LEVEL,
                                                        String.class, Throwable.class);

  private static final Object DEBUG = level("DEBUG");
  private static final Object INFO = level("INFO");
  private static final Object WARNING = level("WARNING");
  private static final Object ERROR = level("ERROR");

  private final Object delegate;

  static Logger create(String name) {
    try {
      return new SystemLoggerAdapter(GET_LOGGER.invoke(null, name));
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException("Could not obtain System.Logger for " + name, e);
    }
  }

  private SystemLoggerAdapter(Object delegate) {
    this.delegate = delegate;
  }

  @Override
  public void finer(String message) {
    log(DEBUG, message, null);
  }

  @Override
  public void info(String message) {
    log(INFO, message, null);
  }

  @Override
  public void warning(String message, Throwable thrown) {
    log(WARNING, message, thrown);
  }

  @Override
  public void severe(String message, Throwable thrown) {
    log(ERROR, message, thrown);
  }

  private void log(Object level, String message, Throwable thrown) {
    try {
      if (thrown == null) {
        LOG.invoke(delegate, level, message);
      } else {
        LOG_THROWABLE.invoke(delegate, level, message, thrown);
      }
    } catch (Exception ignored) {
      // logging should never break the library
    }
  }

  private static Class<?> classForName(String name) {
    try {
      return Class.forName(name);
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException(name + " is not available", e);
    }
  }

  private static Method getMethod(Class<?> type, String name, Class<?>... parameterTypes) {
    try {
      return type.getMethod(name, parameterTypes);
    } catch (NoSuchMethodException e) {
      throw new IllegalStateException(e);
    }
  }

  private static Object level(String name) {
    try {
      return LEVEL.getField(name).get(null);
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException(e);
    }
  }
}
