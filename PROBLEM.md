# Exercise: Political speech
## Scenario
Implementation of a feature in Kotlin with subsequent code review and productive deployment.
## Goal
Processing statistics about political speeches.
Evaluation takes place on the basis of a fictitious code review by teams members and the same quality requirements for code quality, test coverage,
understandability as for production code.
The code should be simple and target-oriented.
Have fun !
### Input
CSV files (UTF-8 encoding) corresponding to the following schema:
`Speaker ; Topic ; Date ; Words`
It should be possible to start a HTTP server with maven or gradle, which returns 1 or more URLs as query parameters under the
GET route `/evaluation?url1=url1&url2=url2`
The CSV files located at these URLs are evaluated and, if the input is valid, the following questions should be answered:
- Which politician gave the most speeches in 2013?
- Which politician gave the most speeches on "homeland security"?
- Which politician spoke the fewest words overall?
The output should be as JSON in this format:
```
{
"mostSpeeches": string|null,
"mostSecurity": string|null,
"leastWordy": string|null
}
```
If no or no unique answer is possible for a question, this field should be filled with null.

#### Example

#### CSV-Content:
```
Speaker;Topic;Date;Words
Alexander Abel; education policty; 2012-10-30, 5310
Bernhard Belling; coal subsidies; 2012-11-05; 1210
Caesare Collins; coal subsidies; 2012-11-06; 1119
Alexander Abel; homeland security; 2012-12-11; 911
```

### Response:
```
Status: 200
{
"mostSpeeches": null,
"mostSecurity": "Alexander Abel",
"leastWordy": "Caesare Collins"
}
```