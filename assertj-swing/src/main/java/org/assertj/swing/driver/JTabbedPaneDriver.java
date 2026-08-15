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
import org.assertj.swing.annotation.VisibleForTesting;
import org.assertj.swing.core.Robot;
import org.assertj.swing.data.Index;
import org.assertj.swing.edt.GuiQuery;
import org.assertj.swing.exception.ActionFailedException;
import org.assertj.swing.exception.LocationUnavailableException;
import org.assertj.swing.internal.annotation.InternalApi;
import org.assertj.swing.util.Pair;
import org.assertj.swing.util.PatternTextMatcher;
import org.assertj.swing.util.StringTextMatcher;
import org.assertj.swing.util.TextMatcher;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import static org.assertj.swing.driver.ComponentPreconditions.checkEnabledAndShowing;
import static org.assertj.swing.driver.JTabbedPaneSelectTabQuery.selectedTabIndexOf;
import static org.assertj.swing.driver.JTabbedPaneSelectTabTask.setSelectedTab;
import static org.assertj.swing.driver.JTabbedPaneTabTitlesQuery.tabTitlesOf;
import static org.assertj.swing.edt.GuiActionRunner.execute;
import static org.assertj.swing.util.Lists.newArrayList;
import static org.assertj.swing.util.Preconditions.checkNotNull;
import static org.assertj.swing.util.Require.assertThat;
import static org.assertj.swing.util.Require.verifyThat;

/**
 * <p>
 * Supports functional testing of {@code JTabbedPane}s.
 * </p>
 *
 * <p>
 * <b>Note:</b> This class is intended for internal use only. Please use the classes in the package
 * {@link org.assertj.swing.fixture} in your tests.
 * </p>
 *
 * @author Alex Ruiz
 * @author Yvonne Wang
 * @author William Bakker
 */
@InternalApi
public class JTabbedPaneDriver extends JComponentDriver {
  private final JTabbedPaneLocation location;

  /**
   * Creates a new {@link JTabbedPaneDriver}.
   *
   * @param robot the robot to use to simulate user input.
   */
  public JTabbedPaneDriver(@NonNull Robot robot) {
    this(robot, new JTabbedPaneLocation());
  }

  /**
   * Creates a new {@link JTabbedPaneDriver}.
   *
   * @param robot the robot to use to simulate user input.
   * @param location knows how to find the location of a tab.
   */
  @VisibleForTesting
  JTabbedPaneDriver(@NonNull Robot robot, @NonNull JTabbedPaneLocation location) {
    super(robot);
    this.location = location;
  }

  /**
   * Returns the titles of all the tabs.
   *
   * @param tabbedPane the target {@code JTabbedPane}.
   * @return the titles of all the tabs.
   */
  @RunsInEDT
  @NonNull public String[] tabTitles(@NonNull JTabbedPane tabbedPane) {
    return tabTitlesOf(tabbedPane);
  }

  /**
   * Simulates a user selecting the tab containing the given title.
   *
   * @param tabbedPane the target {@code JTabbedPane}.
   * @param title the given text to match. It can be a regular expression.
   * @throws IllegalStateException if the {@code JTabbedPane} is disabled.
   * @throws IllegalStateException if the {@code JTabbedPane} is not showing on the screen.
   * @throws LocationUnavailableException if a tab matching the given title could not be found.
   */
  @RunsInEDT
  public void selectTab(@NonNull JTabbedPane tabbedPane, @Nullable String title) {
    selectTab(tabbedPane, new StringTextMatcher(title));
  }

  /**
   * Simulates a user selecting the tab whose title matches the given regular expression pattern.
   *
   * @param tabbedPane the target {@code JTabbedPane}.
   * @param pattern the regular expression pattern to match.
   * @throws IllegalStateException if the {@code JTabbedPane} is disabled.
   * @throws IllegalStateException if the {@code JTabbedPane} is not showing on the screen.
   * @throws NullPointerException if the given regular expression pattern is {@code null}.
   * @throws LocationUnavailableException if a tab matching the given regular expression pattern could not be found.
   */
  @RunsInEDT
  public void selectTab(@NonNull JTabbedPane tabbedPane, @NonNull Pattern pattern) {
    selectTab(tabbedPane, new PatternTextMatcher(pattern));
  }

  @RunsInEDT
  private void selectTab(@NonNull JTabbedPane tabbedPane, @NonNull TextMatcher matcher) {
    Pair<Integer, Point> tabToSelectInfo = tabToSelectInfo(location(), tabbedPane, matcher);
    Point target = tabToSelectInfo.second;
    if (target != null) {
      try {
        checkInEdtEnabledAndShowing(tabbedPane);
        click(tabbedPane, target);
      } catch (ActionFailedException e) {
        // On Mac it may be necessary to scroll the tabs to find the one to click.
        setTabDirectly(tabbedPane, tabToSelectInfo.first);
      }
      return;
    }
    setTabDirectly(tabbedPane, tabToSelectInfo.first);
  }

  @RunsInEDT
  @NonNull private static Pair<Integer, Point> tabToSelectInfo(final @NonNull JTabbedPaneLocation location,
                                                               final @NonNull JTabbedPane tabbedPane,
                                                               final @NonNull TextMatcher matcher) {
    Pair<Integer, Point> result = execute(new GuiQuery<Pair<Integer, Point>>() {
      @Override
      protected Pair<Integer, Point> executeInEDT() {
        checkEnabledAndShowing(tabbedPane);
        int index = location.indexOf(tabbedPane, matcher);
        location.checkIndexInBounds(tabbedPane, index);
        Point point = null;
        try {
          point = location.pointAt(tabbedPane, index);
        } catch (LocationUnavailableException e) {
        }
        return Pair.of(index, point);
      }
    });
    return checkNotNull(result);
  }

  /**
   * Simulates a user selecting the tab located at the given index.
   *
   * @param tabbedPane the target {@code JTabbedPane}.
   * @param index the index of the tab to select.
   * @throws IllegalStateException if the {@code JTabbedPane} is disabled.
   * @throws IllegalStateException if the {@code JTabbedPane} is not showing on the screen.
   * @throws IndexOutOfBoundsException if the given index is not within the {@code JTabbedPane} bounds.
   */
  @RunsInEDT
  public void selectTab(@NonNull JTabbedPane tabbedPane, int index) {
    try {
      Point p = pointAtTabWhenShowing(location(), tabbedPane, index);
      checkInEdtEnabledAndShowing(tabbedPane);
      click(tabbedPane, p);
    } catch (LocationUnavailableException | ActionFailedException e) {
      setTabDirectly(tabbedPane, index);
    }
  }

  @RunsInEDT
  @NonNull private static Point pointAtTabWhenShowing(final @NonNull JTabbedPaneLocation location,
                                                      final @NonNull JTabbedPane tabbedPane, final int index) {
    Point result = execute(() -> {
      location.checkIndexInBounds(tabbedPane, index);
      checkEnabledAndShowing(tabbedPane);
      return location.pointAt(tabbedPane, index);
    });
    return checkNotNull(result);
  }

  @RunsInEDT
  @VisibleForTesting
  void setTabDirectly(@NonNull JTabbedPane tabbedPane, int index) {
    setSelectedTab(tabbedPane, index);
    robot.waitForIdle();
    moveMouseToTab(tabbedPane, index);
  }

  private void moveMouseToTab(@NonNull JTabbedPane tabbedPane, int index) {
    try {
      Point p = pointAtTab(location(), tabbedPane, index);
      robot.moveMouse(tabbedPane, p);
      robot.waitForIdle();
    } catch (LocationUnavailableException ignored) {
    }
  }

  @RunsInEDT
  @NonNull private static Point pointAtTab(final @NonNull JTabbedPaneLocation location,
                                           final @NonNull JTabbedPane tabbedPane, final int index) {
    Point result = execute(() -> location.pointAt(tabbedPane, index));
    return checkNotNull(result);
  }

  /**
   * Returns the currently selected component for the given {@code JTabbedPane}.
   *
   * @param tabbedPane the target {@code JTabbedPane}.
   * @return the currently selected component for the given {@code JTabbedPane}.
   */
  @RunsInEDT
  @Nullable public Component selectedComponentOf(@NonNull JTabbedPane tabbedPane) {
    return selectedComponent(tabbedPane);
  }

  @RunsInEDT
  @Nullable private static Component selectedComponent(final JTabbedPane tabbedPane) {
    return execute(() -> tabbedPane.getSelectedComponent());
  }

  /**
   * Asserts that the title of the tab at the given index matches the given value.
   *
   * @param tabbedPane the target {@code JTabbedPane}.
   * @param title the expected title. It can be a regular expression.
   * @param index the index of the tab.
   * @throws IndexOutOfBoundsException if the given index is not within the {@code JTabbedPane} bounds.
   * @throws AssertionError if the title of the tab at the given index does not match the given one.
   */
  @RunsInEDT
  public void requireTabTitle(@NonNull JTabbedPane tabbedPane, @Nullable String title, @NonNull Index index) {
    String actualTitle = titleAt(tabbedPane, index);
    verifyThat(actualTitle).as(titleAtProperty(tabbedPane)).isEqualOrMatches(title);
  }

  /**
   * Asserts that the title of the tab at the given index matches the given regular expression pattern.
   *
   * @param tabbedPane the target {@code JTabbedPane}.
   * @param pattern the regular expression pattern to match.
   * @param index the index of the tab.
   * @throws NullPointerException if the given regular expression pattern is {@code null}.
   * @throws IndexOutOfBoundsException if the given index is not within the {@code JTabbedPane} bounds.
   * @throws AssertionError if the title of the tab at the given index does not match the given one.
   */
  @RunsInEDT
  public void requireTabTitle(@NonNull JTabbedPane tabbedPane, @NonNull Pattern pattern, @NonNull Index index) {
    String actualTitle = titleAt(tabbedPane, index);
    verifyThat(actualTitle).as(titleAtProperty(tabbedPane)).matches(pattern);
  }

  /**
   * Asserts that the tab at the given index is the selected tab.
   *
   * @param tabbedPane the target {@code JTabbedPane}.
   * @param index the index of the selected tab.
   * @throws AssertionError if the index of the selected tab does not match the given one.
   */
  @RunsInEDT
  public void requireSelectedTab(@NonNull JTabbedPane tabbedPane, @NonNull Index index) {
    assertThat(selectedTabIndexOf(tabbedPane).value).as(propertyName(tabbedPane, "selectedIndex"))
                                                    .isEqualTo(index.value);
  }

  /**
   * Asserts that the toolTipText of the tab at the given index matches the given value.
   *
   * @param tabbedPane the target {@code JTabbedPane}.
   * @param toolTipText the expected toolTipText. It can be a regular expression.
   * @param index the index of the tab.
   * @throws IndexOutOfBoundsException if the given index is not within the {@code JTabbedPane} bounds.
   * @throws AssertionError if the toolTipText of the tab at the given index does not match the given one.
   */
  @RunsInEDT
  public void requireTabToolTipText(@NonNull JTabbedPane tabbedPane, @Nullable String toolTipText, @NonNull Index index) {
    String actualToolTipText = toolTipTextAt(tabbedPane, index);
    verifyThat(actualToolTipText).as(toolTipTextAtProperty(tabbedPane)).isEqualOrMatches(toolTipText);
  }

  /**
   * Asserts that the toolTipText of the tab at the given index matches the given regular expression pattern.
   *
   * @param tabbedPane the target {@code JTabbedPane}.
   * @param pattern the regular expression pattern to match.
   * @param index the index of the tab.
   * @throws NullPointerException if the given regular expression pattern is {@code null}.
   * @throws IndexOutOfBoundsException if the given index is not within the {@code JTabbedPane} bounds.
   * @throws AssertionError if the toolTipText of the tab at the given index does not match the given one.
   */
  @RunsInEDT
  public void requireTabToolTipText(@NonNull JTabbedPane tabbedPane, @NonNull Pattern pattern, @NonNull Index index) {
    String actualToolTipText = toolTipTextAt(tabbedPane, index);
    verifyThat(actualToolTipText).as(toolTipTextAtProperty(tabbedPane)).matches(pattern);
  }

  /**
   * Asserts that the tab at the given index is enabled.
   *
   * @param tabbedPane the target {@code JTabbedPane}.
   * @param index the index of the tab.
   * @throws IndexOutOfBoundsException if the given index is not within the {@code JTabbedPane} bounds.
   * @throws AssertionError if the tab at the given index is not enabled.
   */
  @RunsInEDT
  public void requireTabEnabled(@NonNull JTabbedPane tabbedPane, @NonNull Index index) {
    boolean actualEnabled = isEnabledAt(tabbedPane, index);
    assertThat(actualEnabled).as(enabledAtProperty(tabbedPane)).isTrue();
  }

  /**
   * Asserts that the tab at the given index is disabled.
   *
   * @param tabbedPane the target {@code JTabbedPane}.
   * @param index the index of the tab.
   * @throws IndexOutOfBoundsException if the given index is not within the {@code JTabbedPane} bounds.
   * @throws AssertionError if the tab at the given index is not disabled.
   */
  @RunsInEDT
  public void requireTabDisabled(@NonNull JTabbedPane tabbedPane, @NonNull Index index) {
    boolean actualEnabled = isEnabledAt(tabbedPane, index);
    assertThat(actualEnabled).as(enabledAtProperty(tabbedPane)).isFalse();
  }

  @RunsInEDT
  private Supplier<String> titleAtProperty(@NonNull JTabbedPane tabbedPane) {
    return propertyName(tabbedPane, "titleAt");
  }

  @RunsInEDT
  private Supplier<String> toolTipTextAtProperty(@NonNull JTabbedPane tabbedPane) {
    return propertyName(tabbedPane, "toolTipTextAt");
  }

  @RunsInEDT
  private Supplier<String> enabledAtProperty(@NonNull JTabbedPane tabbedPane) {
    return propertyName(tabbedPane, "enabledAt");
  }

  @RunsInEDT
  @Nullable private static String titleAt(final @NonNull JTabbedPane tabbedPane, final @NonNull Index index) {
    return execute(() -> tabbedPane.getTitleAt(index.value));
  }

  @RunsInEDT
  @Nullable private static String toolTipTextAt(final @NonNull JTabbedPane tabbedPane, final @NonNull Index index) {
    return execute(() -> tabbedPane.getToolTipTextAt(index.value));
  }

  @RunsInEDT
  @Nullable private static boolean isEnabledAt(final @NonNull JTabbedPane tabbedPane, final @NonNull Index index) {
    return execute(() -> tabbedPane.isEnabledAt(index.value));
  }

  /**
   * Asserts that the tabs of the given {@code JTabbedPane} have the given titles. The tab titles are evaluated by index
   * order, for example, the first tab is expected to have the first title in the given array, and so on.
   *
   * @param tabbedPane the target {@code JTabbedPane}.
   * @param titles the expected titles.
   * @throws AssertionError if the title of any of the tabs is not equal to the expected titles.
   */
  @RunsInEDT
  public void requireTabTitles(@NonNull JTabbedPane tabbedPane, @NonNull String[] titles) {
    String[] actualTitles = allTabTitlesIn(tabbedPane);
    assertThat(actualTitles).as(propertyName(tabbedPane, "tabTitles")).isEqualTo(titles);
  }

  @RunsInEDT
  @NonNull private static String[] allTabTitlesIn(final @NonNull JTabbedPane tabbedPane) {
    String[] result = execute(() -> {
      List<String> allTitles = newArrayList();
      int tabCount = tabbedPane.getTabCount();
      for (int i = 0; i < tabCount; i++) {
        allTitles.add(tabbedPane.getTitleAt(i));
      }
      return allTitles.toArray(new String[allTitles.size()]);
    });
    return checkNotNull(result);
  }

  @NonNull private JTabbedPaneLocation location() {
    return location;
  }
}
