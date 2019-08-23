/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.vividus.testcontext;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContextCopyingExecutorTests
{

    private static final String CONTEXT_VALUE = "contextValue";
    @InjectMocks
    private ContextCopyingExecutor contexCopyingExecutor;

    @Test
    void shouldCopyContextBeforeExecution() throws InterruptedException, ExecutionException
    {
        TestContext context = new ThreadedTestContext();
        context.put(ContextCopyingExecutorTests.class, CONTEXT_VALUE);
        contexCopyingExecutor.setTestContext(context);
        contexCopyingExecutor.execute(() -> {
            assertEquals(CONTEXT_VALUE, context.get(ContextCopyingExecutorTests.class));
        }, (t, e) -> { });
    }
}
