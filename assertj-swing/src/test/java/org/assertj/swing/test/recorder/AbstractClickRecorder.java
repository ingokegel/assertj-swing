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
package org.assertj.swing.test.recorder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.core.MouseButton.LEFT_BUTTON;
import static org.assertj.swing.core.MouseButton.MIDDLE_BUTTON;
import static org.assertj.swing.core.MouseButton.RIGHT_BUTTON;
import static org.assertj.swing.util.Maps.newHashMap;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Map;

import org.jspecify.annotations.NonNull;

import org.assertj.swing.core.MouseButton;

/**
 * An abstract listener recording mouse events.
 *
 * @author Alex Ruiz
 * @author Yvonne Wang
 */
public class AbstractClickRecorder {

  private static final Map<Integer, MouseButton> MOUSE_BUTTON_MAP = newHashMap();

  static {
    MOUSE_BUTTON_MAP.put(MouseEvent.BUTTON1, LEFT_BUTTON);
    MOUSE_BUTTON_MAP.put(MouseEvent.BUTTON2, MIDDLE_BUTTON);
    MOUSE_BUTTON_MAP.put(MouseEvent.BUTTON3, RIGHT_BUTTON);
  }

  private MouseButton clickedButton;
  private int clickCount;
  private Point pointClicked;

  protected void record(@NonNull MouseEvent e) {
    clickedButton = MOUSE_BUTTON_MAP.get(e.getButton());
    clickCount = e.getClickCount();
    pointClicked = e.getPoint();
  }

  public final @NonNull AbstractClickRecorder wasNotClicked() {
    assertThat(clickedButton).isNull();
    return this;
  }

  public final @NonNull AbstractClickRecorder timesClicked(int times) {
    assertThat(clickCount).isEqualTo(times);
    return this;
  }

  public final @NonNull AbstractClickRecorder wasClicked() {
    return clicked(LEFT_BUTTON).timesClicked(1);
  }

  public final @NonNull AbstractClickRecorder wasDoubleClicked() {
    return clicked(LEFT_BUTTON).timesClicked(2);
  }

  public final @NonNull AbstractClickRecorder wasRightClicked() {
    return clicked(RIGHT_BUTTON).timesClicked(1);
  }

  public final @NonNull AbstractClickRecorder clicked(@NonNull MouseButton button) {
    return wasClickedWith(button);
  }

  public final @NonNull AbstractClickRecorder wasClickedWith(@NonNull MouseButton button) {
    assertThat(clickedButton).isEqualTo(button);
    return this;
  }

  public final @NonNull AbstractClickRecorder clickedAt(@NonNull Point p) {
    assertThat(pointClicked).isEqualTo(p);
    return this;
  }

  public final @NonNull Point pointClicked() {
    return pointClicked;
  }
}