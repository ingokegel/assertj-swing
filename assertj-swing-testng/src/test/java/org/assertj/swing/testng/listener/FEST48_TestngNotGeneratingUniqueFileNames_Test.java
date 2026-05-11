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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Arrays.array;
import static org.assertj.swing.testng.listener.ScreenshotFileNameGenerator.screenshotFileNameFrom;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.testng.IClass;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

/**
 * Tests for issue <a href="https://kenai.com/jira/browse/FEST-48" target="_blank">FEST-48</a>.
 *
 * @author Alex Ruiz
 */
public class FEST48_TestngNotGeneratingUniqueFileNames_Test {

  private ITestResult testResult;
  private IClass testClass;
  private ITestNGMethod method;

  @Before
  public void setUp() {
    testResult = mock(ITestResult.class);
    testClass = mock(IClass.class);
    method = mock(ITestNGMethod.class);
    when(testResult.getTestClass()).thenReturn(testClass);
    when(testResult.getMethod()).thenReturn(method);
  }

  @Test
  public void should_Generate_Different_File_Names_Using_Parameter_Values() {
    when(method.getMethodName()).thenReturn("myMethod");
    when(testClass.getName()).thenReturn("MyClass");
    assertThat(screenshotFileNameFrom(testResult)).isEqualTo("MyClass.myMethod.png");
    when(testResult.getParameters()).thenReturn(array("one", "two"));
    assertThat(screenshotFileNameFrom(testResult)).isEqualTo("MyClass.myMethod.one.two.png");
  }

  @Test
  public void should_Generate_File_Names_Even_If_Parameter_Value_Is_Null() {
    when(method.getMethodName()).thenReturn("myMethod");
    when(testClass.getName()).thenReturn("MyClass");
    when(testResult.getParameters()).thenReturn(array("one", null));
    assertThat(screenshotFileNameFrom(testResult)).isEqualTo("MyClass.myMethod.one.[null].png");
  }
}
