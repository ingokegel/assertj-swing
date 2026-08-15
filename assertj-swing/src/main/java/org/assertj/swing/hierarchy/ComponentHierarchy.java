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
package org.assertj.swing.hierarchy;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.util.Collection;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import org.assertj.swing.annotation.RunsInCurrentThread;

/**
 * <p>
 * Provides access to all AWT or Swing {@code Component}s in a hierarchy.
 * </p>
 * 
 * <p>
 * <b>Note:</b> Methods in this interface are accessed in the current executing thread. Such thread may or may not be
 * the event dispatch thread (EDT). Client code must call methods in this interface from the EDT.
 * </p>
 * 
 * @author Alex Ruiz
 * @author Yvonne Wang
 */
@RunsInCurrentThread
public interface ComponentHierarchy {
  /**
   * @return all root {@code Container}s in the {@code ComponentHierarchy}.
   */
  @NonNull
  Collection<Container> roots();

  /**
   * Returns all the children of the given AWT or Swing {@code Component}.
   * 
   * @param c the given {@code Component}.
   * @return all the children of the given {@code Component}.
   */
  @NonNull
  Collection<Component> childrenOf(@NonNull Component c);

  /**
   * Returns the parent for the given AWT or Swing {@code Component}.
   * 
   * @param c the given {@code Component}.
   * @return the parent for the given {@code Component}.
   */
  @Nullable
  Container parentOf(@NonNull Component c);

  /**
   * Indicates whether this {@code ComponentHierarchy} contains the given AWT or Swing {@code Component}.
   * 
   * @param c the given {@code Component}.
   * @return {@code true} if this {@code ComponentHierarchy} contains the given {@code Component}, {@code false}
   *         otherwise.
   */
  boolean contains(@NonNull Component c);

  /**
   * Provides proper disposal of the given AWT or Swing {@code Window}, appropriate to this {@code ComponentHierarchy}.
   * After disposal, the {@code Window} and its descendants will no longer be reachable from this
   * {@code ComponentHierarchy}.
   * 
   * @param w the {@code Window} to dispose.
   */
  void dispose(@NonNull Window w);
}
