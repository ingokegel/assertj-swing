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

import static java.io.File.separator;
import static org.assertj.core.util.Strings.concat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.assertj.swing.image.ScreenshotTaker;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for <code>{@link FailureScreenshotTaker}</code>.
 *
 * @author Alex Ruiz
 */
public class FailureScreenshotTaker_saveScreenshot_Test {

  private ScreenshotTaker screenshotTaker;
  private File imageFolder;
  private FailureScreenshotTaker failureScreenshotTaker;

  @Before
  public void setUp() {
    screenshotTaker = mock(ScreenshotTaker.class);
    imageFolder = mock(File.class);
    failureScreenshotTaker = new FailureScreenshotTaker(imageFolder, screenshotTaker);
  }

  @Test
  public void should_Save_Screenshot_With_Given_Test_Name_At_Given_Folder() throws Exception {
    when(imageFolder.getCanonicalPath()).thenReturn("myPath");
    failureScreenshotTaker.saveScreenshot("testName");
    verify(screenshotTaker).saveDesktopAsPng(concat("myPath", separator, "testName.png"));
  }

  @Test
  public void should_Not_Rethrow_Exceptions() throws Exception {
    when(imageFolder.getCanonicalPath()).thenThrow(new IOException("Thrown on purpose"));
    failureScreenshotTaker.saveScreenshot("testName");
  }
}
