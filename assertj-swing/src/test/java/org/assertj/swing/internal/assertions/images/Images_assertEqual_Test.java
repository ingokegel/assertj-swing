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

import java.awt.image.BufferedImage;

import static java.awt.Color.BLUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.test.awt.AwtTestData.*;

public class Images_assertEqual_Test extends ImagesBaseTest {

  @Test
  public void should_Pass_If_Images_Are_Equal() {
    images.assertEqual(actual, newImage(5, 5, BLUE));
  }

  @Test
  public void should_Pass_If_Images_Are_Same() {
    images.assertEqual(actual, actual);
  }

  @Test
  public void should_Pass_If_Both_Images_Are_Null() {
    images.assertEqual(null, null);
  }

  @Test
  public void should_Fail_If_Actual_Is_Null_And_Expected_Is_Not() {
    assertFailureMessage("expecting images to be equal within offset:<0>",
                         () -> images.assertEqual(null, fivePixelBlueImage()));
  }

  @Test
  public void should_Fail_If_Expected_Is_Null_And_Actual_Is_Not() {
    assertFailureMessage("expecting images to be equal within offset:<0>", () -> images.assertEqual(actual, null));
  }

  @Test
  public void should_Fail_If_Images_Have_Different_Size() {
    BufferedImage expected = newImage(6, 6, BLUE);
    assertFailureMessage("expected size:<6x6> but was:<5x5>", () -> images.assertEqual(actual, expected));
  }

  @Test
  public void should_Fail_If_Images_Have_Same_Size_But_Different_Color() {
    BufferedImage expected = fivePixelYellowImage();
    assertFailureMessage("expected:<color[r=255, g=255, b=0]> but was:<color[r=0, g=0, b=255]> at:<[0, 0]> within offset:<0>",
                         () -> images.assertEqual(actual, expected));
  }

  static void assertFailureMessage(String expectedMessage, Runnable assertion) {
    try {
      assertion.run();
    } catch (AssertionError e) {
      assertThat(e.getMessage()).contains(expectedMessage);
      return;
    }
    throw new AssertionError("Expected an AssertionError with message: " + expectedMessage);
  }
}
