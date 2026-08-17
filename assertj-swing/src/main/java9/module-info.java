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
module org.assertj.swing {
  requires transitive java.desktop;
  requires static org.jspecify;

  exports org.assertj.swing.annotation;
  exports org.assertj.swing.assertions;
  exports org.assertj.swing.assertions.data;
  exports org.assertj.swing.assertions.error;
  exports org.assertj.swing.cell;
  exports org.assertj.swing.core;
  exports org.assertj.swing.core.matcher;
  exports org.assertj.swing.data;
  exports org.assertj.swing.edt;
  exports org.assertj.swing.exception;
  exports org.assertj.swing.finder;
  exports org.assertj.swing.fixture;
  exports org.assertj.swing.format;
  exports org.assertj.swing.image;
  exports org.assertj.swing.keystroke;
  exports org.assertj.swing.launcher;
  exports org.assertj.swing.security;
  exports org.assertj.swing.testing;
  exports org.assertj.swing.timing;
  exports org.assertj.swing.util;
}
