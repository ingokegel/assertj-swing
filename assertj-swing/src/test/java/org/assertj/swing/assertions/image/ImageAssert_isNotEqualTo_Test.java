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
package org.assertj.swing.assertions.image;

import org.assertj.swing.assertions.ImageAssert;
import org.assertj.swing.assertions.ImageAssertBaseTest;
import org.junit.Test;

import java.awt.image.BufferedImage;

import static org.assertj.swing.test.awt.AwtTestData.fivePixelBlueImage;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.verify;

public class ImageAssert_isNotEqualTo_Test extends ImageAssertBaseTest {

  private final BufferedImage expected = fivePixelBlueImage();

  @Test
  public void should_Delegate_To_Images() {
    ImageAssert result = assertions.isNotEqualTo(expected);
    assertSame(assertions, result);
    verify(images).assertNotEqual(actual, expected);
  }
}
