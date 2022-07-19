Meta:
    @epic vividus-plugin-excel

Lifecycle:
Before:
Scope: STORY
When I issue a HTTP GET request for a resource with the URL 'https://github.com/vividus-framework/vividus/blob/master/vividus-plugin-excel/src/test/resources/TestTemplate.xlsx?raw=true'
Then the response code is = '200'

Scenario: Step verification 'Then response contains excel sheet with index `$index` and records:$records'
!-- Deprecated
Then response contains excel sheet with index `0` and records:
{valueSeparator=!}
|cellsRange|valueRegex             |
!A4:B5     !(Product|Price)\d+\s*  !
!B3        !Price                  !
!C1:C5     !                       !

Scenario: Step verification 'Then response contains excel sheet with name `$name` and records:$records'
!-- Deprecated
Then response contains excel sheet with name `Mapping` and records:
{valueSeparator=!}
|cellsRange|valueRegex             |
!A4:B5     !(Product|Price)\d+\s*  !
!B3        !Price                  !
!C1:C5     !                       !

Scenario: Step verification 'Then `$excelDocument` contains excel sheet with index `$index` and records:$records'
Then `${response-as-bytes}` contains excel sheet with index `0` and records:
{valueSeparator=!}
|cellsRange|valueRegex             |
!A4:B5     !(Product|Price)\d+\s*  !
!B3        !Price                  !
!C1:C5     !                       !

Scenario: Step verification 'Then `$excelDocument` contains excel sheet with name `$name` and records:$records'
Then `${response-as-bytes}` contains excel sheet with name `Mapping` and records:
{valueSeparator=!}
|cellsRange|valueRegex             |
!A4:B5     !(Product|Price)\d+\s*  !
!B3        !Price                  !
!C1:C5     !                       !

Scenario: Verification of step 'When I create temporary excel file with content:$content and put path to $scopes variable `$variableName`' and transformer FROM_EXCEL
Meta:
    @requirementId 1028, 2953
    @feature table-transformers
When I create temporary excel file with content:
|key1  |key2  |
|value1|value2|
and put path to scenario variable `path`
When I initialize the scenario variable `fileExists` with value `#{evalGroovy(return path.toFile().exists())}`
Then `${fileExists}` is = `true`
When I initialize scenario variable `excel-data` with values:
|key1  |key2  |
|value1|value2|
Then `${excel-data}` is equal to table:
{transformer=FROM_EXCEL, path=$\{path\}, sheet=Sheet0, range=A1:B2}

Scenario: Step verification 'When I create temporary excel file containing sheet with name `$sheetName` and content:$content and put its path to $scopes variable `$variableName`'
Meta:
    @requirementId 2953
When I create temporary excel file containing sheet with name `my-sheet-name` and content:
|key1  |key2  |
|value1|value2|
and put its path to scenario variable `path`
When I initialize scenario variable `excel-data` with values:
|key1  |key2  |
|value1|value2|
Then `${excel-data}` is equal to table:
{transformer=FROM_EXCEL, path=$\{path\}, sheet=my-sheet-name, range=A1:B2}
