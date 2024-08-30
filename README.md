# Discogs Query

A Spring Boot application for querying the Discogs API. This project allows you to search for records in the Discogs
database by specifying an artist, track, and optional format. It includes configuration for API documentation using
Swagger and handles exceptions related to Discogs API interactions.

<!-- TOC -->

* [Discogs Query](#discogs-query)
    * [Features](#features)
    * [Architecture](#architecture)
    * [Getting Started](#getting-started)
        * [Prerequisites](#prerequisites)
        * [Installation](#installation)
        * [Configuration](#configuration)
        * [Endpoints](#endpoints)
        * [API Documentation](#api-documentation)
    * [Exception Handling](#exception-handling)
    * [Development](#development)
        * [Code Style](#code-style)
        * [Running Tests](#running-tests)

<!-- TOC -->

## Features

- **Search Functionality**: Query the Discogs database using artist, track, and format information.
- **Swagger API Documentation**: Automatically generates interactive API documentation.
- **Exception Handling**: Custom exception handling for errors related to the Discogs API.

## Architecture

- **Spring Boot**: The application is built using the Spring Boot framework for easy configuration and deployment.
- **RestTemplate**: Used for making HTTP requests to the Discogs API.
- **Swagger**: Integrated for API documentation.
- **Custom Exceptions**: Handles internal server errors with a custom exception class.

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6.0 or higher

### Installation

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/discogs-query.git
   cd discogs-query
   ```
2. **Build the Project**

```bash
mvn clean install
```

3. **Run the Application**

```bash
mvn spring-boot:run
```

### Configuration

The application requires certain configuration properties to interact with the Discogs API. These properties should be
defined in the application.properties file:

```properties
discogs.url=https://api.discogs.com
discogs.search=/database/search
discogs.agent=YourUserAgent
discogs.page-size=20
discogs.token=YourAccessToken
```

### Endpoints

- **Search Records**
    - **URL**: `/discogs-query/search`
    - **Method**: `GET`
        - **Request Body**:
          ```json
          [
            {
            "track": "Love Train",
            "artist": "The O'Jays",
            "format": "Compilation Vinyl"
            },
            {
            "track": "A fifth of beethoven",
            "artist": "Walter Murphy",
            "format": "Compilation Vinyl"
            },
            {
            "track": "In the summertime",
            "artist": "Mungo Jerry",
            "format": "Compilation Vinyl"
            }
          ]
          ```
    - **Response**: A JSON object containing the search results.

### API Documentation

The API documentation is available via Swagger at:
http://localhost:8080/swagger-ui.html

## Exception Handling

The application uses a custom exception handler for errors related to the Discogs API:

- **`DiscogsAPIException`**: Thrown when an internal server error occurs. It maps to HTTP status code 500.

## Development

### Code Style

- Java 17 or higher is required.
- Follow standard Java coding conventions and use Javadoc for documentation.

### Running Tests

To run the unit tests, use:

```bash
mvn test
```