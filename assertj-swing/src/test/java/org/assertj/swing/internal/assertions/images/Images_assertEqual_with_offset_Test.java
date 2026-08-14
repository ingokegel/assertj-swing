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
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.awt.Color.BLUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.assertions.data.Offset.offset;
import static org.assertj.swing.test.awt.AwtTestData.*;

public class Images_assertEqual_with_offset_Test extends ImagesBaseTest {

  @Override
  @Before
  public void setUp() {
    super.setUp();
    offset = offset(5);
  }

  @Test
  public void should_Throw_Error_If_Offset_Is_Null() {
    try {
      images.assertEqual(actual, actual, null);
    } catch (NullPointerException e) {
      assertThat(e).hasMessage("The given offset should not be null");
      return;
    }
    throw new AssertionError("Expected a NullPointerException");
  }

  @Test
  public void should_Pass_If_Images_Are_Equal() {
    Color similarBlue = new Color(0, 0, 250);
    images.assertEqual(actual, newImage(5, 5, similarBlue), offset);
  }

  @Test
  public void should_Pass_If_Images_Are_Same() {
    images.assertEqual(actual, actual, offset);
  }

  @Test
  public void should_Pass_If_Both_Images_Are_Null() {
    images.assertEqual(null, null, offset);
  }

  @Test
  public void should_Fail_If_Actual_Is_Null_And_Expected_Is_Not() {
    Images_assertEqual_Test.assertFailureMessage("expecting images to be equal within offset:<5>",
                                                 () -> images.assertEqual(null, fivePixelBlueImage(), offset));
  }

  @Test
  public void should_Fail_If_Expected_Is_Null_And_Actual_Is_Not() {
    Images_assertEqual_Test.assertFailureMessage("expecting images to be equal within offset:<5>",
                                                 () -> images.assertEqual(actual, null, offset));
  }

  @Test
  public void should_Fail_If_Images_Have_Different_Size() {
    BufferedImage expected = newImage(6, 6, BLUE);
    Images_assertEqual_Test.assertFailureMessage("expected size:<6x6> but was:<5x5>",
                                                 () -> images.assertEqual(actual, expected, offset));
  }

  @Test
  public void should_Fail_If_Images_Have_Same_Size_But_Different_Color() {
    BufferedImage expected = fivePixelYellowImage();
    Images_assertEqual_Test.assertFailureMessage(
        "expected:<color[r=255, g=255, b=0]> but was:<color[r=0, g=0, b=255]> at:<[0, 0]> within offset:<5>",
        () -> images.assertEqual(actual, expected, offset));
  }
}
