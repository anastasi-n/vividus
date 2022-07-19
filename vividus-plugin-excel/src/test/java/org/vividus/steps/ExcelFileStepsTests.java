/*
 * Copyright 2019-2022 the original author or authors.
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

package org.vividus.steps;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jbehave.core.model.ExamplesTable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vividus.context.VariableContext;
import org.vividus.variable.VariableScope;

@ExtendWith(MockitoExtension.class)
class ExcelFileStepsTests
{
    private static final Set<VariableScope> SCOPES = Set.of(VariableScope.SCENARIO);
    private static final String PATH = "path";
    private static final ExamplesTable CONTENT = new ExamplesTable("|k1|\n|v1|");

    @Mock private VariableContext variableContext;

    @InjectMocks private ExcelFileSteps fileSteps;

    @Test
    void shouldCreateExcelFileContainigSheetWithContent() throws IOException
    {
        fileSteps.createExcelFileContainigSheetWithContent(CONTENT, SCOPES, PATH);
        verifySheet("Sheet0");
    }

    @Test
    void shouldCreateExcelFileContainigSheetWithNameAndContent() throws IOException
    {
        String sheetName = "my sheet name";
        fileSteps.createExcelFileContainigSheetWithNameAndContent(sheetName, CONTENT, SCOPES, PATH);
        verifySheet(sheetName);
    }

    private void verifySheet(String sheetName)
    {
        verify(variableContext).putVariable(eq(SCOPES), eq(PATH), argThat(path ->
        {
            try (XSSFWorkbook myExcelBook = new XSSFWorkbook(FileUtils.openInputStream(new File(path.toString()))))
            {
                XSSFSheet myExcelSheet = myExcelBook.getSheetAt(0);
                String header = myExcelSheet.getRow(0).getCell(0).getStringCellValue();
                String values = header + myExcelSheet.getRow(1).getCell(0).getStringCellValue();
                return "k1v1".equals(values) && myExcelSheet.getSheetName().equals(sheetName);
            }
            catch (IOException e)
            {
                throw new UncheckedIOException(e);
            }
        }));
    }
}
