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

import org.junit.Test;

/**
 * Tests for {@link JTextComponentDriver#requireNotEditable(javax.swing.text.JTextComponent)}.
 * 
 * @author Alex Ruiz
 * @author Yvonne Wang
 */
public class JTextComponentDriver_requireNotEditable_Test extends JTextComponentDriver_TestCase {
  @Test
  public void should_Pass_If_JTextComponent_Is_Not_Editable() {
    makeTextFieldNotEditable();
    driver.requireNotEditable(textField);
  }

  @Test
  public void should_Fail_If_JTextComponent_Is_Editable() {
    makeTextFieldEditable();
    thrown.expectAssertionError("property:'editable'");
    thrown.expectMessageToContain("expected:<[fals]e> but was:<[tru]e>");
    driver.requireNotEditable(textField);
  }
}
