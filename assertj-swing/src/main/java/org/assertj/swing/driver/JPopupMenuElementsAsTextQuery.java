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

import org.assertj.swing.annotation.RunsInEDT;
import org.assertj.swing.edt.GuiQuery;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import javax.swing.*;
import java.awt.*;

import static org.assertj.swing.driver.MenuElementComponentQuery.componentIn;
import static org.assertj.swing.edt.GuiActionRunner.execute;
import static org.assertj.swing.util.Preconditions.checkNotNull;

/**
 * Returns the contents of a {@code JPopupMenu} as a {@code String} array. This query is executed in the event dispatch
 * thread (EDT).
 * 
 * @author Alex Ruiz
 */
final class JPopupMenuElementsAsTextQuery {
  @RunsInEDT
  static @NonNull String[] menuElementsAsText(final @NonNull JPopupMenu popupMenu) {
    String[] result = execute(new GuiQuery<String[]>() {
      @Override
      protected String[] executeInEDT() throws Throwable {
        MenuElement[] subElements = popupMenu.getSubElements();
        String[] result = new String[subElements.length];
        for (int i = 0; i < subElements.length; i++) {
          MenuElement subElement = checkNotNull(subElements[i]);
          result[i] = textOf(subElement);
        }
        return result;
      }
    });
    return checkNotNull(result);
  }

  @Nullable private static String textOf(@NonNull MenuElement e) {
    Component c = componentIn(e);
    if (c instanceof JMenuItem) {
      return ((JMenuItem) c).getText();
    }
    return "-";
  }

  private JPopupMenuElementsAsTextQuery() {
  }
}
