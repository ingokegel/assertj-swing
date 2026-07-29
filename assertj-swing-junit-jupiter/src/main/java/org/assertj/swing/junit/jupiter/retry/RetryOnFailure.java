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
package org.assertj.swing.junit.jupiter.retry;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Understands a test annotation for flaky GUI tests that should be executed again when they fail. It is the JUnit
 * Jupiter counterpart of TestNG's <code>IRetryAnalyzer</code>.
 * <p>
 * The annotated method is executed up to <code>{@link #maxAttempts()}</code> times until it succeeds. Failed attempts
 * except the last one are reported as aborted, so the overall result is "failed" only if all attempts fail. Each
 * attempt runs the full lifecycle, that is all <code>@BeforeEach</code> and <code>@AfterEach</code> methods, with a
 * fresh test fixture.
 * </p>
 * <p>
 * Because this annotation is based on <code>{@link TestTemplate}</code>, it cannot be combined with
 * <code>@Test</code> or <code>@ParameterizedTest</code> on the same method. It can be used as a meta-annotation to
 * create composed annotations.
 * </p>
 */
@Target({METHOD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Documented
@TestTemplate
@ExtendWith(RetryExtension.class)
public @interface RetryOnFailure {

    /**
     * @return the maximum number of times the test is executed, including the first attempt
     */
    int maxAttempts() default 3;
}
