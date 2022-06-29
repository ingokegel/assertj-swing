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
package org.assertj.swing.driver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.query.ComponentLocationOnScreenQuery.locationOnScreen;

import java.awt.Point;

import org.assertj.swing.test.awt.FluentPoint;
import org.assertj.swing.util.RobotFactory;
import org.junit.Test;

/**
 * Tests for {@link WindowDriver#moveTo(java.awt.Window, java.awt.Point)}.
 * 
 * @author Alex Ruiz
 */
public class WindowDriver_moveTo_Test extends WindowDriver_TestCase {
  @Test
  public void should_Move_Window() {
    showWindow();
    Point newPosition = new FluentPoint(RobotFactory.DEFAULT_WINDOW_LOCATION).addToX(100).addToY(100);
    driver.moveTo(window, newPosition);
    assertThat(locationOnScreen(window)).isEqualTo(newPosition);
  }

  @Test
  public void should_Throw_Error_If_Window_Is_Disabled() {
    disableWindow();
    thrown.expectIllegalStateIsDisabledComponent();
    driver.moveTo(window, new FluentPoint(RobotFactory.DEFAULT_WINDOW_LOCATION).addToX(100).addToY(100));
  }

  @Test
  public void should_Throw_Error_If_Window_Is_Not_Showing_On_The_Screen() {
    thrown.expectIllegalStateIsNotShowingComponent();
    driver.moveTo(window, new FluentPoint(RobotFactory.DEFAULT_WINDOW_LOCATION).addToX(100).addToY(100));
  }
}
