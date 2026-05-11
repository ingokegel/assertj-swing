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
package org.assertj.swing.junit.runner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Files.currentFolder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for <code>{@link ImageFolderCreator#createImageFolder()}</code>.
 *
 * @author Alex Ruiz
 */
public class ImageFolderCreator_createImageFolder_Test {

  private FolderCreator folderCreator;
  private ImageFolderCreator imageFolderCreator;

  @Before
  public void setUp() {
    folderCreator = mock(FolderCreator.class);
    imageFolderCreator = new ImageFolderCreator(folderCreator);
  }

  @Test
  public void should_Create_Image_Folder() {
    final File createdFolder = new File("fake");
    when(folderCreator.createFolder(currentFolder(), "failed-gui-tests", true)).thenReturn(createdFolder);
    assertThat(imageFolderCreator.createImageFolder()).isSameAs(createdFolder);
  }
}
