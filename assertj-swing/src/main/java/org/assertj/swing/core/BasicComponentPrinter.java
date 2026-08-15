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
package org.assertj.swing.core;

import org.assertj.swing.annotation.RunsInCurrentThread;
import org.assertj.swing.annotation.RunsInEDT;
import org.assertj.swing.hierarchy.ComponentHierarchy;
import org.assertj.swing.hierarchy.ExistingHierarchy;
import org.assertj.swing.hierarchy.SingleComponentHierarchy;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.awt.*;
import java.io.PrintStream;

import static org.assertj.swing.edt.GuiActionRunner.execute;
import static org.assertj.swing.format.Formatting.format;
import static org.assertj.swing.hierarchy.NewHierarchy.ignoreExistingComponents;
import static org.assertj.swing.util.Preconditions.checkNotNull;

/**
 * Default implementation of {@link ComponentPrinter}.
 *
 * @author Alex Ruiz
 *
 * @see ComponentPrinter
 * @see org.assertj.swing.format.Formatting#format(Component)
 */
public final class BasicComponentPrinter implements ComponentPrinter {
  private static final String INDENTATION = "  ";

  private static final ComponentMatcher ALWAYS_MATCHES = alwaysMatches();

  @NonNull private static ComponentMatcher alwaysMatches() {
    return new ComponentMatcher() {
      @Override
      public boolean matches(@Nullable Component c) {
        return true;
      }
    };
  }

  private final ComponentHierarchy hierarchy;

  /**
   * Creates a new {@link BasicComponentPrinter} with a new AWT hierarchy. AWT and Swing {@code Component}s created
   * before the created {@link BasicComponentPrinter} cannot be accessed by the created {@link BasicComponentPrinter}.
   *
   * @return the created finder.
   */
  @NonNull public static ComponentPrinter printerWithNewAwtHierarchy() {
    return new BasicComponentPrinter(ignoreExistingComponents());
  }

  /**
   * Creates a new {@link BasicComponentPrinter} that has access to all the AWT and Swing {@code Component}s in the AWT
   * hierarchy.
   *
   * @return the created printer.
   */
  @NonNull public static ComponentPrinter printerWithCurrentAwtHierarchy() {
    return new BasicComponentPrinter(new ExistingHierarchy());
  }

  /**
   * Creates a new {@link BasicComponentPrinter}.
   *
   * @param hierarchy the component hierarchy to use.
   */
  protected BasicComponentPrinter(@NonNull ComponentHierarchy hierarchy) {
    this.hierarchy = hierarchy;
  }

  /**
   * @return the component hierarchy used by this printer.
   */
  protected final @NonNull ComponentHierarchy hierarchy() {
    return hierarchy;
  }

  @RunsInEDT
  @Override
  public void printComponents(@NonNull PrintStream out) {
    printComponents(out, ALWAYS_MATCHES);
  }

  @RunsInEDT
  @Override
  public void printComponents(@NonNull PrintStream out, @Nullable Container root) {
    printComponents(out, ALWAYS_MATCHES, root);
  }

  @RunsInEDT
  @Override
  public void printComponents(@NonNull PrintStream out, @NonNull Class<? extends Component> type) {
    printComponents(out, type, null);
  }

  @RunsInEDT
  @Override
  public void printComponents(@NonNull PrintStream out, @NonNull Class<? extends Component> type,
                              @Nullable Container root) {
    print(hierarchy(root), new TypeMatcher(checkNotNull(type)), checkNotNull(out));
  }

  @Override
  public void printComponents(@NonNull PrintStream out, @NonNull ComponentMatcher matcher) {
    printComponents(out, matcher, null);
  }

  @Override
  public void printComponents(@NonNull PrintStream out, @NonNull ComponentMatcher matcher, @Nullable Container root) {
    print(hierarchy(root), checkNotNull(matcher), checkNotNull(out));
  }

  @NonNull private ComponentHierarchy hierarchy(@Nullable Container root) {
    return root != null ? new SingleComponentHierarchy(root, hierarchy) : hierarchy;
  }

  @RunsInEDT
  private static void print(@NonNull final ComponentHierarchy hierarchy, @NonNull final ComponentMatcher matcher,
                            @NonNull final PrintStream out) {
    execute(() -> {
      for (Component c : hierarchy.roots()) {
        print(checkNotNull(c), hierarchy, matcher, 0, out);
      }
    });
  }

  @RunsInCurrentThread
  private static void print(@NonNull Component c, @NonNull ComponentHierarchy h, @NonNull ComponentMatcher matcher,
                            int level, @NonNull PrintStream out) {
    if (matcher.matches(c)) {
      print(c, level, out);
    }
    for (Component child : h.childrenOf(c)) {
      print(checkNotNull(child), h, matcher, level + 1, out);
    }
  }

  @RunsInCurrentThread
  private static void print(@NonNull Component c, int level, @NonNull PrintStream out) {
    for (int i = 0; i < level; i++) {
      out.print(INDENTATION);
    }
    out.println(format(c));
  }
}
