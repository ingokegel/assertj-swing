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

import org.assertj.swing.test.ExpectedException;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.swing.test.ExpectedException.none;
import static org.assertj.swing.util.Require.assertThat;

/**
 * Tests for {@link TextAssert#isEqualOrMatches(String)}.
 * 
 * @author Alex Ruiz
 */
public class TextAssert_isEqualOrMatches_Test {
  @Rule
  public ExpectedException thrown = none();

  @Test
  public void should_Fail_If_Actual_Is_Not_Equal_To_Expected() {
    thrown.expectAssertionError("Expecting actual:\n  \"hello\"\nto match pattern:\n  \"bye\"");
    assertThat("hello").isEqualOrMatches("bye");
  }

  @Test
  public void should_Fail_Showing_Description_If_Actual_Is_Not_Equal_To_Expected() {
    thrown.expectAssertionError("[A Test] \nExpecting actual:\n  \"hello\"\nto match pattern:\n  \"bye\"");
    assertThat("hello").as("A Test").isEqualOrMatches("bye");
  }

  @Test
  public void should_Pass_If_Actual_Is_Equal_To_Expected_But_No_Valid_Pattern() {
    assertThat("[He$$o").isEqualOrMatches("[He$$o");
  }

  @Test
  public void should_Pass_If_Actual_Is_Equal_To_Expected() {
    assertThat("Hello").isEqualOrMatches("Hello");
  }

  @Test
  public void should_Pass_If_Actual_Matches_Regex_Pattern() {
    assertThat("Hello").isEqualOrMatches("Hell.");
  }
}
