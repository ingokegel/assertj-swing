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
package org.assertj.swing.junit.runner;

import org.assertj.swing.image.NoopScreenshotTaker;
import org.assertj.swing.image.ScreenshotTaker;
import org.assertj.swing.image.ScreenshotTakerIF;
import org.assertj.swing.logging.Logger;

import java.awt.*;
import java.io.File;

import static java.io.File.separator;
import static org.assertj.swing.image.ImageFileExtensions.PNG;
import static org.assertj.swing.util.Strings.concat;
import static org.assertj.swing.util.Strings.quote;

/**
 * Understands taking a screenshot of the desktop when a GUI test fails.
 *
 * @author Alex Ruiz
 */
public class FailureScreenshotTaker {

  private static Logger logger = Logger.getLogger(FailureScreenshotTaker.class);

  private final File imageFolder;
  private final ScreenshotTakerIF screenshotTaker;

  /**
   * Creates a new <code>{@link FailureScreenshotTaker}</code>.
   *
   * @param imageFolder the folder where screenshots will be saved to.
   */
  public FailureScreenshotTaker(File imageFolder) {
    this(imageFolder, GraphicsEnvironment.isHeadless() ? new NoopScreenshotTaker() : new ScreenshotTaker());
  }

  FailureScreenshotTaker(File imageFolder, ScreenshotTakerIF screenshotTaker) {
    this.imageFolder = imageFolder;
    this.screenshotTaker = screenshotTaker;
  }

  /**
   * Saves a screenshot of the desktop using the given description as the file name.
   *
   * @param failedTest the description of the test failure.
   */
  public void saveScreenshot(String failedTest) {
    try {
      String fileName = concat(imageFolder.getCanonicalPath(), separator, failedTest, ".", PNG);
      screenshotTaker.saveDesktopAsPng(fileName);
      logger.info(concat("Screenshot of failed test saved as ", quote(fileName)));
    } catch (Exception e) {
      logger.warning(concat("Unable to take screenshot of failed test ", quote(failedTest)), e);
    }
  }
}
