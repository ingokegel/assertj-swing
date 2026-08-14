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
package org.assertj.swing.internal.assertions.images;

import org.assertj.swing.internal.assertions.ImagesBaseTest;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.awt.Color.BLUE;
import static org.assertj.swing.test.awt.AwtTestData.newImage;

public class Images_assertHasSize_Test extends ImagesBaseTest {

  private final BufferedImage image = newImage(5, 5, BLUE);

  @Test
  public void should_Throw_Error_If_Size_Is_Null() {
    try {
      images.assertHasSize(image, null);
    } catch (NullPointerException e) {
      return;
    }
    throw new AssertionError("Expected a NullPointerException");
  }

  @Test
  public void should_Pass_If_Size_Is_Equal_To_Expected() {
    images.assertHasSize(image, new Dimension(5, 5));
  }

  @Test
  public void should_Fail_If_Actual_Is_Null() {
    Images_assertEqual_Test.assertFailureMessage("Expecting actual not to be null",
                                                 () -> images.assertHasSize(null, new Dimension(5, 5)));
  }

  @Test
  public void should_Fail_If_Size_Is_Not_Equal_To_Expected() {
    Images_assertEqual_Test.assertFailureMessage("expected size:<6x8> but was:<5x5>",
                                                 () -> images.assertHasSize(image, new Dimension(6, 8)));
  }
}
