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

import static org.assertj.core.util.Preconditions.checkNotNull;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.jspecify.annotations.NonNull;

/**
 * An event listener that records mouse events on a specific component as {@link MouseListener}.
 * 
 * @author Alex Ruiz
 * @author Yvonne Wang
 */
public class ClickRecorder extends AbstractClickRecorder {

  ClickRecorder() {
    // hide the constructor from the outside
  }

  static @NonNull ClickRecorder attachTo(@NonNull Component target) {
    ClickRecorder recorder = new ClickRecorder();
    attach(new ClickListener(recorder), target);
    return recorder;
  }

  private static void attach(@NonNull ClickListener listener, @NonNull Component target) {
    target.addMouseListener(listener);
    if (!(target instanceof Container)) {
      return;
    }
    for (Component c : ((Container) target).getComponents()) {
      attach(listener, checkNotNull(c));
    }
  }

  private static class ClickListener extends MouseAdapter {
    private final ClickRecorder owner;

    ClickListener(@NonNull ClickRecorder owner) {
      this.owner = owner;
    }

    @Override
    public void mousePressed(MouseEvent e) {
      owner.record(checkNotNull(e));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      owner.record(checkNotNull(e));
    }
  }
}