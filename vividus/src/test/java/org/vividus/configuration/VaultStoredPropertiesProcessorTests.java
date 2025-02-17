/*
 * Copyright 2019-2023 the original author or authors.
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

package org.vividus.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.Properties;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class VaultStoredPropertiesProcessorTests
{
    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "/",
            "engine/",
            "engine/path",
            "engine/path/",
            "/engine/path/",
            "engine//path",
            "/engine/path/key"
    })
    void shouldRejectInvalidSecretPaths(String fullSecretPath) throws IOException
    {
        try (var processor = new VaultStoredPropertiesProcessor(new Properties()))
        {
            var propertyName = "invalid-property";
            var exception = assertThrows(IllegalArgumentException.class,
                    () -> processor.processValue(propertyName, fullSecretPath));
            assertEquals(
                    "Full secret path must follow pattern 'engine/path_with_separators/key', but '" + fullSecretPath
                            + "' was found in property '" + propertyName + "'", exception.getMessage());
        }
    }
}
