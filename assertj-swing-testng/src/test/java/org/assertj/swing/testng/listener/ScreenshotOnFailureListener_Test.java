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
package org.assertj.swing.testng.listener;

import static java.io.File.separator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Files.temporaryFolderPath;
import static org.assertj.core.util.Strings.concat;
import static org.assertj.core.util.Strings.join;
import static org.assertj.swing.assertions.Assertions.assertThat;
import static org.assertj.swing.util.ImageReader.readImageFrom;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Toolkit;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.assertj.swing.annotation.GUITest;
import org.junit.Before;
import org.junit.Test;
import org.testng.IClass;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.internal.ConstructorOrMethod;

/**
 * Tests for <code>{@link ScreenshotOnFailureListener}</code>.
 *
 * @author Yvonne Wang
 */
public class ScreenshotOnFailureListener_Test {

  @GUITest
  public static class SomeGUITestClass {
    @GUITest
    public void someGUITestMethod() {
    }
  }

  private ITestContext testContext;
  private ITestResult testResult;
  private IClass testClass;
  private ITestNGMethod method;
  private ScreenshotOnFailureListener listener;

  @Before
  public void setUp() {
    testContext = mock(ITestContext.class);
    testResult = mock(ITestResult.class);
    testClass = mock(IClass.class);
    method = mock(ITestNGMethod.class);
    when(testResult.getTestClass()).thenReturn(testClass);
    when(testResult.getMethod()).thenReturn(method);
    when(testResult.getInstance()).thenReturn(new Object());
    when(testResult.id()).thenReturn("test-id");
    Reporter.setCurrentTestResult(testResult);
    Reporter.getOutput().clear();
    listener = new ScreenshotOnFailureListener();
  }

  @Test
  public void should_Get_Output_Folder_On_Start() {
    String outputFolder = temporaryFolderPath();
    when(testContext.getOutputDirectory()).thenReturn(outputFolder);
    listener.onStart(testContext);
    assertThat(listener.output()).isEqualTo(outputFolder);
  }

  @Test
  public void should_Take_Screenshot_On_Test_Failure() throws Exception {
    when(testContext.getOutputDirectory()).thenReturn(temporaryFolderPath());
    listener.onStart(testContext);

    Date now = new GregorianCalendar().getTime();
    String className = new SimpleDateFormat("yyMMdd").format(now);
    String methodName = new SimpleDateFormat("hhmmss").format(now);
    Method realMethod = SomeGUITestClass.class.getMethod("someGUITestMethod");
    when(testClass.getName()).thenReturn(className);
    when(testClass.getRealClass()).thenAnswer(invocation -> SomeGUITestClass.class);
    when(method.getMethodName()).thenReturn(methodName);
    ConstructorOrMethod com = mock(ConstructorOrMethod.class);
    when(method.getConstructorOrMethod()).thenReturn(com);
    when(com.getMethod()).thenReturn(realMethod);

    listener.onTestFailure(testResult);

    String imageFileName = join(className, methodName, "png").with(".");
    String screenshotPath = concat(testContext.getOutputDirectory(), separator, imageFileName);
    assertThat(readImageFrom(screenshotPath)).hasSize(Toolkit.getDefaultToolkit().getScreenSize());
    List<String> reporterOutput = Reporter.getOutput();
    assertThat(reporterOutput).hasSize(1);
    assertThat(reporterOutput.get(0)).isEqualTo(concat("<a href=\"", imageFileName, "\">Screenshot</a>"));
  }
}
