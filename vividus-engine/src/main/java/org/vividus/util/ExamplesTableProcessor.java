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

package org.vividus.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jbehave.core.model.ExamplesTable.TableProperties;
import org.jbehave.core.model.ExamplesTableStringBuilder;

public final class ExamplesTableProcessor
{
    private static final String VALUE_SEPARATOR_KEY = "valueSeparator";
    private static final String DEFAULT_SEPARATOR_VALUE = "|";

    private ExamplesTableProcessor()
    {
    }

    public static String buildExamplesTableFromColumns(Collection<String> header, List<List<String>> columnsData,
            TableProperties properties)
    {
        boolean columnsAligned = columnsData
                .stream()
                .mapToInt(List::size)
                .distinct()
                .count() == 1;

        if (columnsAligned)
        {
            List<List<String>> rows = transpose(columnsData);
            return buildExamplesTable(header, rows, properties, true);
        }
        Iterator<String> headerIterator = header.iterator();
        String columnNamesPerValuesNumbers = IntStream.range(0, header.size()).mapToObj(
            i -> String.format("column '%s' has %d value(s)", headerIterator.next(), columnsData.get(i).size()))
            .collect(Collectors.joining(", "));
        throw new IllegalArgumentException("Columns are not aligned: " + columnNamesPerValuesNumbers);
    }

    /**
     * Build a table using input <b>headers</b> and <b>rows</b>
     *
     * @param headers table headers
     * @param rows table rows
     * @param properties table properties
     * @return examples table as string
     */
    public static String buildExamplesTable(Collection<String> headers, Collection<List<String>> rows,
            TableProperties properties)
    {
        return buildExamplesTable(headers, rows, properties, false, false);
    }

    public static String buildExamplesTable(Collection<String> header, List<List<String>> data,
            TableProperties properties, boolean checkForValueSeparator)
    {
        return buildExamplesTable(header, data, properties, checkForValueSeparator, false);
    }

    public static String buildExamplesTable(Collection<String> header, Collection<List<String>> data,
            TableProperties properties, boolean checkForValueSeparator, boolean appendTableProperties)
    {
        String valueSeparator = checkForValueSeparator ? determineValueSeparator(data, properties)
                : properties.getValueSeparator();
        properties.getProperties().setProperty(VALUE_SEPARATOR_KEY, valueSeparator);
        StringBuilder examplesTableBuilder = new StringBuilder();
        if (appendTableProperties)
        {
            appendTableProperties(examplesTableBuilder, properties, properties.getRowSeparator());
        }
        String stringExampleTable = ExamplesTableStringBuilder.buildExamplesTableString(properties, List.copyOf(header),
                List.copyOf(data)).trim();
        return examplesTableBuilder.length() > 0 ? examplesTableBuilder.append(stringExampleTable).toString()
                : stringExampleTable;
    }

    private static void appendTableProperties(StringBuilder examplesTableBuilder, TableProperties properties,
            String rowSeparator)
    {
        StringBuilder propertiesStringBuilder = new StringBuilder();
        appendIfNonDefaultSeparator(propertiesStringBuilder, properties, properties.getHeaderSeparator(),
                "headerSeparator");
        appendIfNonDefaultSeparator(propertiesStringBuilder, properties, properties.getValueSeparator(),
                VALUE_SEPARATOR_KEY);
        int propertiesStringLength = propertiesStringBuilder.length();
        if (propertiesStringLength > 0)
        {
            String propertiesString = propertiesStringBuilder.substring(0, propertiesStringLength - 1);
            examplesTableBuilder.append('{').append(propertiesString).append('}').append(rowSeparator);
        }
    }

    private static void appendIfNonDefaultSeparator(StringBuilder propertiesStringBuilder, TableProperties properties,
            String separator, String key)
    {
        if (!DEFAULT_SEPARATOR_VALUE.equals(separator))
        {
            properties.getProperties().setProperty(key, separator);
            propertiesStringBuilder.append(key).append('=').append(separator).append(',');
        }
    }

    private static String determineValueSeparator(Collection<List<String>> data, TableProperties properties)
    {
        List<String> valueSeparators = List.of(properties.getValueSeparator(), DEFAULT_SEPARATOR_VALUE,
                "!", "?", "$", "#", "%", "*");
        for (String separator : valueSeparators)
        {
            if (data.stream().flatMap(List::stream).noneMatch(s -> s != null && s.contains(separator)))
            {
                properties.getProperties().setProperty(VALUE_SEPARATOR_KEY, separator);
                return separator;
            }
        }
        throw new IllegalArgumentException("There are not alternative value separators applicable for examples table");
    }

    private static List<List<String>> transpose(Collection<List<String>> columnsData)
    {
        return IntStream.range(0, columnsData.iterator().next().size())
                .mapToObj(index -> buildRow(index, columnsData))
                .collect(Collectors.toList());
    }

    private static List<String> buildRow(int rowIndex, Collection<List<String>> columnsData)
    {
        return columnsData.stream().map(l -> l.get(rowIndex)).collect(Collectors.toList());
    }
}
