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
package org.assertj.swing.junit.jupiter.extension;

import java.lang.reflect.Parameter;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.Robot;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * Understands a JUnit Jupiter extension that manages a <code>{@link Robot}</code> for each test without requiring the
 * test class to extend <code>{@link org.assertj.swing.junit.jupiter.testcase.AssertJSwingJupiterTestCase}</code>.
 * Use it when inheritance is not an option, for example with <code>@ParameterizedTest</code> or
 * <code>@TestTemplate</code>-based tests:
 *
 * <pre>
 * &#64;ExtendWith(RobotExtension.class)
 * class MyGuiTest {
 *
 *   &#64;Test
 *   void shouldClickButton(Robot robot) {
 *     // use robot
 *   }
 * }
 * </pre>
 *
 * <p>
 * The extension installs a <code>{@link FailOnThreadViolationRepaintManager}</code> for the duration of the test
 * class, creates a new <code>{@link Robot}</code> before each test and cleans it up afterward. The
 * <code>{@link Robot}</code> can be injected into <code>@BeforeEach</code>, test and <code>@AfterEach</code> method
 * parameters, or obtained with <code>{@link #robot(ExtensionContext)}</code>.
 * </p>
 */
public class RobotExtension implements BeforeAllCallback, BeforeEachCallback, AfterEachCallback, AfterAllCallback,
        ParameterResolver {

    private static final Namespace NAMESPACE = Namespace.create(RobotExtension.class);

    @Override
    public void beforeAll(@NonNull ExtensionContext context) {
        FailOnThreadViolationRepaintManager.install();
    }

    @Override
    public void afterAll(@NonNull ExtensionContext context) {
        FailOnThreadViolationRepaintManager.uninstall();
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        context.getStore(NAMESPACE).put(Robot.class, BasicRobot.robotWithNewAwtHierarchy());
    }

    @Override
    public void afterEach(ExtensionContext context) {
        Robot robot = context.getStore(NAMESPACE).remove(Robot.class, Robot.class);
        if (robot != null) {
            robot.cleanUp();
        }
    }

    /**
     * @param context the current extension context
     * @return the <code>{@link Robot}</code> of the current test
     */
    public static Robot robot(ExtensionContext context) {
        return context.getStore(NAMESPACE).get(Robot.class, Robot.class);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, @NonNull ExtensionContext extensionContext) {
        Parameter parameter = parameterContext.getParameter();
        return parameter.getType() == Robot.class;
    }

    @Override
    public Object resolveParameter(@NonNull ParameterContext parameterContext, @NonNull ExtensionContext extensionContext) {
        return robot(extensionContext);
    }
}
