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
package org.assertj.swing.junit.ant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.image.BufferedImage;

import org.assertj.swing.image.ImageFileWriter;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for
 * <code>{@link ImageHandler#decodeBase64AndSaveAsPng(String, String, ImageDecoder, org.assertj.swing.image.ImageFileWriter)}</code>
 * .
 *
 * @author Alex Ruiz
 */
public class ImageHandler_decodeBase64_withImageDecoderAndImageFileWriter_Test extends ImageHandler_TestCase {

  private ImageDecoder decoder;
  private String encoded;
  private ImageFileWriter writer;
  private BufferedImage image;
  private String path;

  @Before
  public void setUp() {
    encoded = "Hello";
    decoder = mockImageDecoder();
    image = mockImage();
    writer = mock(ImageFileWriter.class);
    path = "somePath";
  }

  @Test
  public void should_Not_Rethrow_Error() throws Throwable {
    when(decoder.decodeBase64(encoded)).thenReturn(image);
    doThrow(thrownOnPurpose()).when(writer).writeAsPng(image, path);
    assertThat(ImageHandler.decodeBase64AndSaveAsPng(encoded, path, decoder, writer)).isEmpty();
  }
}
