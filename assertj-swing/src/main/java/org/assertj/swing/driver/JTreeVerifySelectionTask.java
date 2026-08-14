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

import org.assertj.swing.annotation.RunsInCurrentThread;
import org.assertj.swing.annotation.RunsInEDT;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.tree.TreePath;
import java.util.Arrays;
import java.util.function.Supplier;

import static java.util.Arrays.sort;
import static org.assertj.swing.driver.JTreeMatchingPathQuery.matchingPathWithRootIfInvisible;
import static org.assertj.swing.edt.GuiActionRunner.execute;
import static org.assertj.swing.util.Arrays.format;
import static org.assertj.swing.util.Fail.fail;
import static org.assertj.swing.util.Objects.areEqual;
import static org.assertj.swing.util.Preconditions.checkNotNull;

/**
 * Verifies that a {@code JTree} has the expected selection.
 *
 * @author Alex Ruiz
 */
final class JTreeVerifySelectionTask {
  @RunsInEDT
  static void checkHasSelection(final @Nonnull JTree tree, final @Nonnull int[] selection,
                                final @Nonnull Supplier<String> errMsg) {
    execute(() -> checkSelection(tree, selection, errMsg));
  }

  @RunsInCurrentThread
  private static void checkSelection(@Nonnull JTree tree, @Nonnull int[] selection, @Nonnull Supplier<String> errMsg) {
    int[] selectionRows = tree.getSelectionRows();
    if (selectionRows == null || selectionRows.length == 0) {
      failNoSelection(errMsg);
      return;
    }
    sort(selection);
    if (Arrays.equals(selectionRows, selection)) {
      return;
    }
    failNotEqualSelection(errMsg, selection, selectionRows);
  }

  private static void failNotEqualSelection(@Nonnull Supplier<String> errMsg, @Nonnull int[] expected, @Nonnull int[] actual) {
    String format = "[%s] expecting selection:<%s> but was:<%s>";
    String msg = String.format(format, errMsg.get(), format(expected), format(actual));
    fail(msg);
  }

  @RunsInEDT
  static void checkHasSelection(final @Nonnull JTree tree, final @Nonnull String[] selection,
                                final @Nonnull JTreePathFinder pathFinder, final @Nonnull Supplier<String> errMsg) {
    execute(() -> checkSelection(tree, selection, pathFinder, errMsg));
  }

  @RunsInCurrentThread
  private static void checkSelection(@Nonnull JTree tree, @Nonnull String[] selection,
                                     @Nonnull JTreePathFinder pathFinder, @Nonnull Supplier<String> errMsg) {
    TreePath[] selectionPaths = tree.getSelectionPaths();
    if (selectionPaths == null || selectionPaths.length == 0) {
      failNoSelection(errMsg);
      return;
    }
    int selectionCount = selection.length;
    if (selectionCount != selectionPaths.length) {
      failNotEqualSelection(errMsg, selection, selectionPaths);
    }
    for (int i = 0; i < selectionCount; i++) {
      TreePath expected = matchingPathWithRootIfInvisible(tree, checkNotNull(selection[i]), pathFinder);
      TreePath actual = selectionPaths[i];
      if (!areEqual(expected, actual)) {
        failNotEqualSelection(errMsg, selection, selectionPaths);
      }
    }
  }

  private static void failNotEqualSelection(@Nonnull Supplier<String> errMsg, @Nonnull String[] expected,
                                            @Nonnull TreePath[] actual) {
    String format = "[%s] expecting selection:<%s> but was:<%s>";
    String msg = String.format(format, errMsg.get(), format(expected), format(actual));
    fail(msg);
  }

  private static void failNoSelection(final @Nonnull Supplier<String> errMessage) {
    fail(String.format("[%s] No selection", errMessage.get()));
  }

  @RunsInEDT
  static void checkNoSelection(final @Nonnull JTree tree, final @Nonnull Supplier<String> errMsg) {
    execute(() -> {
      if (tree.getSelectionCount() == 0) {
        return;
      }
      String format = "[%s] expected no selection but was:<%s>";
      String message = String.format(format, errMsg.get(), format(tree.getSelectionPaths()));
      fail(message);
    });
  }

  private JTreeVerifySelectionTask() {
  }
}
