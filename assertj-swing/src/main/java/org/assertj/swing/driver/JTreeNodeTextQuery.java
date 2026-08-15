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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import javax.swing.*;
import javax.swing.tree.TreePath;

import static org.assertj.swing.driver.JTreeMatchingPathQuery.matchingPathWithRootIfInvisible;
import static org.assertj.swing.edt.GuiActionRunner.execute;
import static org.assertj.swing.util.Preconditions.checkNotNull;

/**
 * Returns the text of a node in a {@code JTree}.
 *
 * @author Alex Ruiz
 */
final class JTreeNodeTextQuery {
  @RunsInEDT
  static @Nullable String nodeText(final @NonNull JTree tree, final int row, final @NonNull JTreeLocation location,
                                   final @NonNull JTreePathFinder pathFinder) {
    return execute(() -> {
      TreePath matchingPath = location.pathFor(tree, row);
      return pathFinder.cellReader().valueAt(tree, checkNotNull(matchingPath.getLastPathComponent()));
    });
  }

  @RunsInEDT
  static @Nullable String nodeText(final @NonNull JTree tree, final @NonNull String path,
                                   final @NonNull JTreePathFinder pathFinder) {
    return execute(() -> {
      TreePath matchingPath = matchingPathWithRootIfInvisible(tree, path, pathFinder);
      return pathFinder.cellReader().valueAt(tree, checkNotNull(matchingPath.getLastPathComponent()));
    });
  }

  private JTreeNodeTextQuery() {
  }
}
