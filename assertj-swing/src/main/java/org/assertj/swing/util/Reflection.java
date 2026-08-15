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
package org.assertj.swing.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Utility methods for reflective access to members of the JDK and of GUI components, replacement for the removed
 * {@code fest-reflect} library.
 *
 * @author Alex Ruiz
 */
public final class Reflection {

  /**
   * Invokes the method with the given name and parameter types on the given target. The method is looked up in the
   * class of the target and its superclasses and is made accessible.
   *
   * @param target the object to invoke the method on.
   * @param name the name of the method to invoke.
   * @param parameterTypes the parameter types of the method to invoke, may be empty.
   * @param args the arguments to pass to the method.
   * @return the result of the invocation, boxed if primitive.
   * @throws IllegalStateException if the method cannot be found or invoked.
   * @throws RuntimeException if the invoked method throws a {@code RuntimeException}, propagated unchanged.
   */
  public static @Nullable Object invokeMethod(@NonNull Object target, @NonNull String name,
      @NonNull Class<?>[] parameterTypes, Object... args) {
    Method method = findMethod(target.getClass(), name, parameterTypes);
    return invoke(method, target, args);
  }

  /**
   * Invokes the static method with the given name and parameter types in the given type. The method is looked up in
   * the given type and its superclasses and is made accessible.
   *
   * @param type the type declaring the static method to invoke.
   * @param name the name of the method to invoke.
   * @param parameterTypes the parameter types of the method to invoke, may be empty.
   * @param args the arguments to pass to the method.
   * @return the result of the invocation, boxed if primitive.
   * @throws IllegalStateException if the method cannot be found or invoked.
   * @throws RuntimeException if the invoked method throws a {@code RuntimeException}, propagated unchanged.
   */
  public static @Nullable Object invokeStaticMethod(@NonNull Class<?> type, @NonNull String name,
      @NonNull Class<?>[] parameterTypes, Object... args) {
    Method method = findMethod(type, name, parameterTypes);
    return invoke(method, null, args);
  }

  /**
   * Returns the value of the field with the given name in the given target. The field is looked up in the class of
   * the target and its superclasses and is made accessible.
   *
   * @param target the object holding the field.
   * @param name the name of the field to read.
   * @return the value of the field, boxed if primitive.
   * @throws IllegalStateException if the field cannot be found or read.
   */
  public static @Nullable Object fieldValue(@NonNull Object target, @NonNull String name) {
    try {
      return findField(target.getClass(), name).get(target);
    } catch (ReflectiveOperationException | RuntimeException e) {
      throw new IllegalStateException(String.format("Unable to read field '%s' in %s", name, target.getClass()
          .getName()), e);
    }
  }

  /**
   * Returns the value of the static field with the given name in the given type. The field is looked up in the given
   * type and its superclasses and is made accessible.
   *
   * @param type the type declaring the static field.
   * @param name the name of the field to read.
   * @return the value of the field, boxed if primitive.
   * @throws IllegalStateException if the field cannot be found or read.
   */
  public static @Nullable Object staticFieldValue(@NonNull Class<?> type, @NonNull String name) {
    try {
      return findField(type, name).get(null);
    } catch (ReflectiveOperationException | RuntimeException e) {
      throw new IllegalStateException(String.format("Unable to read static field '%s' in %s", name, type.getName()),
                                       e);
    }
  }

  /**
   * Indicates whether the given type has a default constructor.
   *
   * @param type the type to check.
   * @return {@code true} if the given type has a default constructor, {@code false} otherwise.
   */
  public static boolean hasDefaultConstructor(@NonNull Class<?> type) {
    try {
      type.getDeclaredConstructor();
      return true;
    } catch (ReflectiveOperationException | RuntimeException e) {
      return false;
    }
  }

  private static @NonNull Method findMethod(@NonNull Class<?> type, @NonNull String name,
      @NonNull Class<?>[] parameterTypes) {
    for (Class<?> current = type; current != null; current = current.getSuperclass()) {
      try {
        Method method = current.getDeclaredMethod(name, parameterTypes);
        method.setAccessible(true);
        return method;
      } catch (NoSuchMethodException ignored) {
        // continue with superclass
      }
    }
    throw new IllegalStateException(String.format("Unable to find method '%s' in %s", name, type.getName()));
  }

  private static @NonNull Field findField(@NonNull Class<?> type, @NonNull String name) throws NoSuchFieldException {
    for (Class<?> current = type; current != null; current = current.getSuperclass()) {
      try {
        Field field = current.getDeclaredField(name);
        field.setAccessible(true);
        return field;
      } catch (NoSuchFieldException ignored) {
        // continue with superclass
      }
    }
    throw new NoSuchFieldException(name);
  }

  private static @Nullable Object invoke(@NonNull Method method, @Nullable Object target, Object... args) {
    try {
      return method.invoke(target, args);
    } catch (InvocationTargetException e) {
      Throwable thrown = e.getTargetException();
      if (thrown instanceof RuntimeException)
        throw (RuntimeException) thrown;
      if (thrown instanceof Error)
        throw (Error) thrown;
      throw new IllegalStateException(String.format("Unable to invoke method '%s'", method.getName()), thrown);
    } catch (ReflectiveOperationException | RuntimeException e) {
      throw new IllegalStateException(String.format("Unable to invoke method '%s'", method.getName()), e);
    }
  }

  private Reflection() {
  }
}
