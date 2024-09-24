# Discogs Query

A full-stack application for querying the Discogs API. Built using Spring Boot for the backend and React for the
frontend, this project allows you to search for records in the Discogs database by specifying an artist, track, and
optional format. It includes configuration for API documentation using Swagger and handles exceptions related to Discogs
API interactions.

<!-- TOC -->

* [Discogs Query](#discogs-query)
    * [Features](#features)
    * [Architecture](#architecture)
    * [Getting Started](#getting-started)
        * [Prerequisites](#prerequisites)
        * [Installation](#installation)
        * [Configuration](#configuration)
        * [Running Locally](#running-locally)
            * [Run the Backend](#run-the-backend)
            * [Run the Frontend](#run-the-frontend)
    * [Development](#development)
        * [Code Style](#code-style)
        * [Running Tests](#running-tests)

<!-- TOC -->

## Features

- **Search Functionality**: Query the Discogs database using artist, track, and format information.
- **Swagger API Documentation**: Automatically generates interactive API documentation.
- **Exception Handling**: Custom exception handling for errors related to the Discogs API.
- **React Frontend**: A modern UI built with React to perform searches and display results.
- **Docker Support**: Build and deploy the application with Docker.

## Architecture

- **Spring Boot**: Used for the backend with REST APIs to handle Discogs queries.
- **React**: Frontend built with React, providing a search form and displaying results.
- **RestTemplate**: Used for making HTTP requests to the Discogs API.
- **Swagger**: Integrated for API documentation.
- **Custom Exceptions**: Handles internal server errors with a custom exception class.
- **Docker**: Multi-stage Dockerfile for building both frontend and backend in a single image.

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6.0 or higher
- Node.js 22 or higher
- Docker (optional, for containerized deployment)

### Installation

1. Clone the Repository
   ```bash
   git clone https://github.com/yourusername/discogs-query.git
   cd discogs-query
2. Install Backend Dependencies
    ```bash
    mvn clean install
    ```
3. Install Frontend Dependencies
    ```bash
    npm install
    ```

### Configuration

The application requires certain configuration properties to interact with the Discogs API. These properties should be
defined in the application.properties file for the backend:

```properties
discogs.url=https://api.discogs.com
discogs.search=/database/search
discogs.agent=YourUserAgent
discogs.page-size=20
discogs.token=YourAccessToken
```

### Running Locally

You can run both the backend and frontend locally as follows:

#### Run the Backend

```bash
mvn spring-boot:run
```

#### Run the Frontend

```bash
npm start
```

This will start the backend on http://localhost:9090 and the frontend on http://localhost:3000.

## Development

### Code Style

- Java: Follow standard Java coding conventions and use Javadoc for documentation.
- JavaScript: Adhere to standard JavaScript/React best practices.

### Running Tests

To run the backend unit tests, use:

```bash
mvn test
```

For frontend tests, run:

```bash
npm test
```

Both frontend and backend tests are crucial to maintain code quality and robustness.

This updated README reflects the new structure and provides additional details for Docker and deployment steps. Let me
know if you'd like to add or modify anything!





