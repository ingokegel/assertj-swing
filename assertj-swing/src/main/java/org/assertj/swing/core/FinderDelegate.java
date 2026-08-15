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

import org.assertj.swing.annotation.RunsInEDT;
import org.assertj.swing.hierarchy.ComponentHierarchy;

import org.jspecify.annotations.NonNull;
import java.awt.*;
import java.util.Collection;
import java.util.Set;

import static org.assertj.swing.edt.GuiActionRunner.execute;
import static org.assertj.swing.util.Preconditions.checkNotNull;
import static org.assertj.swing.util.Sets.newLinkedHashSet;

/**
 * Finds all the AWT and Swing {@code Components} in a {@link ComponentHierarchy} that match the search criteria
 * specified in a {@link ComponentMatcher}.
 *
 * @author Alex Ruiz
 */
final class FinderDelegate {
  @RunsInEDT
  @NonNull
  Collection<Component> find(@NonNull ComponentHierarchy h, @NonNull ComponentMatcher m) {
    Set<Component> found = newLinkedHashSet();
    for (Component c : rootsOf(h)) {
      find(h, m, checkNotNull(c), found);
    }
    return found;
  }

  @RunsInEDT
  private void find(@NonNull ComponentHierarchy h, @NonNull ComponentMatcher m, @NonNull Component root,
                    @NonNull Set<Component> found) {
    for (Component c : childrenOfComponent(root, h)) {
      find(h, m, checkNotNull(c), found);
    }
    if (isMatching(root, m)) {
      found.add(root);
    }
  }

  @RunsInEDT
  @NonNull private static Collection<Component> childrenOfComponent(final @NonNull Component c,
                                                                    final @NonNull ComponentHierarchy h) {
    Collection<Component> children = execute(() -> h.childrenOf(c));
    return checkNotNull(children);
  }

  @RunsInEDT
  private static boolean isMatching(@NonNull final Component c, @NonNull final ComponentMatcher m) {
    Boolean matching = execute(() -> m.matches(c));
    return checkNotNull(matching);
  }

  @RunsInEDT
  @NonNull
  <T extends Component> Collection<T> find(@NonNull ComponentHierarchy h, @NonNull GenericTypeMatcher<T> m) {
    Set<T> found = newLinkedHashSet();
    for (Component c : rootsOf(h)) {
      find(h, m, checkNotNull(c), found);
    }
    return found;
  }

  @RunsInEDT
  @NonNull private static Collection<? extends Component> rootsOf(final @NonNull ComponentHierarchy h) {
    return checkNotNull(execute(() -> h.roots()));
  }

  @RunsInEDT
  private <T extends Component> void find(@NonNull ComponentHierarchy h, @NonNull GenericTypeMatcher<T> m,
                                          @NonNull Component root, Set<T> found) {
    for (Component c : childrenOfComponent(root, h)) {
      find(h, m, checkNotNull(c), found);
    }
    if (isMatching(root, m)) {
      found.add(m.supportedType().cast(root));
    }
  }

  @RunsInEDT
  private static <T extends Component> boolean isMatching(final @NonNull Component c,
                                                          final @NonNull GenericTypeMatcher<T> m) {
    Boolean matching = execute(() -> m.matches(c));
    return checkNotNull(matching);
  }
}
