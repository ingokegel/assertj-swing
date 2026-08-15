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
import org.assertj.swing.annotation.VisibleForTesting;
import org.assertj.swing.cell.JTreeCellReader;
import org.assertj.swing.core.MouseButton;
import org.assertj.swing.core.MouseClickInfo;
import org.assertj.swing.core.Robot;
import org.assertj.swing.edt.GuiQuery;
import org.assertj.swing.exception.LocationUnavailableException;
import org.assertj.swing.exception.WaitTimedOutError;
import org.assertj.swing.internal.annotation.InternalApi;
import org.assertj.swing.util.ArrayPreconditions;
import org.assertj.swing.util.Pair;
import org.assertj.swing.util.Triple;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import javax.swing.*;
import javax.swing.plaf.TreeUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.function.Supplier;

import static org.assertj.swing.core.MouseButton.LEFT_BUTTON;
import static org.assertj.swing.core.MouseButton.RIGHT_BUTTON;
import static org.assertj.swing.driver.ComponentPreconditions.checkEnabledAndShowing;
import static org.assertj.swing.driver.JTreeChildrenShowUpCondition.untilChildrenShowUp;
import static org.assertj.swing.driver.JTreeClearSelectionTask.clearSelectionOf;
import static org.assertj.swing.driver.JTreeEditableQuery.isEditable;
import static org.assertj.swing.driver.JTreeExpandPathTask.expandTreePath;
import static org.assertj.swing.driver.JTreeMatchingPathQuery.matchingPathFor;
import static org.assertj.swing.driver.JTreeMatchingPathQuery.verifyJTreeIsReadyAndFindMatchingPath;
import static org.assertj.swing.driver.JTreeNodeTextQuery.nodeText;
import static org.assertj.swing.driver.JTreeToggleExpandStateTask.toggleExpandState;
import static org.assertj.swing.driver.JTreeVerifySelectionTask.checkHasSelection;
import static org.assertj.swing.driver.JTreeVerifySelectionTask.checkNoSelection;
import static org.assertj.swing.edt.GuiActionRunner.execute;
import static org.assertj.swing.exception.ActionFailedException.actionFailure;
import static org.assertj.swing.timing.Pause.pause;
import static org.assertj.swing.util.Platform.controlOrCommandKey;
import static org.assertj.swing.util.Preconditions.checkNotNull;
import static org.assertj.swing.util.Preconditions.checkNotNullOrEmpty;
import static org.assertj.swing.util.Require.assertThat;
import static org.assertj.swing.util.Strings.concat;

/**
 * <p>
 * Supports functional testing of {@code JTree}s.
 * </p>
 *
 * <p>
 * <b>Note:</b> This class is intended for internal use only. Please use the classes in the package
 * {@link org.assertj.swing.fixture} in your tests.
 * </p>
 *
 * @author Alex Ruiz
 */
@InternalApi
public class JTreeDriver extends JComponentDriver {
  private static final String EDITABLE_PROPERTY = "editable";
  private static final String SELECTION_PROPERTY = "selection";

  private final JTreeLocation location;
  private final JTreePathFinder pathFinder;

  /**
   * Creates a new {@link JTreeDriver}.
   *
   * @param robot the robot to use to simulate user input.
   */
  public JTreeDriver(@NonNull Robot robot) {
    super(robot);
    location = new JTreeLocation();
    pathFinder = new JTreePathFinder();
  }

  /**
   * Clicks the given row.
   *
   * @param tree the target {@code JTree}.
   * @param row the given row.
   * @throws IllegalStateException if the {@code JTree} is disabled.
   * @throws IllegalStateException if the {@code JTree} is not showing on the screen.
   * @throws IndexOutOfBoundsException if the given row is less than zero or equal than or greater than the number of
   *           visible rows in the {@code JTree}.
   * @throws LocationUnavailableException if a tree path for the given row cannot be found.
   */
  @RunsInEDT
  public void clickRow(@NonNull JTree tree, int row) {
    Point p = scrollToRow(tree, row);
    robot.click(tree, p);
  }

  /**
   * Clicks the given row.
   *
   * @param tree the target {@code JTree}.
   * @param row the given row.
   * @param button the mouse button to use.
   * @throws NullPointerException if the given button is {@code null}.
   * @throws IllegalStateException if the {@code JTree} is disabled.
   * @throws IllegalStateException if the {@code JTree} is not showing on the screen.
   * @throws IndexOutOfBoundsException if the given row is less than zero or equal than or greater than the number of
   *           visible rows in the {@code JTree}.
   * @throws LocationUnavailableException if a tree path for the given row cannot be found.
   */
  @RunsInEDT
  public void clickRow(@NonNull JTree tree, int row, @NonNull MouseButton button) {
    checkNotNull(button);
    clickRow(tree, row, button, 1);
  }

  /**
   * Clicks the given row.
   *
   * @param tree the target {@code JTree}.
   * @param row the given row.
   * @param mouseClickInfo specifies the mouse button to use and how many times to click.
   * @throws NullPointerException if the given {@code MouseClickInfo} is {@code null}.
   * @throws IllegalStateException if the {@code JTree} is disabled.
   * @throws IllegalStateException if the {@code JTree} is not showing on the screen.
   * @throws IndexOutOfBoundsException if the given row is less than zero or equal than or greater than the number of
   *           visible rows in the {@code JTree}.
   * @throws LocationUnavailableException if a tree path for the given row cannot be found.
   */
  @RunsInEDT
  public void clickRow(@NonNull JTree tree, int row, @NonNull MouseClickInfo mouseClickInfo) {
    checkNotNull(mouseClickInfo);
    clickRow(tree, row, mouseClickInfo.button(), mouseClickInfo.times());
  }

  @RunsInEDT
  private void clickRow(@NonNull JTree tree, int row, @NonNull MouseButton button, int times) {
    Point p = scrollToRow(tree, row);
    robot.click(tree, p, button, times);
  }

  /**
   * Double-clicks the given row.
   *
   * @param tree the target {@code JTree}.
   * @param row the given row.
   * @throws IllegalStateException if the {@code JTree} is disabled.
   * @throws IllegalStateException if the {@code JTree} is not showing on the screen.
   * @throws IndexOutOfBoundsException if the given row is less than zero or equal than or greater than the number of
   *           visible rows in the {@code JTree}.
   * @throws LocationUnavailableException if a tree path for the given row cannot be found.
   */
  @RunsInEDT
  public void doubleClickRow(@NonNull JTree tree, int row) {
    Point p = scrollToRow(tree, row);
    doubleClick(tree, p);
  }

  /**
   * Right-clicks the given row.
   *
   * @param tree the target {@code JTree}.
   * @param row the given row.
   * @throws IllegalStateException if the {@code JTree} is disabled.
   * @throws IllegalStateException if the {@code JTree} is not showing on the screen.
   * @throws IndexOutOfBoundsException if the given row is less than zero or equal than or greater than the number of
   *           visible rows in the {@code JTree}.
   * @throws LocationUnavailableException if a tree path for the given row cannot be found.
   */
  @RunsInEDT
  public void rightClickRow(@NonNull JTree tree, int row) {
    Point p = scrollToRow(tree, row);
    rightClick(tree, p);
  }

  @RunsInEDT
  @NonNull private Point scrollToRow(@NonNull JTree tree, int row) {
    Point p = scrollToRow(tree, row, location(), false).second;
    robot.waitForIdle();
    return checkNotNull(p);
  }

  /**
   * Clicks the given path, expanding parent nodes if necessary.
   *
   * @param tree the target {@code JTree}.
   * @param path the path to path.
   * @throws IllegalStateException if the {@code JTree} is disabled.
   * @throws IllegalStateException if the {@code JTree} is not showing on the screen.
   * @throws LocationUnavailableException if the given path cannot be found.
   */
  @RunsInEDT
  public void clickPath(@NonNull JTree tree, @NonNull String path) {
    Point p = scrollToPath(tree, path);
    robot.click(tree, p);
  }

  /**
   * Clicks the given path, expanding parent nodes if necessary.
   *
   * @param tree the target {@code JTree}.
   * @param path the path to path.
   * @param button the mouse button to use.
   * @throws NullPointerException if the given button is {@code null}.
   * @throws IllegalStateException if the {@code JTree} is disabled.
   * @throws IllegalStateException if the {@code JTree} is not showing on the screen.
   * @throws LocationUnavailableException if the given path cannot be found.
   */
  @RunsInEDT
  public void clickPath(@NonNull JTree tree, @NonNull String path, @NonNull MouseButton button) {
    checkNotNull(button);
    clickPath(tree, path, button, 1);
  }

  /**
   * Clicks the given path, expanding parent nodes if necessary.
   *
   * @param tree the target {@code JTree}.
   * @param path the path to path.
   * @param mouseClickInfo specifies the mouse button to use and how many times to click.
   * @throws NullPointerException if the given {@code MouseClickInfo} is {@code null}.
   * @throws IllegalStateException if the {@code JTree} is disabled.
   * @throws IllegalStateException if the {@code JTree} is not showing on the screen.
   * @throws LocationUnavailableException if the given path cannot be found.
   */
  @RunsInEDT
  public void clickPath(@NonNull JTree tree, @NonNull String path, @NonNull MouseClickInfo mouseClickInfo) {
    checkNotNull(mouseClickInfo);
    clickPath(tree, path, mouseClickInfo.button(), mouseClickInfo.times());
  }

  private void clickPath(@NonNull JTree tree, @NonNull String path, @NonNull MouseButton button, int times) {
    Point p = scrollToPath(tree, path);
    robot.click(tree, p, button, times);
  }

  /**
   * Double-clicks the given path.
   *
   * @param tree the target {@code JTree}.
   * @param path the path to double-click.
   * @throws IllegalStateException if the {@code JTree} is disabled.
   * @throws IllegalStateException if the {@code JTree} is not showing on the screen.
   * @throws LocationUnavailableException if the given path cannot be found.
   */
  @RunsInEDT
  public void doubleClickPath(@NonNull JTree tree, @NonNull String path) {
    Point p = scrollToPath(tree, path);
    doubleClick(tree, p);
  }

  @NonNull private Point scrollToPath(@NonNull JTree tree, @NonNull String path) {
    Point p = scrollToMatchingPath(tree, path, false).third;
    robot.waitForIdle();
    return checkNotNull(p);
  }

  private void doubleClick(@NonNull JTree tree, @NonNull Point p) {
    robot.click(tree, p, LEFT_BUTTON, 2);
  }

  /**
   * Right-clicks the given path, expanding parent nodes if necessary.
   *
   * @param tree the target {@code JTree}.
   * @param path the path to path.
   * @throws IllegalStateException if the {@code JTree} is disabled.
   * @throws IllegalStateException if the {@code JTree} is not showing on the screen.
   * @throws LocationUnavailableException if the given path cannot be found.
   */
  @RunsInEDT
  public void rightClickPath(@NonNull JTree tree, @NonNull String path) {
    Point p = scrollToPath(tree, path);
    rightClick(tree, p);
  }

  private void rightClick(@NonNull JTree tree, @NonNull Point p) {
    robot.click(tree, p, RIGHT_BUTTON, 1);
  }

  /**
   * Expands the given row, is possible. If the row is already expanded, this method will not do anything.
   * <p>
   * NOTE: a reasonable assumption is that the toggle control is just to the left of the row bounds and is roughly a
   * square the dimensions of the row height. Clicking in the centre of that square should work.
   * </p>
   *
   * @param tree the target {@code JTree}.
   * @param row the given row.
   * @throws IllegalStateException if the {@code JTree} is disabled.
   * @throws IllegalStateException if the {@code JTree} is not showing on the screen.
   * @throws IndexOutOfBoundsException if the given row is less than zero or equal than or greater than the number of
   *           visible rows in the {@code JTree}.
   * @throws LocationUnavailableException if a tree path for the given row cannot be found.
   * @throws org.assertj.swing.exception.ActionFailedException if this method fails to expand the row.
   */
  @RunsInEDT
  public void expandRow(@NonNull JTree tree, int row) {
    Triple<Boolean, Point, Integer> info = scrollToRowAndGetToggleInfo(tree, row, location());
    robot.waitForIdle();
    if (info.first) {
      return; // already expanded
    }
    toggleCell(tree, checkNotNull(info.second), info.third);
  }

  /**
   * Collapses the given row, is possible. If the row is already collapsed, this method will not do anything.
   * <p>
   * NOTE: a reasonable assumption is that the toggle control is just to the left of the row bounds and is roughly a
   * square the dimensions of the row height. Clicking in the centre of that square should work.
   * </p>
   *
   * @param tree the target {@code JTree}.
   * @param row the given row.
   * @throws IllegalStateException if the {@code JTree} is disabled.
   * @throws IllegalStateException if the {@code JTree} is not showing on the screen.
   * @throws IndexOutOfBoundsException if the given row is less than zero or equal than or greater than the number of
   *           visible rows in the {@code JTree}.
   * @throws LocationUnavailableException if a tree path for the given row cannot be found.
   * @throws org.assertj.swing.exception.ActionFailedException if this method fails to collapse the row.
   */
  @RunsInEDT
  public void collapseRow(@NonNull JTree tree, int row) {
    Triple<Boolean, Point, Integer> info = scrollToRowAndGetToggleInfo(tree, row, location());
    robot.waitForIdle();
    if (!info.first) {
      return; // already collapsed
    }
    toggleCell(tree, checkNotNull(info.second), info.third);
  }

  /**
   * Change the open/closed state of the given row, if possible.
   * <p>
   * NOTE: a reasonable assumption is that the toggle control is just to the left of the row bounds and is roughly a
   * square the dimensions of the row height. Clicking in the centre of that square should work.
   * </p>
   *
   * @param tree the target {@code JTree}.
   * @param row the given row.
   * @throws IllegalStateException if the {@code JTree} is disabled.
   * @throws IllegalStateException if the {@code JTree} is not showing on the screen.
   * @throws IndexOutOfBoundsException if the given row is less than zero or equal than or greater than the number of
   *           visible rows in the {@code JTree}.
   * @throws LocationUnavailableException if a tree path for the given row cannot be found.
   * @throws org.assertj.swing.exception.ActionFailedException if this method fails to toggle the row.
   */
  @RunsInEDT
  public void toggleRow(@NonNull JTree tree, int row) {
    Triple<Boolean, Point, Integer> info = scrollToRowAndGetToggleInfo(tree, row, location());
    robot.waitForIdle();
    toggleCell(tree, checkNotNull(info.second), info.third);
  }

  /*
   * Returns: 1. if the row is expanded 2. the location of the row 3. the number of mouse clicks to toggle a row
   */
  @RunsInEDT
  @NonNull private static Triple<Boolean, Point, Integer> scrollToRowAndGetToggleInfo(final @NonNull JTree tree,
                                                                                      final int row,
                                                                                      final @NonNull JTreeLocation location) {
    Triple<Boolean, Point, Integer> result = execute(new GuiQuery<Triple<Boolean, Point, Integer>>() {
      @Override
      protected Triple<Boolean, Point, Integer> executeInEDT() {
        checkEnabledAndShowing(tree);
        Point p = scrollToVisible(tree, row, location);
        return Triple.of(tree.isExpanded(row), p, tree.getToggleClickCount());
      }
    });
    return checkNotNull(result);
  }

  /**
   * Expands the given path, is possible. If the path is already expanded, this method will not do anything.
   * <p>
   * NOTE: a reasonable assumption is that the toggle control is just to the left of the row bounds and is roughly a
   * square the dimensions of the row height. Clicking in the centre of that square should work.
   * </p>
   *
   * @param tree the target {@code JTree}.
   * @param path the path to expand.
   * @throws IllegalStateException if the {@code JTree} is disabled.
   * @throws IllegalStateException if the {@code JTree} is not showing on the screen.
   * @throws LocationUnavailableException if the given path cannot be found.
   * @throws org.assertj.swing.exception.ActionFailedException if this method fails to expand the path.
   */
  @RunsInEDT
  public void expandPath(@NonNull JTree tree, @NonNull String path) {
    Triple<Boolean, Point, Integer> info = scrollToMatchingPathAndGetToggleInfo(tree, path, pathFinder(), location());
    if (info.first) {
      return; // already expanded
    }
    toggleCell(tree, checkNotNull(info.second), info.third);
  }

  /**
   * Collapses the given path, is possible. If the path is already expanded, this method will not do anything.
   * <p>
   * NOTE: a reasonable assumption is that the toggle control is just to the left of the row bounds and is roughly a
   * square the dimensions of the row height. Clicking in the centre of that square should work.
   * </p>
   *
   * @param tree the target {@code JTree}.
   * @param path the path to collapse.
   * @throws IllegalStateException if the {@code JTree} is disabled.
   * @throws IllegalStateException if the {@code JTree} is not showing on the screen.
   * @throws LocationUnavailableException if the given path cannot be found.
   * @throws org.assertj.swing.exception.ActionFailedException if this method fails to collapse the path.
   */
  @RunsInEDT
  public void collapsePath(@NonNull JTree tree, @NonNull String path) {
    Triple<Boolean, Point, Integer> info = scrollToMatchingPathAndGetToggleInfo(tree, path, pathFinder(), location());
    if (!info.first) {
      return; // already collapsed
    }
    toggleCell(tree, checkNotNull(info.second), info.third);
  }

  /*
   * Returns: 1. if the node is expanded 2. the location of the node 3. the number of mouse clicks to toggle a node
   */
  @RunsInEDT
  @NonNull private static Triple<Boolean, Point, Integer> scrollToMatchingPathAndGetToggleInfo(
                                                                                               final @NonNull JTree tree,
                                                                                               final @NonNull String path,
                                                                                               final @NonNull JTreePathFinder pathFinder,
                                                                                               final @NonNull JTreeLocation location) {
    Triple<Boolean, Point, Integer> result = execute(new GuiQuery<Triple<Boolean, Point, Integer>>() {
      @Override
      protected Triple<Boolean, Point, Integer> executeInEDT() {
        checkEnabledAndShowing(tree);
        TreePath matchingPath = matchingPathFor(tree, path, pathFinder);
        Point p = scrollToTreePath(tree, matchingPath, location);
        return Triple.of(tree.isExpanded(matchingPath), p, tree.getToggleClickCount());
      }
    });
    return checkNotNull(result);
  }

  @RunsInEDT
  private void toggleCell(@NonNull JTree tree, @NonNull Point p, int toggleClickCount) {
    if (toggleClickCount == 0) {
      toggleRowThroughTreeUI(tree, p);
      robot.waitForIdle();
      return;
    }
    robot.click(tree, p, LEFT_BUTTON, toggleClickCount);
  }

  @RunsInEDT
  private static void toggleRowThroughTreeUI(final @NonNull JTree tree, final @NonNull Point p) {
    execute(() -> {
      TreeUI treeUI = tree.getUI();
      if (!(treeUI instanceof BasicTreeUI)) {
        throw actionFailure(concat("Can't toggle row for ", treeUI));
      }
      toggleExpandState(tree, p);
    });
  }

  /**
   * Selects the given rows.
   *
   * @param tree the target {@code JTree}.
   * @param rows the rows to select.
   * @throws NullPointerException if the array of rows is {@code null}.
   * @throws IllegalArgumentException if the array of rows is empty.
   * @throws IllegalStateException if the {@code JTree} is disabled.
   * @throws IllegalStateException if the {@code JTree} is not showing on the screen.
   * @throws IndexOutOfBoundsException if any of the given rows is less than zero or equal than or greater than the
   *           number of visible rows in the {@code JTree}.
   * @throws LocationUnavailableException if a tree path for any of the given rows cannot be found.
   */
  @RunsInEDT
  public void selectRows(final @NonNull JTree tree, final @NonNull int[] rows) {
    ArrayPreconditions.checkNotNullOrEmpty(rows);
    clearSelection(tree);
    new MultipleSelectionTemplate(robot) {
      @Override
      int elementCount() {
        return rows.length;
      }

      @Override
      void selectElement(int index) {
        selectRow(tree, rows[index]);
      }
    }.multiSelect();
  }

  /**
   * Unselects the given rows.
   *
   * @param tree the target {@code JTree}.
   * @param rows the rows to unselect.
   * @throws NullPointerException if the array of rows is {@code null}.
   * @throws IllegalArgumentException if the array of rows is empty.
   * @throws IllegalStateException if the {@code JTree} is disabled.
   * @throws IllegalStateException if the {@code JTree} is not showing on the screen.
   * @throws IndexOutOfBoundsException if any of the given rows is less than zero or equal than or greater than the
   *           number of visible rows in the {@code JTree}.
   * @throws LocationUnavailableException if a tree path for any of the given rows cannot be found.
   */
  @RunsInEDT
  public void unselectRows(final @NonNull JTree tree, final @NonNull int[] rows) {
    ArrayPreconditions.checkNotNullOrEmpty(rows);
    new MultipleSelectionTemplate(robot) {
      @Override
      int elementCount() {
        return rows.length;
      }

      @Override
      void unselectElement(int index) {
        unselectRow(tree, rows[index]);
      }
    }.multiUnselect();
  }

  @RunsInEDT
  private void clearSelection(final @NonNull JTree tree) {
    clearSelectionOf(tree);
    robot.waitForIdle();
  }

  /**
   * Selects the given row.
   *
   * @param tree the target {@code JTree}.
   * @param row the row to select.
   * @throws IllegalStateException if the {@code JTree} is disabled.
   * @throws IllegalStateException if the {@code JTree} is not showing on the screen.
   * @throws IndexOutOfBoundsException if the given row is less than zero or equal than or greater than the number of
   *           visible rows in the {@code JTree}.
   */
  @RunsInEDT
  public void selectRow(@NonNull JTree tree, int row) {
    scrollAndSelectRow(tree, row, true, false);
  }

  /**
   * Unselects the given row.
   *
   * @param tree the target {@code JTree}.
   * @param row the row to unselect.
   * @throws IllegalStateException if the {@code JTree} is disabled.
   * @throws IllegalStateException if the {@code JTree} is not showing on the screen.
   * @throws IndexOutOfBoundsException if the given row is less than zero or equal than or greater than the number of
   *           visible rows in the {@code JTree}.
   */
  @RunsInEDT
  public void unselectRow(@NonNull JTree tree, int row) {
    int key = controlOrCommandKey();
    robot.pressKeyWhileRunning(key, () -> scrollAndSelectRow(tree, row, false, false));
  }

  /**
   * Selects the given paths, expanding parent nodes if necessary.
   *
   * @param tree the target {@code JTree}.
   * @param paths the paths to select.
   * @throws NullPointerException if the array of rows is {@code null}.
   * @throws IllegalArgumentException if the array of rows is empty.
   * @throws IllegalStateException if the {@code JTree} is disabled.
   * @throws IllegalStateException if the {@code JTree} is not showing on the screen.
   * @throws LocationUnavailableException if any the given path cannot be found.
   */
  @RunsInEDT
  public void selectPaths(final @NonNull JTree tree, final @NonNull String[] paths) {
    checkNotNullOrEmpty(paths);
    clearSelection(tree);
    new MultipleSelectionTemplate(robot) {
      @Override
      int elementCount() {
        return paths.length;
      }

      @Override
      void selectElement(int index) {
        selectPath(tree, checkNotNull(paths[index]));
      }
    }.multiSelect();
  }

  /**
   * Unselects the given paths, expanding parent nodes if necessary.
   *
   * @param tree the target {@code JTree}.
   * @param paths the paths to unselect.
   * @throws NullPointerException if the array of rows is {@code null}.
   * @throws IllegalArgumentException if the array of rows is empty.
   * @throws IllegalStateException if the {@code JTree} is disabled.
   * @throws IllegalStateException if the {@code JTree} is not showing on the screen.
   * @throws LocationUnavailableException if any the given path cannot be found.
   */
  @RunsInEDT
  public void unselectPaths(final @NonNull JTree tree, final @NonNull String[] paths) {
    checkNotNullOrEmpty(paths);
    new MultipleSelectionTemplate(robot) {
      @Override
      int elementCount() {
        return paths.length;
      }

      @Override
      void unselectElement(int index) {
        unselectPath(tree, checkNotNull(paths[index]));
      }
    }.multiUnselect();
  }

  /**
   * Selects the given path, expanding parent nodes if necessary. Unlike {@link #clickPath(JTree, String)}, this method
   * will not click the path if it is already selected
   *
   * @param tree the target {@code JTree}.
   * @param path the path to select.
   * @throws IllegalStateException if the {@code JTree} is disabled.
   * @throws IllegalStateException if the {@code JTree} is not showing on the screen.
   * @throws LocationUnavailableException if the given path cannot be found.
   */
  @RunsInEDT
  public void selectPath(@NonNull JTree tree, @NonNull String path) {
    selectMatchingPath(tree, path, true, false);
  }

  /**
   * Unselects the given path, expanding parent nodes if necessary. Unlike {@link #clickPath(JTree, String)}, this
   * method
   * will not click the path if it is not selected
   *
   * @param tree the target {@code JTree}.
   * @param path the path to unselect.
   * @throws IllegalStateException if the {@code JTree} is disabled.
   * @throws IllegalStateException if the {@code JTree} is not showing on the screen.
   * @throws LocationUnavailableException if the given path cannot be found.
   */
  @RunsInEDT
  public void unselectPath(@NonNull JTree tree, @NonNull String path) {
    int key = controlOrCommandKey();
    robot.pressKeyWhileRunning(key, () -> selectMatchingPath(tree, path, false, false));
  }

  /**
   * Shows a pop-up menu at the position of the node in the given row.
   *
   * @param tree the target {@code JTree}.
   * @param row the given row.
   * @return a driver that manages the displayed pop-up menu.
   * @throws IllegalStateException if the {@code JTree} is disabled.
   * @throws IllegalStateException if the {@code JTree} is not showing on the screen.
   * @throws org.assertj.swing.exception.ComponentLookupException if a pop-up menu cannot be found.
   * @throws IndexOutOfBoundsException if the given row is less than zero or equal than or greater than the number of
   *           visible rows in the {@code JTree}.
   * @throws LocationUnavailableException if a tree path for the given row cannot be found.
   */
  @RunsInEDT
  @NonNull public JPopupMenu showPopupMenu(@NonNull JTree tree, int row) {
    Pair<Boolean, Point> info = scrollToRow(tree, row, location(), true);
    Point p = checkNotNull(info.second);
    return robot.showPopupMenu(tree, p);
  }

  /**
   * Shows a pop-up menu at the position of the last node in the given path. The last node in the given path will be
   * made visible (by expanding the parent node(s)) if it is not visible.
   *
   * @param tree the target {@code JTree}.
   * @param path the given path.
   * @return a driver that manages the displayed pop-up menu.
   * @throws IllegalStateException if the {@code JTree} is disabled.
   * @throws IllegalStateException if the {@code JTree} is not showing on the screen.
   * @throws org.assertj.swing.exception.ComponentLookupException if a pop-up menu cannot be found.
   * @throws LocationUnavailableException if the given path cannot be found.
   * @see #replaceSeparator(String)
   */
  @RunsInEDT
  @NonNull public JPopupMenu showPopupMenu(@NonNull JTree tree, @NonNull String path) {
    Triple<TreePath, Boolean, Point> info = scrollToMatchingPath(tree, path, true);
    robot.waitForIdle();
    Point where = checkNotNull(info.third);
    return robot.showPopupMenu(tree, where);
  }

  /**
   * Starts a drag operation at the location of the given row.
   *
   * @param tree the target {@code JTree}.
   * @param row the given row.
   * @throws IllegalStateException if the {@code JTree} is disabled.
   * @throws IllegalStateException if the {@code JTree} is not showing on the screen.
   * @throws IndexOutOfBoundsException if the given row is less than zero or equal than or greater than the number of
   *           visible rows in the {@code JTree}.
   * @throws LocationUnavailableException if a tree path for the given row cannot be found.
   */
  @RunsInEDT
  public void drag(@NonNull JTree tree, int row) {
    Point p = scrollAndSelectRow(tree, row, true, true);
    drag(tree, p);
  }

  @RunsInEDT
  @NonNull private Point scrollAndSelectRow(@NonNull JTree tree, int row, boolean select, boolean singleSelectRequired) {
    Pair<Boolean, Point> info = scrollToRow(tree, row, location(), singleSelectRequired);
    Point p = checkNotNull(info.second);
    if (info.first != select) {
      robot.click(tree, p);
    }
    return p;
  }

  /**
   * Ends a drag operation at the location of the given row.
   *
   * @param tree the target {@code JTree}.
   * @param row the given row.
   * @throws IllegalStateException if the {@code JTree} is disabled.
   * @throws IllegalStateException if the {@code JTree} is not showing on the screen.
   * @throws IndexOutOfBoundsException if the given row is less than zero or equal than or greater than the number of
   *           visible rows in the {@code JTree}.
   * @throws LocationUnavailableException if a tree path for the given row cannot be found.
   * @throws org.assertj.swing.exception.ActionFailedException if there is no drag action in effect.
   */
  @RunsInEDT
  public void drop(@NonNull JTree tree, int row) {
    Pair<Boolean, Point> info = scrollToRow(tree, row, location(), true);
    drop(tree, checkNotNull(info.second));
  }

  /*
   * Returns: 1. if the node is expanded 2. the location of the node
   */
  @RunsInEDT
  @NonNull private static Pair<Boolean, Point> scrollToRow(final @NonNull JTree tree, final int row,
                                                           final @NonNull JTreeLocation location,
                                                           boolean singleSelectRequired) {
    Pair<Boolean, Point> result = execute(new GuiQuery<Pair<Boolean, Point>>() {
      @Override
      protected Pair<Boolean, Point> executeInEDT() {
        checkEnabledAndShowing(tree);
        Point p = scrollToVisible(tree, row, location);
        boolean selected = (!singleSelectRequired || tree.getSelectionCount() == 1) && tree.isRowSelected(row);
        return Pair.of(selected, p);
      }
    });
    return checkNotNull(result);
  }

  @RunsInCurrentThread
  @NonNull private static Point scrollToVisible(@NonNull JTree tree, int row, @NonNull JTreeLocation location) {
    Pair<Rectangle, Point> boundsAndCoordinates = location.rowBoundsAndCoordinates(tree, row);
    tree.scrollRectToVisible(boundsAndCoordinates.first);
    return checkNotNull(boundsAndCoordinates.second);
  }

  /**
   * Starts a drag operation at the location of the given {@code TreePath}.
   *
   * @param tree the target {@code JTree}.
   * @param path the given path.
   * @throws IllegalStateException if the {@code JTree} is disabled.
   * @throws IllegalStateException if the {@code JTree} is not showing on the screen.
   * @throws LocationUnavailableException if the given path cannot be found.
   * @see #replaceSeparator(String)
   */
  @RunsInEDT
  public void drag(@NonNull JTree tree, @NonNull String path) {
    Point p = selectMatchingPath(tree, path, true, true);
    drag(tree, p);
  }

  @RunsInEDT
  @NonNull private Point selectMatchingPath(@NonNull JTree tree, @NonNull String path, boolean select,
                                            boolean singleSelectionRequired) {
    Triple<TreePath, Boolean, Point> info = scrollToMatchingPath(tree, path, singleSelectionRequired);
    robot.waitForIdle();
    Point where = checkNotNull(info.third);
    if (info.second != select) {
      robot.click(tree, where);
    }
    return where;
  }

  /**
   * Ends a drag operation at the location of the given {@code TreePath}.
   *
   * @param tree the target {@code JTree}.
   * @param path the given path.
   * @throws IllegalStateException if the {@code JTree} is disabled.
   * @throws IllegalStateException if the {@code JTree} is not showing on the screen.
   * @throws LocationUnavailableException if the given path cannot be found.
   * @throws org.assertj.swing.exception.ActionFailedException if there is no drag action in effect.
   * @see #replaceSeparator(String)
   */
  @RunsInEDT
  public void drop(@NonNull JTree tree, @NonNull String path) {
    Point p = scrollToMatchingPath(tree, path, true).third;
    drop(tree, checkNotNull(p));
  }

  /*
   * returns: 1. the found matching path 2. whether the path is already selected 3. the location where the path is in
   * the JTree
   */
  @RunsInEDT
  @NonNull private Triple<TreePath, Boolean, Point> scrollToMatchingPath(@NonNull JTree tree, @NonNull String path,
                                                                         boolean singleSelectionRequired) {
    TreePath matchingPath = verifyJTreeIsReadyAndFindMatchingPath(tree, path, pathFinder());
    makeVisible(tree, matchingPath, false);
    Pair<Boolean, Point> info = scrollToPathToSelect(tree, matchingPath, location(), singleSelectionRequired);
    return Triple.of(matchingPath, info.first, info.second);
  }

  /*
   * returns: 1. whether the path is already selected 2. the location where the path is in the JTree
   */
  @RunsInEDT
  @NonNull private static Pair<Boolean, Point> scrollToPathToSelect(final @NonNull JTree tree,
                                                                    final @NonNull TreePath path,
                                                                    final @NonNull JTreeLocation location,
                                                                    boolean singleSelectionRequired) {
    Pair<Boolean, Point> result = execute(new GuiQuery<Pair<Boolean, Point>>() {
      @Override
      protected Pair<Boolean, Point> executeInEDT() {
        boolean isSelected = (!singleSelectionRequired || tree.getSelectionCount() == 1) && tree.isPathSelected(path);
        return Pair.of(isSelected, scrollToTreePath(tree, path, location));
      }
    });
    return checkNotNull(result);
  }

  @RunsInCurrentThread
  @NonNull private static Point scrollToTreePath(@NonNull JTree tree, @NonNull TreePath path,
                                                 @NonNull JTreeLocation location) {
    Pair<Rectangle, Point> boundsAndCoordinates = location.pathBoundsAndCoordinates(tree, path);
    tree.scrollRectToVisible(boundsAndCoordinates.first);
    return checkNotNull(boundsAndCoordinates.second);
  }

  @RunsInEDT
  private boolean makeParentVisible(@NonNull JTree tree, @NonNull TreePath path) {
    boolean changed = makeVisible(tree, checkNotNull(path.getParentPath()), true);
    if (changed) {
      robot.waitForIdle();
    }
    return changed;
  }

  /**
   * Matches, makes visible, and expands the path one component at a time, from uppermost ancestor on down, since
   * children may be lazily loaded/created.
   *
   * @param tree the target {@code JTree}.
   * @param path the tree path to make visible.
   * @param expandWhenFound indicates if nodes should be expanded or not when found.
   * @return if it was necessary to make visible and/or expand a node in the path.
   */
  @RunsInEDT
  private boolean makeVisible(@NonNull JTree tree, @NonNull TreePath path, boolean expandWhenFound) {
    boolean changed = false;
    if (path.getPathCount() > 1) {
      changed = makeParentVisible(tree, path);
    }
    if (!expandWhenFound) {
      return changed;
    }
    expandTreePath(tree, path);
    waitForChildrenToShowUp(tree, path);
    return true;
  }

  @RunsInEDT
  private void waitForChildrenToShowUp(@NonNull JTree tree, @NonNull TreePath path) {
    int timeout = robot.settings().timeoutToBeVisible();
    try {
      pause(untilChildrenShowUp(tree, path), timeout);
    } catch (WaitTimedOutError e) {
      throw new LocationUnavailableException(e.getMessage());
    }
  }

  /**
   * Asserts that the given {@code JTree}'s selected rows are equal to the given one.
   *
   * @param tree the target {@code JTree}.
   * @param rows the indices of the rows, expected to be selected.
   * @throws NullPointerException if the array of row indices is {@code null}.
   * @throws AssertionError if the given {@code JTree} selection is not equal to the given rows.
   */
  @RunsInEDT
  public void requireSelection(@NonNull JTree tree, @NonNull int[] rows) {
    checkNotNull(rows);
    checkHasSelection(tree, rows, selectionProperty(tree));
  }

  /**
   * Asserts that the given {@code JTree}'s selected paths are equal to the given one.
   *
   * @param tree the target {@code JTree}.
   * @param paths the given paths, expected to be selected.
   * @throws NullPointerException if the array of paths is {@code null}.
   * @throws LocationUnavailableException if any of the given paths cannot be found.
   * @throws AssertionError if the given {@code JTree} selection is not equal to the given paths.
   * @see #replaceSeparator(String)
   */
  @RunsInEDT
  public void requireSelection(@NonNull JTree tree, @NonNull String[] paths) {
    checkNotNull(paths);
    checkHasSelection(tree, paths, pathFinder(), selectionProperty(tree));
  }

  /**
   * Asserts that the given {@code JTree} does not have any selection.
   *
   * @param tree the given {@code JTree}.
   * @throws AssertionError if the {@code JTree} has a selection.
   */
  @RunsInEDT
  public void requireNoSelection(@NonNull JTree tree) {
    checkNoSelection(tree, selectionProperty(tree));
  }

  @RunsInEDT
  @NonNull private Supplier<String> selectionProperty(@NonNull JTree tree) {
    return propertyName(tree, SELECTION_PROPERTY);
  }

  /**
   * Asserts that the given {@code JTree} is editable.
   *
   * @param tree the given {@code JTree}.
   * @throws AssertionError if the {@code JTree} is not editable.
   */
  @RunsInEDT
  public void requireEditable(@NonNull JTree tree) {
    assertEditable(tree, true);
  }

  /**
   * Asserts that the given {@code JTree} is not editable.
   *
   * @param tree the given {@code JTree}.
   * @throws AssertionError if the {@code JTree} is editable.
   */
  @RunsInEDT
  public void requireNotEditable(@NonNull JTree tree) {
    assertEditable(tree, false);
  }

  @RunsInEDT
  private void assertEditable(@NonNull JTree tree, boolean editable) {
    assertThat(isEditable(tree)).as(editableProperty(tree)).isEqualTo(editable);
  }

  @RunsInEDT
  @NonNull private static Supplier<String> editableProperty(@NonNull JTree tree) {
    return propertyName(tree, EDITABLE_PROPERTY);
  }

  /**
   * @return the separator to use when converting {@code TreePath}s to {@code String}s.
   */
  @NonNull public String separator() {
    return pathFinder.separator();
  }

  /**
   * Updates the separator to use when converting {@code TreePath}s to {@code String}s.
   *
   * @param newSeparator the new separator.
   * @throws NullPointerException if the given separator is {@code null}.
   */
  public void replaceSeparator(@NonNull String newSeparator) {
    pathFinder.replaceSeparator(checkNotNull(newSeparator));
  }

  /**
   * Updates the implementation of {@link JTreeCellReader} to use when comparing internal values of a {@code JTree} and
   * the values expected in a test.
   *
   * @param newCellReader the new {@code JTreeCellValueReader} to use.
   * @throws NullPointerException if {@code newCellReader} is {@code null}.
   */
  public void replaceCellReader(@NonNull JTreeCellReader newCellReader) {
    pathFinder.replaceCellReader(checkNotNull(newCellReader));
  }

  /**
   * Verifies that the given row index is valid.
   *
   * @param tree the given {@code JTree}.
   * @param row the given index.
   * @throws IndexOutOfBoundsException if the given index is less than zero or equal than or greater than the number of
   *           visible rows in the {@code JTree}.
   */
  @RunsInEDT
  public void checkRowInBounds(@NonNull JTree tree, int row) {
    location.checkRowInBounds(tree, row);
  }

  /**
   * Verifies that the given node path exists.
   *
   * @param tree the given {@code JTree}.
   * @param path the given path.
   * @throws LocationUnavailableException if the given path cannot be found.
   */
  @RunsInEDT
  public void checkPathExists(@NonNull JTree tree, @NonNull String path) {
    matchingPathFor(tree, path, pathFinder());
  }

  /**
   * Returns the {@code String} representation of the node at the given path.
   *
   * @param tree the given {@code JTree}.
   * @param path the given path.
   * @return the {@code String} representation of the node at the given path.
   * @throws LocationUnavailableException if the given path cannot be found.
   */
  @RunsInEDT
  @Nullable public String nodeValue(@NonNull JTree tree, @NonNull String path) {
    return nodeText(tree, path, pathFinder());
  }

  /**
   * Returns the {@code String} representation of the node at the given row index.
   *
   * @param tree the given {@code JTree}.
   * @param row the given row.
   * @return the {@code String} representation of the node at the given row index.
   * @throws IndexOutOfBoundsException if the given row is less than zero or equal than or greater than the number of
   *           visible rows in the {@code JTree}.
   * @throws LocationUnavailableException if a tree path for the given row cannot be found.
   */
  @Nullable public String nodeValue(@NonNull JTree tree, int row) {
    return nodeText(tree, row, location(), pathFinder());
  }

  @VisibleForTesting
  JTreeCellReader cellReader() {
    return pathFinder.cellReader();
  }

  @NonNull private JTreeLocation location() {
    return location;
  }

  @NonNull private JTreePathFinder pathFinder() {
    return pathFinder;
  }
}
