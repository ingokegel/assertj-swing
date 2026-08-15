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
import org.assertj.swing.core.Robot;
import org.assertj.swing.edt.GuiQuery;
import org.assertj.swing.internal.annotation.InternalApi;
import org.assertj.swing.util.GenericRange;
import org.assertj.swing.util.Pair;

import org.jspecify.annotations.NonNull;
import javax.swing.*;
import java.awt.*;

import static org.assertj.swing.driver.ComponentPreconditions.checkEnabledAndShowing;
import static org.assertj.swing.driver.JSliderSetValueTask.setValue;
import static org.assertj.swing.edt.GuiActionRunner.execute;
import static org.assertj.swing.util.Preconditions.checkNotNull;

/**
 * <p>
 * Supports functional testing of {@code JSlider}s.
 * </p>
 * 
 * <p>
 * <b>Note:</b> This class is intended for internal use only. Please use the classes in the package
 * {@link org.assertj.swing.fixture} in your tests.
 * </p>
 * 
 * @author Alex Ruiz
 * @author Yvonne Wang
 */
@InternalApi
public class JSliderDriver extends JComponentDriver {
  private final JSliderLocation location;

  /**
   * Creates a new {@link JSliderDriver}.
   * 
   * @param robot the robot to use to simulate user input.
   */
  public JSliderDriver(@NonNull Robot robot) {
    super(robot);
    location = new JSliderLocation();
  }

  /**
   * Slides the knob to its maximum.
   * 
   * @param slider the target {@code JSlider}.
   * @throws IllegalStateException if the {@code JSlider} is disabled.
   * @throws IllegalStateException if the {@code JSlider} is not showing on the screen.
   */
  @RunsInEDT
  public void slideToMaximum(@NonNull JSlider slider) {
    slide(slider, findSlideToMaximumInfo(slider, location()));
  }

  @RunsInEDT
  @NonNull private static Pair<Integer, GenericRange<Point>> findSlideToMaximumInfo(final @NonNull JSlider slider,
      final @NonNull JSliderLocation location) {
    Pair<Integer, GenericRange<Point>> result = execute(new GuiQuery<Pair<Integer, GenericRange<Point>>>() {
      @Override
      protected Pair<Integer, GenericRange<Point>> executeInEDT() {
        checkEnabledAndShowing(slider);
        int value = slider.getMaximum();
        GenericRange<Point> fromAndTo = slideInfo(slider, location, value);
        return Pair.of(value, fromAndTo);
      }
    });
    return checkNotNull(result);
  }

  /**
   * Slides the knob to its minimum.
   * 
   * @param slider the target {@code JSlider}.
   * @throws IllegalStateException if the {@code JSlider} is disabled.
   * @throws IllegalStateException if the {@code JSlider} is not showing on the screen.
   */
  @RunsInEDT
  public void slideToMinimum(@NonNull JSlider slider) {
    slide(slider, findSlideToMinimumInfo(slider, location()));
  }

  @RunsInEDT
  @NonNull private static Pair<Integer, GenericRange<Point>> findSlideToMinimumInfo(final @NonNull JSlider slider,
      final @NonNull JSliderLocation location) {
    Pair<Integer, GenericRange<Point>> result = execute(new GuiQuery<Pair<Integer, GenericRange<Point>>>() {
      @Override
      protected Pair<Integer, GenericRange<Point>> executeInEDT() {
        checkEnabledAndShowing(slider);
        int value = slider.getMinimum();
        GenericRange<Point> fromAndTo = slideInfo(slider, location, value);
        return Pair.of(value, fromAndTo);
      }
    });
    return checkNotNull(result);
  }

  @RunsInEDT
  private void slide(@NonNull JSlider slider, @NonNull Pair<Integer, GenericRange<Point>> slideInfo) {
    slide(slider, slideInfo.first, checkNotNull(slideInfo.second));
  }

  /**
   * Slides the knob to the requested value.
   * 
   * @param slider the target {@code JSlider}.
   * @param value the requested value.
   * @throws IllegalStateException if the {@code JSlider} is disabled.
   * @throws IllegalStateException if the {@code JSlider} is not showing on the screen.
   * @throws IllegalArgumentException if the given position is not within the {@code JSlider} bounds.
   */
  @RunsInEDT
  public void slide(@NonNull JSlider slider, int value) {
    GenericRange<Point> slideInfo = findSlideInfo(slider, location(), value);
    slide(slider, value, slideInfo);
  }

  @RunsInEDT
  private void slide(@NonNull JSlider slider, int value, @NonNull GenericRange<Point> fromAndTo) {
    moveMouseIgnoringAnyError(slider, fromAndTo.from());
    moveMouseIgnoringAnyError(slider, fromAndTo.to());
    setValue(slider, value);
    robot.waitForIdle();
  }

  @RunsInEDT
  @NonNull private static GenericRange<Point> findSlideInfo(final @NonNull JSlider slider,
      final @NonNull JSliderLocation location, final int value) {
    GenericRange<Point> result = execute(new GuiQuery<GenericRange<Point>>() {
      @Override
      protected GenericRange<Point> executeInEDT() {
        checkValueInBounds(slider, value);
        checkEnabledAndShowing(slider);
        return slideInfo(slider, location, value);
      }
    });
    return checkNotNull(result);
  }

  @RunsInCurrentThread
  private static void checkValueInBounds(@NonNull JSlider slider, int value) {
    int min = slider.getMinimum();
    int max = slider.getMaximum();
    if (value >= min && value <= max) {
      return;
    }
    String msg = String.format("Value <%d> is not within the JSlider bounds of <%d> and <%d>", value, min, max);
    throw new IllegalArgumentException(msg);
  }

  @RunsInCurrentThread
  private static GenericRange<Point> slideInfo(@NonNull JSlider slider, JSliderLocation location, int value) {
    Point from = location.pointAt(slider, slider.getValue());
    Point to = location.pointAt(slider, value);
    return new GenericRange<Point>(from, to);
  }

  @NonNull private JSliderLocation location() {
    return location;
  }
}
