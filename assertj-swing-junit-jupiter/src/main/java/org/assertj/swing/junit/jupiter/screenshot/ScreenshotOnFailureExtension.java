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

import static java.io.File.separator;
import static java.util.logging.Level.SEVERE;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.util.logging.Logger;

import org.assertj.swing.image.NoopScreenshotTaker;
import org.assertj.swing.image.ScreenshotTaker;
import org.assertj.swing.image.ScreenshotTakerIF;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

/**
 * Understands a JUnit Jupiter <code>{@link TestWatcher}</code> that takes a screenshot of the desktop when a test
 * fails.
 *
 * <pre>
 * &#64;ExtendWith(ScreenshotOnFailureExtension.class)
 * class MyGuiTest {
 *   ...
 * }
 * </pre>
 *
 * <p>
 * Screenshots are saved as PNG files named after the test class and method. The output directory is taken from the
 * system property <code>{@link #SCREENSHOT_DIR_PROPERTY}</code> and defaults to
 * <code>target/failed-gui-test-screenshots</code>. In a headless environment no screenshots are taken.
 * </p>
 */
public class ScreenshotOnFailureExtension implements TestWatcher {

    /**
     * The name of the system property that specifies the directory where failure screenshots are saved.
     */
    public static final String SCREENSHOT_DIR_PROPERTY = "assertj.swing.screenshot.dir";

    private static final String DEFAULT_OUTPUT_DIR = "target" + separator + "failed-gui-test-screenshots";

    private static final Logger logger = Logger.getAnonymousLogger();

    private final ScreenshotTakerIF screenshotTaker;

    /**
     * Creates a new <code>{@link ScreenshotOnFailureExtension}</code>.
     */
    public ScreenshotOnFailureExtension() {
        this(GraphicsEnvironment.isHeadless() ? new NoopScreenshotTaker() : new ScreenshotTaker());
    }

    ScreenshotOnFailureExtension(ScreenshotTakerIF screenshotTaker) {
        this.screenshotTaker = screenshotTaker;
    }

    /**
     * When a test fails, this method takes a screenshot of the desktop and saves it in the output directory.
     *
     * @param context the current extension context
     * @param cause   the throwable that caused the test failure
     */
    @Override
    public void testFailed(@NonNull ExtensionContext context, Throwable cause) {
        String imagePath = outputDirectory() + separator + screenshotFileName(context);
        try {
            new File(outputDirectory()).mkdirs();
            screenshotTaker.saveDesktopAsPng(imagePath);
            logger.info("Screenshot of desktop saved as: " + imagePath);
        } catch (RuntimeException e) {
            logger.log(SEVERE, "Unable to take screenshot of failed test", e);
        }
    }

    static String outputDirectory() {
        return System.getProperty(SCREENSHOT_DIR_PROPERTY, DEFAULT_OUTPUT_DIR);
    }

    static String screenshotFileName(ExtensionContext context) {
        String methodName = context.getTestMethod().map(method -> "." + method.getName()).orElse("");
        String base = context.getRequiredTestClass().getName() + methodName;
        String uniqueSuffix = "_" + Integer.toHexString(context.getUniqueId().hashCode());
        return base.replaceAll("[^A-Za-z0-9._-]", "_") + uniqueSuffix + ".png";
    }
}
