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
package org.assertj.swing.assertions;

import org.assertj.swing.internal.assertions.Images;
import org.junit.Before;

import java.awt.image.BufferedImage;

import static org.assertj.swing.test.awt.AwtTestData.fivePixelBlueImage;
import static org.mockito.Mockito.mock;

public abstract class ImageAssertBaseTest {
  protected final BufferedImage actual = fivePixelBlueImage();
  protected Images images;
  protected ImageAssert assertions;

  @Before
  public void setUp() {
    images = mock(Images.class);
    assertions = new ImageAssert(actual);
    assertions.images = images;
  }
}
