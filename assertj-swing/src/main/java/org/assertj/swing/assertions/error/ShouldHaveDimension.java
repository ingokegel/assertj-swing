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
package org.assertj.swing.assertions.error;

import java.awt.*;

/**
 * Creates an error message indicating that an assertion that expected the size of an image to be equal to a given
 * size failed.
 *
 * @author Yvonne Wang
 */
public final class ShouldHaveDimension {

  /**
   * Creates a new error message.
   *
   * @param actual the actual value in the failed assertion.
   * @param actualSize the size of {@code actual}.
   * @param expectedSize the expected size.
   * @return the created error message.
   */
  public static String shouldHaveDimension(Object actual, Dimension actualSize, Dimension expectedSize) {
    return String.format("expected size:<%sx%s> but was:<%sx%s> in:<%s>", expectedSize.width, expectedSize.height,
                         actualSize.width, actualSize.height, actual);
  }

  private ShouldHaveDimension() {
  }
}
