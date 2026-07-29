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
/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.assertj.swing.junit.jupiter.screenshot;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.assertj.swing.image.ScreenshotTakerIF;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;

class ScreenshotOnFailureExtensionTest {

    @Test
    void saves_screenshot_with_name_derived_from_test_class_and_method() throws Exception {
        AtomicReference<String> savedPath = new AtomicReference<>();
        ScreenshotOnFailureExtension extension = createExtension(savedPath);
        File outputDir = Files.createTempDirectory("screenshot-test").toFile();
        System.setProperty(ScreenshotOnFailureExtension.SCREENSHOT_DIR_PROPERTY, outputDir.getAbsolutePath());
        try {
            extension.testFailed(extensionContext("someMethod"), new AssertionError("failing on purpose"));
            String path = savedPath.get();
            assertThat(path).startsWith(outputDir.getAbsolutePath() + File.separator);
            assertThat(new File(path).getName())
                    .matches(ScreenshotOnFailureExtensionTest.class.getName().replace(".", "\\.")
                            + "\\.unusedMethod_[0-9a-f]+\\.png");
        } finally {
            System.clearProperty(ScreenshotOnFailureExtension.SCREENSHOT_DIR_PROPERTY);
        }
    }

    private static @NonNull ScreenshotOnFailureExtension createExtension(AtomicReference<String> savedPath) {
        ScreenshotTakerIF screenshotTaker = new ScreenshotTakerIF() {
            @Override
            public void saveDesktopAsPng(String imageFilePath) {
                savedPath.set(imageFilePath);
            }

            @Override
            public java.awt.image.BufferedImage takeDesktopScreenshot() {
                return null;
            }

            @Override
            public void saveComponentAsPng(java.awt.Component c, String imageFilePath) {
            }

            @Override
            public java.awt.image.BufferedImage takeScreenshotOf(java.awt.Component c) {
                return null;
            }

            @Override
            public void saveImage(java.awt.image.BufferedImage image, String filePath) {
            }
        };
        return new ScreenshotOnFailureExtension(screenshotTaker);
    }

    private static ExtensionContext extensionContext(String methodName) {
        return (ExtensionContext) Proxy.newProxyInstance(ScreenshotOnFailureExtensionTest.class.getClassLoader(),
                new Class<?>[]{ExtensionContext.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getRequiredTestClass" -> ScreenshotOnFailureExtensionTest.class;
                    case "getTestClass" -> Optional.of(ScreenshotOnFailureExtensionTest.class);
                    case "getTestMethod" -> Optional.of(
                            ScreenshotOnFailureExtensionTest.class.getDeclaredMethod("unusedMethod"));
                    case "getUniqueId" -> "[engine:test]/[class:stub]/[method:" + methodName + "()]";
                    case "getDisplayName" -> methodName;
                    default -> defaultValue(method.getReturnType());
                });
    }

    @SuppressWarnings("unused")
    private void unusedMethod() {
    }

    private static Object defaultValue(Class<?> type) {
        if (type == boolean.class) return false;
        if (type == Optional.class) return Optional.empty();
        return null;
    }
}
