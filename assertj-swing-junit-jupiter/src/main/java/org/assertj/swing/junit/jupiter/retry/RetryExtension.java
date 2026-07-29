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

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.junit.jupiter.api.extension.TestWatcher;
import org.junit.platform.commons.support.AnnotationSupport;
import org.opentest4j.TestAbortedException;

/**
 * Understands the <code>{@link TestTemplateInvocationContextProvider}</code> behind
 * <code>{@link RetryOnFailure}</code>. Registered automatically by the annotation, direct use of this class is
 * normally not necessary.
 */
public class RetryExtension implements TestTemplateInvocationContextProvider {

    private static final Namespace NAMESPACE = Namespace.create(RetryExtension.class);
    private static final String PASSED_KEY = "passed";

    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return context.getTestMethod()
                .map(method -> AnnotationSupport.isAnnotated(method, RetryOnFailure.class))
                .orElse(false);
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
        RetryOnFailure retry = AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), RetryOnFailure.class)
                .orElseThrow(() -> new IllegalStateException("No @RetryOnFailure"));
        int maxAttempts = Math.max(1, retry.maxAttempts());
        return IntStream.rangeClosed(1, maxAttempts)
                .mapToObj(attempt -> new RetryInvocationContext(attempt, maxAttempts));
    }

    private static class RetryInvocationContext implements TestTemplateInvocationContext {
        private final int attempt;
        private final int maxAttempts;

        RetryInvocationContext(int attempt, int maxAttempts) {
            this.attempt = attempt;
            this.maxAttempts = maxAttempts;
        }

        @Override
        public String getDisplayName(int invocationIndex) {
            return attempt == 1 ? "attempt 1" : "retry " + (attempt - 1) + " of " + (maxAttempts - 1);
        }

        @Override
        public List<Extension> getAdditionalExtensions() {
            return Arrays.asList(new SkipAfterPassCondition(), new RetryAttemptHandler());
        }

        /**
         * Skips further attempts once a previous attempt has passed.
         */
        private static class SkipAfterPassCondition implements ExecutionCondition {
            @Override
            public ConditionEvaluationResult evaluateExecutionCondition(@NonNull ExtensionContext context) {
                if (hasPassed(context)) {
                    return ConditionEvaluationResult.disabled("Already passed in a previous attempt");
                }
                return ConditionEvaluationResult.enabled(null);
            }

            private static boolean hasPassed(ExtensionContext context) {
                return context.getParent()
                        .map(parent -> parent.getStore(NAMESPACE).get(PASSED_KEY, Boolean.class))
                        .orElse(false);
            }
        }

        private class RetryAttemptHandler implements TestExecutionExceptionHandler, TestWatcher {

            @Override
            public void testSuccessful(ExtensionContext context) {
                context.getParent()
                        .ifPresent(parent -> parent.getStore(NAMESPACE).put(PASSED_KEY, true));
            }

            @Override
            public void handleTestExecutionException(@NonNull ExtensionContext context, @NonNull Throwable throwable) throws Throwable {
                if (attempt < maxAttempts) {
                    throw new TestAbortedException("Attempt " + attempt + " of " + maxAttempts + " failed", throwable);
                }
                throw throwable;
            }
        }
    }
}
