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
package org.assertj.swing.keystroke;

import org.assertj.swing.annotation.VisibleForTesting;
import org.assertj.swing.logging.Logger;
import org.assertj.swing.util.OSFamily;

import org.jspecify.annotations.NonNull;
import java.util.Locale;

import static org.assertj.swing.keystroke.KeyStrokeMappingProviderNames.generateNamesFrom;
import static org.assertj.swing.util.Preconditions.checkNotNull;

/**
 * Chooses a {@link KeyStrokeMappingProvider} based on OS family and locale.
 *
 * @author Alex Ruiz
 */
class KeyStrokeMappingProviderPicker {
  private static Logger LOGGER = Logger.getLogger(KeyStrokeMappingProviderPicker.class.getName());
  private final KeyStrokeMappingProviderFactory factory;

  KeyStrokeMappingProviderPicker() {
    this(new KeyStrokeMappingProviderFactory());
  }

  @VisibleForTesting
  KeyStrokeMappingProviderPicker(@NonNull KeyStrokeMappingProviderFactory factory) {
    this.factory = factory;
  }

  KeyStrokeMappingProvider providerFor(@NonNull OSFamily osFamily, @NonNull Locale locale) {
    LOGGER.finer("providing keystroke mappings for OS=" + osFamily + ", locale=" + locale);
    for (String name : generateNamesFrom(osFamily, locale)) {
      LOGGER.finer("trying >" + name + "<");
      String typeName = checkNotNull(name);
      KeyStrokeMappingProvider provider = factory.createProvider(typeName);
      if (provider != null) {
        LOGGER.finer("created successfully.");
        return provider;
      }
    }
    return new KeyStrokeMappingProvider_en();
  }
}
