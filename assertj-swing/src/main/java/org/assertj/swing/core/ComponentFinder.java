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

import java.awt.Component;
import java.awt.Container;
import java.util.Collection;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import org.assertj.swing.annotation.RunsInEDT;

/**
 * Looks up AWT and Swing {@code Component}s based on different search criteria, such as a {@code Component}'s name,
 * type or label, and custom search criteria as well.
 *
 * @author Alex Ruiz
 */
@RunsInEDT
public interface ComponentFinder {
  /**
   * @return the {@code ComponentPrinter} in this finder.
   */
  @NonNull
  ComponentPrinter printer();

  /**
   * <p>
   * Finds an AWT or Swing {@code Component} by type. If this finder is attached to a {@link Robot}, it will use the
   * component lookup scope in the {@code Robot}'s {@link Settings} to determine whether the component to find should be
   * showing or not. If this finder is <em>not</em> attached to any {@code Robot}, the component to find does not have
   * to be showing.
   * </p>
   *
   * <p>
   * Example:
   * </p>
   *
   * <pre>
   * JTextField textbox = finder.findByType(JTextField.class);
   * </pre>
   *
   * @param <T> the type of the component to find.
   * @param type the class of the component to find.
   * @return the found component.
   * @throws org.assertj.swing.exception.ComponentLookupException if a matching component could not be found.
   * @throws org.assertj.swing.exception.ComponentLookupException if more than one matching component is found.
   * @see Robot#settings()
   * @see Settings#componentLookupScope()
   * @see ComponentLookupScope
   */
  @NonNull
  <T extends Component> T findByType(@NonNull Class<T> type);

  /**
   * Finds an AWT or Swing {@code Component} by type. For example:
   *
   * @param <T> the type of the component to find.
   * @param type the class of the component to find.
   * @param showing indicates whether the component to find should be visible (or showing) or not.
   * @return the found component.
   * @throws org.assertj.swing.exception.ComponentLookupException if a matching component could not be found.
   * @throws org.assertj.swing.exception.ComponentLookupException if more than one matching component is found.
   * @see #findByType(Class)
   */
  @NonNull
  <T extends Component> T findByType(@NonNull Class<T> type, boolean showing);

  /**
   * <p>
   * Finds an AWT or Swing {@code Component} by type in the hierarchy under the given root. If this finder is attached
   * to a {@link Robot}, it will use the component lookup scope in the {@code Robot}'s {@link Settings} to determine
   * whether the component to find should be showing or not. If this finder is <em>not</em> attached to any
   * {@code Robot}, the component to find does not have to be showing.
   * </p>
   *
   * <p>
   * Let's assume we have the following {@code JFrame} containing a {@code JTextField}:
   * </p>
   *
   * <pre>
   * JFrame myFrame = new JFrame();
   * myFrame.add(new JTextField());
   * </pre>
   *
   * <p>
   * If we want to get a reference to the {@code JTextField} in that particular {@code JFrame} without going through the
   * whole AWT component hierarchy, we could simply specify:
   * </p>
   *
   * <pre>
   * JTextField textbox = finder.findByType(myFrame, JTextField.class);
   * </pre>
   *
   * @param root the root used as the starting point of the search.
   * @param <T> the type of the component to find.
   * @param type the class of the component to find.
   * @return the found component.
   * @throws org.assertj.swing.exception.ComponentLookupException if a matching component could not be found.
   * @throws org.assertj.swing.exception.ComponentLookupException if more than one matching component is found.
   * @see Robot#settings()
   * @see Settings#componentLookupScope()
   * @see ComponentLookupScope
   */
  @NonNull
  <T extends Component> T findByType(@NonNull Container root, @NonNull Class<T> type);

  /**
   * Finds an AWT or Swing {@code Component} by type in the hierarchy under the given root.
   *
   * @param root the root used as the starting point of the search.
   * @param <T> the type of the component to find.
   * @param type the class of the component to find.
   * @param showing indicates whether the component to find should be visible (or showing) or not.
   * @return the found component.
   * @throws org.assertj.swing.exception.ComponentLookupException if a matching component could not be found.
   * @throws org.assertj.swing.exception.ComponentLookupException if more than one matching component is found.
   * @see #findByType(Container, Class)
   */
  @NonNull
  <T extends Component> T findByType(@NonNull Container root, @NonNull Class<T> type, boolean showing);

  /**
   * <p>
   * Finds an AWT or Swing {@code Component} by by the text of its associated {@code JLabel}. If this finder is attached
   * to a {@link Robot}, it will use the component lookup scope in the {@code Robot}'s {@link Settings} to determine
   * whether the component to find should be showing or not. If this finder is <em>not</em> attached to any
   * {@code Robot}, the component to find does not have to be showing.
   * </p>
   *
   * <p>
   * Let's assume we have the {@code JTextField} with a {@code JLabel} with text "Name";
   * </p>
   *
   * <pre>
   * JLabel label = new JLabel(&quot;Name&quot;);
   * JTextField textbox = new JTextField();
   * label.setLabelFor(textBox);
   * </pre>
   *
   * <p>
   * To get a reference to this {@code JTextField} by the text of its associated {@code JLabel}, we can specify:
   * </p>
   *
   * <pre>
   * JTextField textBox = (JTextField) finder.findByLabel(&quot;Name&quot;);
   * </pre>
   *
   * <p>
   * Please note that you need to cast the result of the lookup to the right type. To avoid casting, please use one of
   * following:
   * </p>
   * <ol>
   * <li>{@link #findByLabel(String, Class)}</li>
   * <li>{@link #findByLabel(String, Class, boolean)}</li>
   * <li>{@link #findByLabel(Container, String, Class)}</li>
   * <li>{@link #findByLabel(Container, String, Class, boolean)}</li>
   * </ol>
   *
   * @param label the text of the {@code JLabel} associated to the component to find.
   * @return the found component.
   * @throws org.assertj.swing.exception.ComponentLookupException if a matching component could not be found.
   * @throws org.assertj.swing.exception.ComponentLookupException if more than one matching component is found.
   * @see javax.swing.JLabel#getLabelFor()
   * @see javax.swing.JLabel#setLabelFor(Component)
   * @see Robot#settings()
   * @see Settings#componentLookupScope()
   * @see ComponentLookupScope
   */
  @NonNull
  Component findByLabel(@Nullable String label);

  /**
   * Finds an AWT or Swing {@code Component} by the text of its associated {@code JLabel} and type. If this finder is
   * attached to a {@link Robot}, it will use the component lookup scope in the {@code Robot}'s {@link Settings} to
   * determine whether the component to find should be showing or not. If this finder is <em>not</em> attached to any
   * {@code Robot}, the component to find does not have to be showing.
   *
   * @param label the text of the {@code JLabel} associated to the component to find.
   * @param <T> the type of the component to find.
   * @param type the class of the component to find.
   * @return the found component.
   * @throws org.assertj.swing.exception.ComponentLookupException if a matching component could not be found.
   * @throws org.assertj.swing.exception.ComponentLookupException if more than one matching component is found.
   * @see #findByLabel(String)
   * @see javax.swing.JLabel#getLabelFor()
   * @see javax.swing.JLabel#setLabelFor(Component)
   * @see Robot#settings()
   * @see Settings#componentLookupScope()
   * @see ComponentLookupScope
   */
  @NonNull
  <T extends Component> T findByLabel(@Nullable String label, @NonNull Class<T> type);

  /**
   * Finds an AWT or Swing {@code Component} by the text of its associated {@code JLabel} and type.
   *
   * @param label the text of the {@code JLabel} associated to the component to find.
   * @param <T> the type of the component to find.
   * @param type the class of the component to find.
   * @param showing indicates whether the component to find should be visible (or showing) or not.
   * @return the found component.
   * @throws org.assertj.swing.exception.ComponentLookupException if a matching component could not be found.
   * @throws org.assertj.swing.exception.ComponentLookupException if more than one matching component is found.
   * @see #findByLabel(String)
   * @see javax.swing.JLabel#getLabelFor()
   * @see javax.swing.JLabel#setLabelFor(Component)
   */
  @NonNull
  <T extends Component> T findByLabel(@Nullable String label, @NonNull Class<T> type, boolean showing);

  /**
   * Finds an AWT or Swing {@code Component} by by the text of its associated {@code JLabel}.
   *
   * @param label the text of the {@code JLabel} associated to the component to find.
   * @param showing indicates whether the component to find should be visible (or showing) or not.
   * @return the found component.
   * @throws org.assertj.swing.exception.ComponentLookupException if a matching component could not be found.
   * @throws org.assertj.swing.exception.ComponentLookupException if more than one matching component is found.
   * @see #findByLabel(String)
   * @see javax.swing.JLabel#getLabelFor()
   * @see javax.swing.JLabel#setLabelFor(Component)
   */
  @NonNull
  Component findByLabel(@Nullable String label, boolean showing);

  /**
   * Finds an AWT or Swing {@code Component} by the text of its associated {@code JLabel}, in the hierarchy under the
   * given root. If this finder is attached to a {@link Robot}, it will use the component lookup scope in the
   * {@code Robot}'s {@link Settings} to determine whether the component to find should be showing or not. If this
   * finder is <em>not</em> attached to any {@code Robot}, the component to find does not have to be showing.
   *
   * @param root the root used as the starting point of the search.
   * @param label the text of the {@code JLabel} associated to the component to find.
   * @return the found component.
   * @throws org.assertj.swing.exception.ComponentLookupException if a matching component could not be found.
   * @throws org.assertj.swing.exception.ComponentLookupException if more than one matching component is found.
   * @see #findByLabel(String)
   * @see javax.swing.JLabel#getLabelFor()
   * @see javax.swing.JLabel#setLabelFor(Component)
   * @see Robot#settings()
   * @see Settings#componentLookupScope()
   * @see ComponentLookupScope
   */
  @NonNull
  Component findByLabel(@NonNull Container root, @Nullable String label);

  /**
   * Finds an AWT or Swing {@code Component} by the text of its associated {@code JLabel}, in the hierarchy under the
   * given root.
   *
   * @param root the root used as the starting point of the search.
   * @param label the text of the {@code JLabel} associated to the component to find.
   * @param showing indicates whether the component to find should be visible (or showing) or not.
   * @return the found component.
   * @throws org.assertj.swing.exception.ComponentLookupException if a matching component could not be found.
   * @throws org.assertj.swing.exception.ComponentLookupException if more than one matching component is found.
   * @see #findByLabel(String)
   * @see javax.swing.JLabel#getLabelFor()
   * @see javax.swing.JLabel#setLabelFor(Component)
   */
  @NonNull
  Component findByLabel(@NonNull Container root, @Nullable String label, boolean showing);

  /**
   * Finds an AWT or Swing {@code Component} by the text of its associated {@code JLabel} and type, in the hierarchy
   * under the given root. If this finder is attached to a {@link Robot}, it will use the component lookup scope in the
   * {@code Robot}'s {@link Settings} to determine whether the component to find should be showing or not. If this
   * finder is <em>not</em> attached to any {@code Robot}, the component to find does not have to be showing.
   *
   * @param root the root used as the starting point of the search.
   * @param label the text of the {@code JLabel} associated to the component to find.
   * @param <T> the type of the component to find.
   * @param type the class of the component to find.
   * @return the found component.
   * @throws org.assertj.swing.exception.ComponentLookupException if a matching component could not be found.
   * @throws org.assertj.swing.exception.ComponentLookupException if more than one matching component is found.
   * @see #findByLabel(String)
   * @see javax.swing.JLabel#getLabelFor()
   * @see javax.swing.JLabel#setLabelFor(Component)
   * @see Robot#settings()
   * @see Settings#componentLookupScope()
   * @see ComponentLookupScope
   */
  @NonNull
  <T extends Component> T findByLabel(@NonNull Container root, @Nullable String label, @NonNull Class<T> type);

  /**
   * Finds an AWT or Swing {@code Component} by the text of its associated {@code JLabel} and type, in the hierarchy
   * under the given root.
   *
   * @param root the root used as the starting point of the search.
   * @param label the text of the {@code JLabel} associated to the component to find.
   * @param <T> the type of the component to find.
   * @param type the class of the component to find.
   * @param showing indicates whether the component to find should be visible (or showing) or not.
   * @return the found component.
   * @throws org.assertj.swing.exception.ComponentLookupException if a matching component could not be found.
   * @throws org.assertj.swing.exception.ComponentLookupException if more than one matching component is found.
   * @see #findByLabel(String)
   * @see javax.swing.JLabel#getLabelFor()
   * @see javax.swing.JLabel#setLabelFor(Component)
   */
  @NonNull
  <T extends Component> T findByLabel(@NonNull Container root, @Nullable String label, @NonNull Class<T> type,
                                      boolean showing);

  /**
   * <p>
   * Finds an AWT or Swing {@code Component} by name. If this finder is attached to a {@link Robot}, it will use the
   * component lookup scope in the {@code Robot}'s {@link Settings} to determine whether the component to find should be
   * showing or not. If this finder is <em>not</em> attached to any {@code Robot}, the component to find does not have
   * to be showing.
   * </p>
   *
   * <p>
   * Let's assume we have the {@code JTextField} with name "myTextBox";
   * </p>
   *
   * <pre>
   * JTextField textbox = new JTextField();
   * textBox.setName(&quot;myTextBox&quot;);
   * </pre>
   *
   * <p>
   * To get a reference to this {@code JTextField} by its name, we can specify:
   * </p>
   *
   * <pre>
   * JTextField textBox = (JTextField) finder.findByName(&quot;myTextBox&quot;);
   * </pre>
   *
   * <p>
   * Please note that you need to cast the result of the lookup to the right type. To avoid casting, please use one of
   * following:
   * </p>
   * <ol>
   * <li>{@link #findByName(String, Class)}</li>
   * <li>{@link #findByName(String, Class, boolean)}</li>
   * <li>{@link #findByName(Container, String, Class)}</li>
   * <li>{@link #findByName(Container, String, Class, boolean)}</li>
   * </ol>
   *
   * @param name the name of the component to find.
   * @return the found component.
   * @throws org.assertj.swing.exception.ComponentLookupException if a matching component could not be found.
   * @throws org.assertj.swing.exception.ComponentLookupException if more than one matching component is found.
   * @see Robot#settings()
   * @see Settings#componentLookupScope()
   * @see ComponentLookupScope
   */
  @NonNull
  Component findByName(@Nullable String name);

  /**
   * Finds an AWT or Swing {@code Component} by name and type. If this finder is attached to a {@link Robot} , it will
   * use the component lookup scope in the {@code Robot}'s {@link Settings} to determine whether the component to find
   * should be showing or not. If this finder is <em>not</em> attached to any {@code Robot}, the component to find does
   * not have to be showing.
   *
   * @param name the name of the component to find.
   * @param <T> the type of the component to find.
   * @param type the class of the component to find.
   * @return the found component.
   * @throws org.assertj.swing.exception.ComponentLookupException if a matching component could not be found.
   * @throws org.assertj.swing.exception.ComponentLookupException if more than one matching component is found.
   * @see Robot#settings()
   * @see Settings#componentLookupScope()
   * @see ComponentLookupScope
   * @see #findByName(String)
   */
  @NonNull
  <T extends Component> T findByName(@Nullable String name, @NonNull Class<T> type);

  /**
   * Finds an AWT or Swing {@code Component} by name and type.
   *
   * @param name the name of the component to find.
   * @param <T> the type of the component to find.
   * @param type the class of the component to find.
   * @param showing indicates whether the component to find should be visible (or showing) or not.
   * @return the found component.
   * @throws org.assertj.swing.exception.ComponentLookupException if a matching component could not be found.
   * @throws org.assertj.swing.exception.ComponentLookupException if more than one matching component is found.
   * @see #findByName(String)
   */
  @NonNull
  <T extends Component> T findByName(@Nullable String name, @NonNull Class<T> type, boolean showing);

  /**
   * Finds an AWT or Swing {@code Component} by name.
   *
   * @param name the name of the component to find.
   * @param showing indicates whether the component to find should be visible (or showing) or not.
   * @return the found component.
   * @throws org.assertj.swing.exception.ComponentLookupException if a matching component could not be found.
   * @throws org.assertj.swing.exception.ComponentLookupException if more than one matching component is found.
   * @see #findByName(String)
   */
  @NonNull
  Component findByName(@Nullable String name, boolean showing);

  /**
   * Finds an AWT or Swing {@code Component} by name, in the hierarchy under the given root. If this finder is attached
   * to a {@link Robot}, it will use the component lookup scope in the {@code Robot}'s {@link Settings} to determine
   * whether the component to find should be showing or not. If this finder is <em>not</em> attached to any
   * {@code Robot}, the component to find does not have to be showing.
   *
   * @param root the root used as the starting point of the search.
   * @param name the name of the component to find.
   * @return the found component.
   * @throws org.assertj.swing.exception.ComponentLookupException if a matching component could not be found.
   * @throws org.assertj.swing.exception.ComponentLookupException if more than one matching component is found.
   * @see Robot#settings()
   * @see Settings#componentLookupScope()
   * @see ComponentLookupScope
   * @see #findByName(String)
   */
  @NonNull
  Component findByName(@NonNull Container root, @Nullable String name);

  /**
   * Finds an AWT or Swing {@code Component} by name, in the hierarchy under the given root.
   *
   * @param root the root used as the starting point of the search.
   * @param name the name of the component to find.
   * @param showing indicates whether the component to find should be visible (or showing) or not.
   * @return the found component.
   * @throws org.assertj.swing.exception.ComponentLookupException if a matching component could not be found.
   * @throws org.assertj.swing.exception.ComponentLookupException if more than one matching component is found.
   * @see #findByName(String)
   */
  @NonNull
  Component findByName(@NonNull Container root, @Nullable String name, boolean showing);

  /**
   * Finds an AWT or Swing {@code Component} by name and type, in the hierarchy under the given root. If this finder is
   * attached to a {@link Robot}, it will use the component lookup scope in the {@code Robot}'s {@link Settings} to
   * determine whether the component to find should be showing or not. If this finder is <em>not</em> attached to any
   * {@code Robot}, the component to find does not have to be showing.
   *
   * @param root the root used as the starting point of the search.
   * @param name the name of the component to find.
   * @param <T> the type of the component to find.
   * @param type the class of the component to find.
   * @return the found component.
   * @throws org.assertj.swing.exception.ComponentLookupException if a matching component could not be found.
   * @throws org.assertj.swing.exception.ComponentLookupException if more than one matching component is found.
   * @see Robot#settings()
   * @see Settings#componentLookupScope()
   * @see ComponentLookupScope
   * @see #findByName(String)
   */
  @NonNull
  <T extends Component> T findByName(@NonNull Container root, @Nullable String name, @NonNull Class<T> type);

  /**
   * Finds an AWT or Swing {@code Component} by name and type, in the hierarchy under the given root.
   *
   * @param root the root used as the starting point of the search.
   * @param name the name of the component to find.
   * @param <T> the type of the component to find.
   * @param type the class of the component to find.
   * @param showing indicates whether the component to find should be visible (or showing) or not.
   * @return the found component.
   * @throws org.assertj.swing.exception.ComponentLookupException if a matching component could not be found.
   * @throws org.assertj.swing.exception.ComponentLookupException if more than one matching component is found.
   * @see #findByName(String)
   */
  @NonNull
  <T extends Component> T findByName(@NonNull Container root, @Nullable String name, @NonNull Class<T> type,
                                     boolean showing);

  /**
   * Finds an AWT or Swing {@code Component} using the given {@link ComponentMatcher}. The given matcher will be
   * evaluated in the event dispatch thread (EDT). Implementations of {@code ComponentMatcher} do not need to be
   * concerned about the event dispatch thread (EDT).
   *
   * @param m the matcher to use to find the component of interest.
   * @return the found component.
   * @throws org.assertj.swing.exception.ComponentLookupException if a matching component could not be found.
   * @throws org.assertj.swing.exception.ComponentLookupException if more than one matching component is found.
   */
  @NonNull
  Component find(@NonNull ComponentMatcher m);

  /**
   * Finds an AWT or Swing {@code Component} using the given {@link GenericTypeMatcher}. The given matcher will be
   * evaluated in the event dispatch thread (EDT). Implementations of {@code GenericTypeMatcher} do not need to be
   * concerned about the event dispatch thread (EDT).
   *
   * @param <T> the type of the component to find.
   * @param m the matcher to use to find the component of interest.
   * @return the found component.
   * @throws org.assertj.swing.exception.ComponentLookupException if a matching component could not be found.
   * @throws org.assertj.swing.exception.ComponentLookupException if more than one matching component is found.
   */
  @NonNull
  <T extends Component> T find(@NonNull GenericTypeMatcher<T> m);

  /**
   * Finds an AWT or Swing {@code Component} using the given {@link GenericTypeMatcher} in the hierarchy under the given
   * root. The given matcher will be evaluated in the event dispatch thread (EDT). Implementations of
   * {@code GenericTypeMatcher} do not need to be concerned about the event dispatch thread (EDT).
   *
   * @param root the root used as the starting point of the search.
   * @param <T> the type of the component to find.
   * @param m the matcher to use to find the component.
   * @return the found component.
   * @throws org.assertj.swing.exception.ComponentLookupException if a matching component could not be found.
   * @throws org.assertj.swing.exception.ComponentLookupException if more than one matching component is found.
   */
  @NonNull
  <T extends Component> T find(@NonNull Container root, @NonNull GenericTypeMatcher<T> m);

  /**
   * Finds an AWT or Swing {@code Component} using the given {@link ComponentMatcher} in the hierarchy under the given
   * root. The given matcher will be evaluated in the event dispatch thread (EDT). Implementations of
   * {@code ComponentMatcher} do not need to be concerned about the event dispatch thread (EDT).
   *
   * @param root the root used as the starting point of the search.
   * @param m the matcher to use to find the component.
   * @return the found component.
   * @throws org.assertj.swing.exception.ComponentLookupException if a matching component could not be found.
   * @throws org.assertj.swing.exception.ComponentLookupException if more than one matching component is found.
   */
  @NonNull
  Component find(@Nullable Container root, @NonNull ComponentMatcher m);

  /**
   * Returns all the AWT or Swing {@code Component}s that match the search criteria specified in the given
   * {@link ComponentMatcher}.
   *
   * @param m the matcher to use to find the component.
   * @return all the {@code Component}s that match the search criteria specified in the given {@code ComponentMatcher};
   *         or an empty collection, if there are no matching components.
   */
  @NonNull
  Collection<Component> findAll(@NonNull ComponentMatcher m);

  /**
   * Returns all the AWT or Swing {@code Component}s under the given root that match the search criteria specified in
   * the given {@link ComponentMatcher}.
   *
   * @param root the root used as the starting point of the search.
   * @param m the matcher to use to find the component.
   * @return all the {@code Component}s under the given root that match the search criteria specified in the given
   *         {@code ComponentMatcher}; or an empty collection, if there are no matching components.
   */
  @NonNull
  Collection<Component> findAll(@NonNull Container root, @NonNull ComponentMatcher m);

  /**
   * Returns all the AWT or Swing {@code Component}s that match the search criteria specified in the given
   * {@link GenericTypeMatcher}.
   *
   * @param <T> the type of the component to find.
   * @param m the matcher to use to find the component.
   * @return all the {@code Component}s that match the search criteria specified in the given {@code GenericTypeMatcher}
   *         ; or an empty collection, if there are no matching components.
   */
  @NonNull
  <T extends Component> Collection<T> findAll(@NonNull GenericTypeMatcher<T> m);

  /**
   * Returns all the AWT or Swing {@code Component}s under the given root that match the search criteria specified in
   * the given {@link GenericTypeMatcher}.
   *
   * @param root the root used as the starting point of the search.
   * @param <T> the type of the component to find.
   * @param m the matcher to use to find the component.
   * @return all the {@code Component}s under the given root that match the search criteria specified in the given
   *         {@code GenericTypeMatcher}; or an empty collection, if there are no matching components.
   */
  @NonNull
  <T extends Component> Collection<T> findAll(@NonNull Container root, @NonNull GenericTypeMatcher<T> m);

  /**
   * Returns whether the message in a {@link org.assertj.swing.exception.ComponentLookupException} should include the
   * current component hierarchy.
   * The default value is {@code true}.
   *
   * @return {@code true} if the component hierarchy is included as part of the
   *         {@code org.assertj.swing.exception.ComponentLookupException} message, {@code false} otherwise.
   */
  boolean includeHierarchyIfComponentNotFound();

  /**
   * Updates whether the message in a {@link org.assertj.swing.exception.ComponentLookupException} should include the
   * current component hierarchy.
   * The default value is {@code true}.
   *
   * @param newValue the new value to set.
   */
  void includeHierarchyIfComponentNotFound(boolean newValue);
}