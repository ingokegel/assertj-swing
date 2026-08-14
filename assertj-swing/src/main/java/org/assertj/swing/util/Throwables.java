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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility methods related to {@code Throwable}s.
 *
 * @author Alex Ruiz
 */
public final class Throwables {

  /**
   * Appends the stack trace of the current thread to the given throwable, starting after the frame with the given
   * method name. This is useful to see where a throwable that was created in another thread (e.g. the event dispatch
   * thread) corresponds to in the current thread.
   *
   * @param throwable the given throwable.
   * @param methodName the name of the method in the current thread's stack trace after which appending starts.
   */
  public static void appendStackTraceInCurrentThreadToThrowable(@Nonnull Throwable throwable, @Nonnull String methodName) {
    List<StackTraceElement> stackTrace = new ArrayList<>();
    Collections.addAll(stackTrace, throwable.getStackTrace());
    stackTrace.addAll(stackTraceInCurrentThread(methodName));
    throwable.setStackTrace(stackTrace.toArray(new StackTraceElement[0]));
  }

  private static List<StackTraceElement> stackTraceInCurrentThread(String methodName) {
    StackTraceElement[] elements = Thread.currentThread().getStackTrace();
    List<StackTraceElement> toAppend = new ArrayList<>();
    boolean methodFound = false;
    for (StackTraceElement e : elements) {
      if (!methodFound) {
        if (methodName.equals(e.getMethodName()))
          methodFound = true;
        continue;
      }
      toAppend.add(e);
    }
    return toAppend;
  }

  private Throwables() {
  }
}
