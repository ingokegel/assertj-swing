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
package org.assertj.swing.core;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import static org.assertj.swing.util.Preconditions.checkNotNull;

/**
 * Identifies the "main" thread.
 * 
 * @author Alex Ruiz
 */
class MainThreadIdentifier {
  @Nullable
  Thread mainThreadIn(@NonNull Thread[] threads) {
    for (Thread t : threads) {
      if (isMain(checkNotNull(t))) {
        return t;
      }
    }
    return null;
  }

  private boolean isMain(@NonNull Thread thread) {
    return "main".equalsIgnoreCase(thread.getName());
  }
}
