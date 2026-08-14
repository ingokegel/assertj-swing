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

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;

/**
 * Utility methods related to {@code File}s.
 *
 * @author Yvonne Wang
 * @author Alex Ruiz
 */
public final class Files {

  /**
   * Creates a new file under the current working folder.
   *
   * @param name the name of the file to create.
   * @return the created file.
   * @throws IOException if an I/O error occurs.
   */
  public static @Nonnull File newFile(@Nonnull String name) throws IOException {
    File file = new File(name);
    file.createNewFile();
    return file;
  }

  /**
   * Deletes the given file. Directories are deleted recursively.
   *
   * @param file the file to delete.
   */
  public static void delete(@Nonnull File file) {
    if (file.isDirectory()) {
      File[] contents = file.listFiles();
      if (contents != null)
        for (File f : contents)
          delete(f);
    }
    file.delete();
  }

  /**
   * Returns the current working folder.
   *
   * @return the current working folder.
   */
  public static @Nonnull File currentFolder() {
    try {
      return new File(".").getCanonicalFile();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to get the current folder", e);
    }
  }

  private Files() {
  }
}
