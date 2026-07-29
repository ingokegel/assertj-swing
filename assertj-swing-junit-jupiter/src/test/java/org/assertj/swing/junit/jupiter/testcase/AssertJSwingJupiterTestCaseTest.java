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
 *
 */
package org.assertj.swing.junit.jupiter.testcase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import java.awt.FlowLayout;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.Robot;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.jupiter.extension.RobotExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.testkit.engine.EngineTestKit;

@GUITest
class AssertJSwingJupiterTestCaseTest {

    @Test
    void manages_robot_and_calls_lifecycle_methods() {
        AtomicBoolean setUpCalled = new AtomicBoolean();
        SmokeTest.setUpCalled = setUpCalled;
        EngineTestKit.engine("junit-jupiter")
                .selectors(selectClass(SmokeTest.class))
                .execute()
                .testEvents()
                .assertStatistics(stats -> stats.started(1).succeeded(1));
        assertThat(setUpCalled).isTrue();
    }

    @Test
    void extension_provides_robot_without_base_class() {
        AtomicBoolean robotInjected = new AtomicBoolean();
        ExtensionSmokeTest.robotInjected = robotInjected;
        EngineTestKit.engine("junit-jupiter")
                .selectors(selectClass(ExtensionSmokeTest.class))
                .execute()
                .testEvents()
                .assertStatistics(stats -> stats.started(1).succeeded(1));
        assertThat(robotInjected).isTrue();
    }

    static class SmokeTest extends AssertJSwingJupiterTestCase {
        private static AtomicBoolean setUpCalled;
        private JFrame frame;

        @Override
        protected void onSetUp() {
            setUpCalled.set(true);
            frame = GuiActionRunner.execute(() -> {
                JFrame newFrame = new JFrame("smoke");
                newFrame.setLayout(new FlowLayout());
                JButton button = new JButton("click me");
                button.setName("button");
                newFrame.add(button);
                newFrame.pack();
                newFrame.setVisible(true);
                return newFrame;
            });
        }

        @Override
        protected void onTearDown() {
            GuiActionRunner.execute(() -> frame.dispose());
        }

        @Test
        void finds_frame_and_clicks_button() {
            FrameFixture fixture = new FrameFixture(robot(), frame);
            fixture.button("button").click();
            fixture.requireVisible();
        }
    }

    @ExtendWith(RobotExtension.class)
    static class ExtensionSmokeTest {
        private static AtomicBoolean robotInjected;
        private JFrame frame;

        @BeforeEach
        void showFrame(Robot robot) {
            robotInjected.set(robot != null);
            frame = GuiActionRunner.execute(() -> {
                JFrame newFrame = new JFrame();
                newFrame.setName("extension smoke");
                newFrame.pack();
                newFrame.setVisible(true);
                return newFrame;
            });
        }

        @AfterEach
        void disposeFrame() {
            GuiActionRunner.execute(() -> frame.dispose());
        }

        @Test
        void finds_frame(Robot robot) {
            assertThat(WindowFinder.findFrame("extension smoke").using(robot).target()).isSameAs(frame);
        }
    }
}
