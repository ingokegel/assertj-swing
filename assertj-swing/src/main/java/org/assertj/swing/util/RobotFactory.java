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
package org.assertj.swing.util;

import java.awt.*;

import org.jspecify.annotations.NonNull;

/**
 * Factory of AWT {@code Robot}s.
 * 
 * @author Alex Ruiz
 */
public class RobotFactory {

  private static GraphicsDevice getScreenDevice() {
    if (OVERRIDDEN_SCREEN_DEVICE != null) {
      return OVERRIDDEN_SCREEN_DEVICE;
    } else {
      return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    }
  }

  private static GraphicsDevice getOverriddenScreenDevice() {
    String testDisplay = System.getenv().get("TEST_DISPLAY");
    if (testDisplay != null) {
      for (GraphicsDevice screenDevice : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
        if (screenDevice.getIDstring().equals(testDisplay)) {
          return screenDevice;
        }
      }
    }
    return null;
  }

  public static final GraphicsDevice OVERRIDDEN_SCREEN_DEVICE = getOverriddenScreenDevice();
  public static final GraphicsDevice DEFAULT_SCREEN_DEVICE = getScreenDevice();
  public static final Point DEFAULT_WINDOW_LOCATION = getDefaultWindowLocation();

  private static Point getDefaultWindowLocation() {
    Rectangle screenBounds = DEFAULT_SCREEN_DEVICE.getDefaultConfiguration().getBounds();
    return new Point(screenBounds.x + 100, screenBounds.y + 100);
  }

  /**
   * Creates a new AWT {@code Robot} object in the coordinate system of the primary screen.
   * 
   * @return the created {@code Robot}.
   * @throws AWTException if the platform configuration does not allow low-level input control. This exception is always
   *           thrown when {@code GraphicsEnvironment.isHeadless()} returns {@code true}.
   * @throws SecurityException if {@code createRobot} permission is not granted.
   */
  @NonNull public Robot newRobotInPrimaryScreen() throws AWTException {
    return new Robot(DEFAULT_SCREEN_DEVICE);
  }

  /**
   * Creates a new AWT {@code Robot} object in the coordinate system of the left screen (in terms of coordinates).
   * 
   * @return the created {@code Robot}.
   * @throws AWTException if the platform configuration does not allow low-level input control. This exception is always
   *           thrown when {@code GraphicsEnvironment.isHeadless()} returns {@code true}.
   * @throws SecurityException if {@code createRobot} permission is not granted.
   */
  @NonNull public
  Robot newRobotInLeftScreen() throws AWTException {
    int lowestX = Integer.MAX_VALUE;
    GraphicsDevice lowestScreen = null;
    if (OVERRIDDEN_SCREEN_DEVICE != null) {
      lowestScreen = OVERRIDDEN_SCREEN_DEVICE;
    } else {
      for (GraphicsDevice screen : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
        if (screen.getDefaultConfiguration().getBounds().x < lowestX) {
          lowestX = screen.getDefaultConfiguration().getBounds().x;
          lowestScreen = screen;
        }
      }
    }
    return new Robot(lowestScreen);
  }
}
