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
package org.assertj.swing.junit.ant;

import static java.awt.image.BufferedImage.TYPE_BYTE_BINARY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.junit.ant.ImageHandler.encodeBase64;
import static org.assertj.swing.junit.ant.Tests.testClassNameFrom;
import static org.assertj.swing.junit.ant.Tests.testMethodNameFrom;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.image.BufferedImage;

import junit.framework.TestResult;

import org.assertj.swing.image.ScreenshotTaker;
import org.assertj.swing.junit.xml.XmlDocument;
import org.assertj.swing.junit.xml.XmlNode;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for <code>{@link ScreenshotXmlWriter}</code>.
 *
 * @author Alex Ruiz
 */
public class ScreenshotXmlWriter_writeScreenshot_Test {

  private XmlNode root;
  private XmlNode errorNode;
  private ScreenshotTaker screenshotTaker;
  private GUITestRecognizer guiTestRecognizer;
  private MyTest test;
  private ScreenshotXmlWriter writer;

  @Before
  public void setUp() {
    XmlDocument document = new XmlDocument();
    root = document.newRoot("root");
    errorNode = root.addNewNode("error");
    screenshotTaker = mock(ScreenshotTaker.class);
    guiTestRecognizer = mock(GUITestRecognizer.class);
    test = new MyTest();
    writer = new ScreenshotXmlWriter(screenshotTaker, guiTestRecognizer);
  }

  @Test
  public void should_Add_Screenshot_Element_Test_Is_GUI_Test() {
    final BufferedImage image = new BufferedImage(10, 10, TYPE_BYTE_BINARY);
    when(guiTestRecognizer.isGUITest(testClassNameFrom(test), testMethodNameFrom(test))).thenReturn(true);
    when(screenshotTaker.takeDesktopScreenshot()).thenReturn(image);
    writer.writeScreenshot(errorNode, test);
    assertThat(root.size()).isEqualTo(2);
    assertThat(root.child(0)).isEqualTo(errorNode);
    XmlNode secondChild = root.child(1);
    assertThat(secondChild.name()).isEqualTo("screenshot");
    assertThat(secondChild.text()).isEqualTo(encodeBase64(image));
  }

  @Test
  public void should_Not_Add_Screenshot_Element_Test_Is_Not_GUI_Test() {
    when(guiTestRecognizer.isGUITest(testClassNameFrom(test), testMethodNameFrom(test))).thenReturn(false);
    writer.writeScreenshot(errorNode, test);
    assertThat(root.size()).isEqualTo(1);
    assertThat(root.child(0)).isEqualTo(errorNode);
  }

  private static class MyTest implements junit.framework.Test {
    @Override
    public int countTestCases() {
      return 0;
    }

    @Override
    public void run(TestResult result) {
    }
  }
}
