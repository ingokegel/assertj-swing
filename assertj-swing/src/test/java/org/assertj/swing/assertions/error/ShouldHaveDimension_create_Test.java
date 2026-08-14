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

import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static junit.framework.Assert.assertEquals;
import static org.assertj.swing.assertions.error.ShouldHaveDimension.shouldHaveDimension;

public class ShouldHaveDimension_create_Test {
  @Test
  public void should_Create_Error_Message() {
    BufferedImage actual = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    assertEquals("expected size:<10x10> but was:<10x10> in:<" + actual.toString() + ">",
                 shouldHaveDimension(actual, new Dimension(10, 10), new Dimension(10, 10)));
  }
}
