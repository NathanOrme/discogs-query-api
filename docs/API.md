# Discogs Query API Documentation

## Overview

The Discogs Query API provides a RESTful interface for searching the Discogs catalog with advanced features including batch processing, marketplace integration, rate limiting, and caching. The API supports multiple search criteria and returns enriched results with pricing and availability information.

## Base URL

```
http://localhost:9090
```

## Authentication

The API uses Spring Security with basic authentication:

- **Username**: `username`
- **Password**: `password`

Include these credentials in your requests using HTTP Basic Authentication.

## Rate Limiting

The API implements intelligent rate limiting:

- **Default Limit**: 60 requests per minute
- **Algorithm**: Token bucket with atomic counters
- **Behavior**: Requests exceeding the limit will wait for available tokens
- **Configuration**: Adjustable via `discogs.rate-limit` property

## Endpoints

### 1. Search Discogs Catalog

Execute batch queries against the Discogs database with marketplace integration.

**Endpoint**: `POST /discogs-query/search`

**Content-Type**: `application/json`

**Request Body**:

```json
{
  "queries": [
    {
      "artist": "The Beatles",
      "album": "Abbey Road",
      "track": "Come Together",
      "title": "Optional title override",
      "format": "LP",
      "country": "UK",
      "types": "release",
      "barcode": "123456789"
    }
  ],
  "username": "optional_discogs_username"
}
```

**Query Parameters**:

| Field      | Type   | Required | Description                              | Example                                |
| ---------- | ------ | -------- | ---------------------------------------- | -------------------------------------- |
| `artist`   | String | Yes      | Artist name to search for                | "The Beatles"                          |
| `album`    | String | No       | Album/release title                      | "Abbey Road"                           |
| `track`    | String | No       | Track title                              | "Come Together"                        |
| `title`    | String | No       | Title override for search                | "Custom Title"                         |
| `format`   | String | No       | Release format                           | "LP", "CD", "Cassette"                 |
| `country`  | Enum   | No       | Country of release                       | "UK", "US", "DE", etc.                 |
| `types`    | Enum   | No       | Release type                             | "release", "master", "artist", "label" |
| `barcode`  | String | No       | Product barcode                          | "123456789"                            |
| `username` | String | No       | Discogs username for collection searches | "your_username"                        |

**Response**:

```json
[
  {
    "originalQuery": {
      "artist": "The Beatles",
      "album": "Abbey Road",
      "track": "Come Together",
      "format": "LP"
    },
    "results": {
      "Abbey Road": [
        {
          "title": "Abbey Road",
          "artist": "The Beatles",
          "format": "LP",
          "year": 1969,
          "label": "Apple Records",
          "catno": "PCS 7088",
          "country": "UK",
          "id": 12345,
          "uri": "/The-Beatles-Abbey-Road/release/12345",
          "resourceUrl": "https://api.discogs.com/releases/12345",
          "type": "release",
          "thumb": "https://img.discogs.com/thumb.jpg",
          "coverImage": "https://img.discogs.com/image.jpg",
          "genre": ["Rock"],
          "style": ["Pop Rock"],
          "marketplace": {
            "lowestPrice": 25.99,
            "numForSale": 15,
            "currency": "GBP",
            "available": true,
            "shipsFromUk": true
          }
        }
      ]
    },
    "cheapestItem": {
      "title": "Abbey Road",
      "artist": "The Beatles",
      "lowestPrice": 25.99,
      "currency": "GBP",
      "numForSale": 15
    }
  }
]
```

**Response Fields**:

| Field            | Type   | Description                              |
| ---------------- | ------ | ---------------------------------------- |
| `originalQuery`  | Object | The original query parameters            |
| `results`        | Object | Results grouped by title                 |
| `results[title]` | Array  | Array of matching releases               |
| `cheapestItem`   | Object | Cheapest available item from all results |

**Individual Release Fields**:

| Field         | Type    | Description                           |
| ------------- | ------- | ------------------------------------- |
| `id`          | Integer | Discogs release ID                    |
| `title`       | String  | Release title                         |
| `artist`      | String  | Artist name                           |
| `format`      | String  | Release format                        |
| `year`        | Integer | Release year                          |
| `label`       | String  | Record label                          |
| `catno`       | String  | Catalog number                        |
| `country`     | String  | Country of release                    |
| `uri`         | String  | Discogs URI path                      |
| `resourceUrl` | String  | Full API resource URL                 |
| `type`        | String  | Resource type (release, master, etc.) |
| `thumb`       | String  | Thumbnail image URL                   |
| `coverImage`  | String  | Full cover image URL                  |
| `genre`       | Array   | Music genres                          |
| `style`       | Array   | Music styles                          |
| `marketplace` | Object  | Marketplace data (if available)       |

**Marketplace Fields**:

| Field         | Type    | Description              |
| ------------- | ------- | ------------------------ |
| `lowestPrice` | Number  | Lowest available price   |
| `numForSale`  | Integer | Number of items for sale |
| `currency`    | String  | Price currency           |
| `available`   | Boolean | Item availability status |
| `shipsFromUk` | Boolean | Ships from UK flag       |

**HTTP Status Codes**:

| Code  | Description                                     |
| ----- | ----------------------------------------------- |
| `200` | Success - Results found                         |
| `204` | No Content - No results found                   |
| `400` | Bad Request - Invalid query parameters          |
| `408` | Request Timeout - Query processing timeout      |
| `429` | Too Many Requests - Rate limit exceeded         |
| `500` | Internal Server Error - Server processing error |

**Error Response**:

```json
{
  "error": "Error message description",
  "timestamp": "2024-01-01T12:00:00.000Z",
  "path": "/discogs-query/search"
}
```

### 2. Health Check

Check application health status.

**Endpoint**: `GET /actuator/health`

**Response**:

```json
{
  "status": "UP",
  "components": {
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 1000000000,
        "free": 500000000,
        "threshold": 10485760,
        "path": "/."
      }
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

### 3. API Documentation

Access interactive API documentation.

**Endpoint**: `GET /swagger-ui.html`

Opens the Swagger UI interface for interactive API exploration and testing.

**Endpoint**: `GET /api-docs`

Returns the OpenAPI specification in JSON format.

### 4. Request Mappings

View all available request mappings.

**Endpoint**: `GET /actuator/mappings`

Returns detailed information about all controller mappings and endpoints.

## Advanced Features

### Batch Processing

- Execute multiple queries in a single request
- Concurrent processing with configurable timeouts
- Automatic deduplication of results

### Marketplace Integration

- Real-time price and availability data
- Currency support (GBP, USD, EUR, etc.)
- UK shipping filter option
- Stock level information

### Caching Strategy

- **Duration**: 10-minute TTL
- **Capacity**: 1000 entries per cache namespace
- **Namespaces**:
  - `discogsResults` - Search API responses
  - `stringResults` - Raw string responses
  - `marketplaceResults` - Marketplace data
  - `collectionReleases` - User collections

### Format Expansion

Certain formats trigger automatic query expansion:

- `ALL_VINYLS` → expands to `LP`, `VINYL_COMPILATION`, `VINYL`
- Provides comprehensive results across format variations

### Error Handling

- Comprehensive exception hierarchy
- Retry logic with exponential backoff
- Graceful degradation for marketplace failures
- Detailed error messages with context

## Configuration

### Query Settings

```yaml
queries:
  timeout: 59 # Query timeout in seconds
  filterForUk: false # Filter for UK shipping only
  searchCollection: true # Enable collection searching
```

### Rate Limiting

```yaml
discogs:
  rate-limit: 60 # Requests per minute
```

### Caching

```yaml
spring:
  cache:
    caffeine:
      spec: maximumSize=1000,expireAfterWrite=10m
```

## Examples

### Basic Artist Search

```bash
curl -X POST "http://localhost:9090/discogs-query/search" \
  -H "Content-Type: application/json" \
  -u username:password \
  -d '{
    "queries": [
      {
        "artist": "Pink Floyd"
      }
    ]
  }'
```

### Advanced Search with Multiple Criteria

```bash
curl -X POST "http://localhost:9090/discogs-query/search" \
  -H "Content-Type: application/json" \
  -u username:password \
  -d '{
    "queries": [
      {
        "artist": "The Beatles",
        "album": "Abbey Road",
        "format": "LP",
        "country": "UK",
        "types": "release"
      },
      {
        "artist": "Pink Floyd",
        "album": "Dark Side of the Moon",
        "format": "CD"
      }
    ],
    "username": "your_discogs_username"
  }'
```

### Collection Search

```bash
curl -X POST "http://localhost:9090/discogs-query/search" \
  -H "Content-Type: application/json" \
  -u username:password \
  -d '{
    "queries": [
      {
        "artist": "Miles Davis"
      }
    ],
    "username": "jazz_collector_username"
  }'
```

## Best Practices

1. **Rate Limiting**: Respect the 60 requests/minute limit
2. **Batch Queries**: Use batch processing for multiple searches
3. **Caching**: Identical queries within 10 minutes return cached results
4. **Error Handling**: Implement proper error handling for timeout and rate limit scenarios
5. **Authentication**: Always include valid credentials
6. **Filtering**: Use format and country filters to narrow results
7. **Marketplace Data**: Handle cases where marketplace data may be unavailable

## Support

For additional support:

- Review the [Swagger UI](http://localhost:9090/swagger-ui.html) for interactive testing
- Check the [Health endpoint](http://localhost:9090/actuator/health) for system status
- Consult the [README.md](../README.md) for setup instructions
