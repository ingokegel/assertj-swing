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
package org.assertj.swing.input;

import static java.awt.event.MouseEvent.MOUSE_MOVED;
import static java.awt.event.MouseEvent.MOUSE_PRESSED;
import static java.awt.event.MouseEvent.MOUSE_RELEASED;

import java.awt.Component;
import java.awt.Point;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Description of a drag/drop operation.
 * 
 * @author Alex Ruiz
 */
class DragDropInfo {
  private Component source;
  private int x;
  private int y;

  void clear() {
    source(null);
  }

  void update(@Nonnull MouseEvent event) {
    int mouseEventId = event.getID();
    if (mouseEventId == MOUSE_RELEASED || mouseEventId == MOUSE_MOVED) {
      clear();
      return;
    }
    if (mouseEventId == MOUSE_PRESSED) {
      source(event.getComponent());
      x = event.getX();
      y = event.getY();
    }
  }

  @Nullable
  Component source() {
    return source;
  }

  void source(@Nullable Component newSource) {
    source = newSource;
  }

  boolean isDragging() {
    return source != null;
  }

  @Nonnull
  Point origin() {
    return new Point(x, y);
  }

  void origin(@Nonnull Point origin) {
    x = origin.x;
    y = origin.y;
  }

  boolean isNativeDragActive() {
    try {
      Class<?> type = Class.forName("sun.awt.dnd.SunDragSourceContextPeer");
      Method checkDragDropInProgress = type.getDeclaredMethod("checkDragDropInProgress");
      checkDragDropInProgress.setAccessible(true);
      checkDragDropInProgress.invoke(null);
      return false;
    } catch (InvocationTargetException e) {
      return e.getTargetException() instanceof InvalidDnDOperationException;
    } catch (Exception ignored) {
      return false;
    }
  }
}
