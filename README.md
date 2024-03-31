# Political Speech
Process statistics about political speeches.
### Requirements
- Java 17, Gradle, Static File Server(nginx)

### Configuration
- You can change variables from `application.properties` under resources
- 
### Important Note:
- Two projects implemented, V1 and V2 (github branches)
- The main branch is V2.



### To run project
```
First Step: 
gradle build

Second Step (Or you can run from runnable classes)
gradle run
```

## Usage 
### Evaluating Speeches
The application exposes a GET endpoint `/evaluation` that accepts CSV file URLs as query parameters (e.g., `/evaluation?url1=link_to_csv1&url2=link_to_csv2`). It processes these CSV files to answer questions like which politician gave the most speeches in a specific year, who spoke most about homeland security, and who was the least wordy.


### To run tests
```
gradlew test
```

### Endpoint
```
GET /evaluate?url1=https://example.com/valid-speeches-1.csv
Host: localhost:8080

- Yuu can use nginx easily for host static files
```
### Swagger and ApiDoc addresses:
```
http://localhost:8080/swagger-ui/index.html
http://localhost:8080/v3/api-docs
```

- Postman workspace (collection) containing request with public csv urls (Linkes may not work, use request in local id recommended
```
https://www.postman.com/bgunay1/workspace/public-workspace/collection/1152813-9c321f49-1a3a-4189-841b-e4ead905e850?action=share&creator=1152813
```
## Screenshots
- ![img_1.png](img_1.png)


Swagger Request View:
-  ![img.png](img.png)
- 
### Restrictions
- Url format should be `/evaluate?url1=...&url2=...&urln=...` Other query-params will be ignored.
- Url schema should be `http` or `https`. Other protocols (`file`, `ftp`...) give error.
- CSV file schema should be `Speaker ; Topic ; Date ; Words` And all fields are required (not-null)
- Date format should be `yyyy-MM-dd`. Other formats give parser error.
- `Words` should be greater than `0`
- 
## Architecture
* HttpClient: Custom client for handling HTTP requests to download CSV files.
* CsvParser: Parses CSV data into Speech objects.
* SpeechService: Analyzes the speeches and computes the statistics.
* SpeechRoute: Manages the API route for handling requests to the /evaluation endpoint.


## Thought Process and Decisions
* Modular Architecture: The project adopts a modular design for ease of maintenance and scalability. Each module, like HttpClient, CsvParser, and SpeechService, is responsible for a specific aspect of the application, ensuring separation of concerns.
* Custom HTTP Client: Instead of using a third-party library, a custom HTTP client was implemented using Ktor's CIO engine. This decision was made to have finer control over the HTTP requests and to tailor error handling specific to the application's needs, especially for CSV file downloads.
* CSV Parsing Strategy: The CsvParser was designed to transform CSV data into Speech objects. This approach was chosen for its simplicity and efficiency, allowing the application to directly process the structured CSV data.
* V1 is first implemented, in V2 some of the ambiguities  were removed, separation of concerns implemented cleaner, flow is changed.

## Algorithms
* Speech Analysis: The core analysis algorithms reside in the SpeechService. These algorithms focus on grouping and aggregating speech data to derive meaningful statistics.
* Most Speeches: Aggregates speeches by speaker and year, then identifies the speaker with the highest count.
* Most Security Talks: Filters speeches on the topic of "homeland security" and performs similar aggregation to find the top speaker.
* Least Wordy: Calculates the total word count for each speaker across all speeches, finding the one with the minimum word count.
* Unique Max/Min Finder: A specialized algorithm was developed to ensure that the results for the most and least are unique. If multiple politicians share the top or bottom spot, the algorithm returns null, adhering to the requirement for a unique answer.

## Error Handling
* Custom Exceptions: DownloadException and CsvParsingException were introduced to handle specific error scenarios. This ensures that the application provides clear and actionable error messages, enhancing the robustness of the system.
* Status Pages Configuration: Ktor's StatusPages feature is utilized to catch these exceptions and respond with appropriate HTTP status codes and messages, ensuring a user-friendly API experience.

## Testing
* Unit Tests: Ensure individual components function correctly based on possible real life scenarios.
* Integration Tests: Verify the integration of different components and the overall workflow.
