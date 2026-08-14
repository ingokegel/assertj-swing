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
package org.assertj.swing.internal.assertions;

import org.assertj.swing.assertions.data.Offset;
import org.junit.Before;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.assertj.swing.assertions.data.Offset.offset;
import static org.assertj.swing.test.awt.AwtTestData.fivePixelBlueImage;

public class ImagesBaseTest {
  protected BufferedImage actual;
  protected Offset<Integer> offset;
  protected Images images;

  @Before
  public void setUp() {
    images = Images.instance();
    actual = fivePixelBlueImage();
    offset = offset(0);
  }

  protected Dimension sizeOf(BufferedImage actual) {
    return Images.sizeOf(actual);
  }
}
