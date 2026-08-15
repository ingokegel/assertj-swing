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

import org.jspecify.annotations.NonNull;
import javax.swing.*;
import javax.swing.plaf.TreeUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreePath;
import java.awt.*;

import static org.assertj.swing.util.Require.assertThat;
import static org.assertj.swing.util.Reflection.invokeMethod;

/**
 * Uses reflection to toggle the "expand state" of a node in a given {@code JTextComponent}. This task is executed in
 * the event dispatch thread (EDT).
 * 
 * @author Yvonne Wang
 */
final class JTreeToggleExpandStateTask {
  @RunsInCurrentThread
  static void toggleExpandState(final @NonNull JTree tree, final @NonNull Point pathLocation) {
    TreePath path = tree.getPathForLocation(pathLocation.x, pathLocation.y);
    TreeUI treeUI = tree.getUI();
    assertThat(treeUI).isInstanceOf(BasicTreeUI.class);
    invokeMethod(treeUI, "toggleExpandState", new Class<?>[] { TreePath.class }, path);
  }

  private JTreeToggleExpandStateTask() {
  }
}