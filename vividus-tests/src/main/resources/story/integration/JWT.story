Description: Tests to demonstrate working with JSON Web Tokens (JWTs)

Meta:
    @epic vividus-examples
    @feature JWT

Scenario: Decode/encode JWTs, validate extracked jsons
Given I initialize scenario variable `JWT` with value `#{loadResource(JWT.txt)}`
Given I initialize scenario variable `header` with value `#{decodeFromBase64(#{eval(`${JWT}`.replaceFirst("([^.]+).*", "$1"))})}`
Given I initialize scenario variable `payload` with value `#{decodeFromBase64(#{eval(`${JWT}`.replaceFirst(".*(?<=\.)(.*?)(?=\.).*", "$1"))})}`
Given I initialize scenario variable `encodedSignature` with value `#{eval(`${JWT}`.replaceFirst(".*(?<=\.)([^.]+)$", "$1"))}`
When I save JSON element from `${header}` by JSON path `alg` to scenario variable `alg`
Then `#{removeWrappingDoubleQuotes(${alg})}` is = `HS256`
Then number of JSON elements from `${header}` by JSON path `typ` is = 1
Then JSON element from `${payload}` by JSON path `sub` is equal to `"1234567890"`TREATING_NULL_AS_ABSENT
Then number of JSON elements from `${payload}` by JSON path `name` is = 1
Then `#{encodeToBase64(${header})}.#{eval(`#{encodeToBase64(${payload})}`.replaceFirst("==", ""))}.${encodedSignature}` is = `${JWT}`
