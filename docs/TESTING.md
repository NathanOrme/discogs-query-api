# Testing Documentation

## Overview

The Discogs Query API implements a comprehensive testing strategy covering backend services, frontend components, API integration, and end-to-end workflows. The testing framework ensures code quality, reliability, and maintainability across the full-stack application.

## Testing Architecture

### Backend Testing (Java/Spring Boot)

- **Unit Tests**: JUnit 5 with Mockito for service and component testing
- **Integration Tests**: Spring Boot Test for full application context testing
- **BDD Tests**: Karate framework for behavior-driven development
- **Generated Tests**: Diffblue Cover for additional coverage
- **Code Coverage**: JaCoCo for comprehensive coverage reporting

### Frontend Testing (React/TypeScript)

- **Unit Tests**: Jest with React Testing Library for component testing
- **Integration Tests**: Full component interaction testing
- **Type Safety**: TypeScript compiler as first line of defense
- **Linting**: ESLint integration for code quality

## Backend Testing Strategy

### 1. Unit Testing with JUnit 5

#### Test Structure

```java
@ExtendWith(MockitoExtension.class)
class DiscogsQueryServiceImplTest {

    @Mock
    private DiscogsAPIClient discogsAPIClient;

    @Mock
    private DiscogsFilterService discogsFilterService;

    @InjectMocks
    private DiscogsQueryServiceImpl discogsQueryService;

    @Test
    @DisplayName("Should return results when search is successful")
    void shouldReturnResultsWhenSearchIsSuccessful() {
        // Given
        DiscogsQueryDTO query = createTestQuery();
        DiscogsResult mockResult = createMockResult();
        when(discogsAPIClient.getSearchResults(any())).thenReturn(mockResult);

        // When
        DiscogsResultDTO result = discogsQueryService.searchBasedOnQuery(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.results()).hasSize(1);
        verify(discogsAPIClient).getSearchResults(any());
    }
}
```

#### Key Testing Patterns

- **@ExtendWith(MockitoExtension.class)**: Mockito integration
- **@Mock**: External dependency mocking
- **@InjectMocks**: Service under test injection
- **@DisplayName**: Descriptive test names
- **AssertJ**: Fluent assertions for better readability

### 2. Integration Testing

#### Spring Boot Integration Tests

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "discogs.token=test-token",
    "discogs.agent=test-agent"
})
class DiscogsQueryControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private DiscogsAPIClient discogsAPIClient;

    @Test
    void shouldReturnSearchResults() {
        // Given
        DiscogsRequestDTO request = createTestRequest();
        when(discogsAPIClient.getSearchResults(any()))
            .thenReturn(createMockSearchResult());

        // When
        ResponseEntity<List<DiscogsMapResultDTO>> response =
            restTemplate.postForEntity("/discogs-query/search", request,
                new ParameterizedTypeReference<List<DiscogsMapResultDTO>>() {});

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }
}
```

#### Integration Test Features

- **Full Application Context**: Real Spring Boot application startup
- **Random Port**: Avoids port conflicts in CI/CD
- **MockBean**: Mock external dependencies while keeping Spring context
- **TestRestTemplate**: HTTP client for endpoint testing

### 3. BDD Testing with Karate

#### Feature File Structure

```gherkin
# src/test/resources/karate/discogs-search.feature
Feature: Discogs Search API

  Background:
    * url baseUrl
    * configure headers = { 'Content-Type': 'application/json' }

  Scenario: Successful search with valid query
    Given path '/discogs-query/search'
    And request
      """
      {
        "queries": [
          {
            "artist": "The Beatles",
            "album": "Abbey Road"
          }
        ]
      }
      """
    When method POST
    Then status 200
    And match response[0].results != null
    And match response[0].originalQuery.artist == 'The Beatles'
```

#### Karate Test Runner

```java
@Test
class DiscogsSearchKarateTest {

    @Karate.Test
    Karate testSearch() {
        return Karate.run("classpath:karate/discogs-search.feature")
                .relativeTo(getClass());
    }
}
```

### 4. Service Layer Testing

#### Rate Limiter Testing

```java
@Test
void shouldRespectRateLimit() {
    // Given
    RateLimiter rateLimiter = new RateLimiter(2); // 2 requests per minute

    // When & Then
    assertThat(rateLimiter.tryAcquire()).isTrue();  // First request
    assertThat(rateLimiter.tryAcquire()).isTrue();  // Second request
    assertThat(rateLimiter.tryAcquire()).isFalse(); // Third request blocked
}
```

#### Caching Testing

```java
@Test
@DirtiesContext
void shouldCacheApiResults() {
    // Given
    String searchUrl = "test-url";
    DiscogsResult expectedResult = createMockResult();
    when(httpRequestService.get(searchUrl, DiscogsResult.class))
        .thenReturn(expectedResult);

    // When - First call
    DiscogsResult firstResult = discogsAPIClient.getSearchResults(searchUrl);

    // When - Second call
    DiscogsResult secondResult = discogsAPIClient.getSearchResults(searchUrl);

    // Then
    assertThat(firstResult).isEqualTo(secondResult);
    verify(httpRequestService, times(1)).get(searchUrl, DiscogsResult.class);
}
```

### 5. Exception Handling Testing

```java
@Test
void shouldHandleDiscogsApiException() {
    // Given
    when(discogsAPIClient.getSearchResults(any()))
        .thenThrow(new DiscogsSearchException("API Error"));

    // When & Then
    assertThatThrownBy(() -> discogsQueryService.searchBasedOnQuery(createTestQuery()))
        .isInstanceOf(DiscogsSearchException.class)
        .hasMessage("API Error");
}
```

## Frontend Testing Strategy

### 1. Component Testing with React Testing Library

#### Basic Component Testing

```typescript
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { App } from './App';

describe('App Component', () => {
  it('renders the main navigation stepper', () => {
    render(<App />);

    expect(screen.getByText('Queries')).toBeInTheDocument();
    expect(screen.getByText('Search')).toBeInTheDocument();
    expect(screen.getByText('Results')).toBeInTheDocument();
    expect(screen.getByText('Cheapest Items')).toBeInTheDocument();
  });

  it('navigates between steps correctly', async () => {
    const user = userEvent.setup();
    render(<App />);

    // Initially on first step
    expect(screen.getByTestId('query-fields')).toBeInTheDocument();

    // Navigate to next step
    await user.click(screen.getByText('Next'));
    expect(screen.getByTestId('search-form')).toBeInTheDocument();

    // Navigate back
    await user.click(screen.getByText('Back'));
    expect(screen.getByTestId('query-fields')).toBeInTheDocument();
  });
});
```

#### Component Isolation Testing

```typescript
// Mock child components for focused testing
jest.mock('./modules/HeroBanner', () => ({
  __esModule: true,
  default: () => <div data-testid="hero-banner">Hero Banner</div>
}));

jest.mock('./modules/QueryFields', () => ({
  __esModule: true,
  default: ({ onQueriesChange }: any) => (
    <div data-testid="query-fields">
      <button onClick={() => onQueriesChange([{ id: 1, artist: 'Test' }])}>
        Update Queries
      </button>
    </div>
  )
}));
```

### 2. Form Testing

```typescript
describe('QueryFields Component', () => {
  it('adds new query when add button is clicked', async () => {
    const user = userEvent.setup();
    const mockOnChange = jest.fn();

    render(<QueryFields onQueriesChange={mockOnChange} />);

    await user.click(screen.getByText('Add Query'));

    expect(mockOnChange).toHaveBeenCalledWith(
      expect.arrayContaining([
        expect.objectContaining({ id: expect.any(Number) })
      ])
    );
  });

  it('validates required fields', async () => {
    const user = userEvent.setup();
    render(<QueryFields onQueriesChange={jest.fn()} />);

    const artistInput = screen.getByLabelText('Artist');
    await user.clear(artistInput);
    await user.tab(); // Trigger validation

    expect(screen.getByText('Artist is required')).toBeInTheDocument();
  });
});
```

### 3. API Integration Testing

```typescript
describe('SearchForm API Integration', () => {
  beforeEach(() => {
    global.fetch = jest.fn();
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  it('handles successful API response', async () => {
    const user = userEvent.setup();
    const mockResponse = [{ originalQuery: {}, results: {} }];

    (global.fetch as jest.Mock).mockResolvedValueOnce({
      ok: true,
      json: () => Promise.resolve(mockResponse)
    });

    const mockSetResponse = jest.fn();
    render(<SearchForm queries={[]} setResponse={mockSetResponse} />);

    await user.click(screen.getByText('Search'));

    await waitFor(() => {
      expect(mockSetResponse).toHaveBeenCalledWith(mockResponse);
    });
  });

  it('handles API errors gracefully', async () => {
    const user = userEvent.setup();

    (global.fetch as jest.Mock).mockRejectedValueOnce(
      new Error('Network error')
    );

    render(<SearchForm queries={[]} setResponse={jest.fn()} />);

    await user.click(screen.getByText('Search'));

    await waitFor(() => {
      expect(screen.getByText(/error occurred/i)).toBeInTheDocument();
    });
  });
});
```

## Test Configuration

### Backend Test Configuration

#### Maven Surefire Configuration

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-surefire-plugin</artifactId>
  <version>3.5.3</version>
  <configuration>
    <includes>
      <include>**/*Test.java</include>
      <include>**/*Tests.java</include>
      <include>**/*IT.java</include>
    </includes>
  </configuration>
</plugin>
```

#### JaCoCo Coverage Configuration

```xml
<plugin>
  <groupId>org.jacoco</groupId>
  <artifactId>jacoco-maven-plugin</artifactId>
  <version>0.8.13</version>
  <executions>
    <execution>
      <goals>
        <goal>prepare-agent</goal>
      </goals>
    </execution>
    <execution>
      <id>report</id>
      <phase>test</phase>
      <goals>
        <goal>report</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

### Frontend Test Configuration

#### Jest Configuration

```javascript
// jest.config.cjs
module.exports = {
  testEnvironment: "jsdom",
  setupFilesAfterEnv: ["<rootDir>/src/setupTests.ts"],
  moduleNameMapping: {
    "\\.(css|less|scss|sass)$": "identity-obj-proxy",
    "\\.(jpg|jpeg|png|gif|eot|otf|webp|svg|ttf|woff|woff2)$":
      "jest-transform-stub",
  },
  transform: {
    "^.+\\.(ts|tsx)$": "ts-jest",
    "^.+\\.(js|jsx)$": "babel-jest",
  },
  collectCoverageFrom: [
    "src/**/*.{ts,tsx}",
    "!src/**/*.d.ts",
    "!src/index.tsx",
    "!src/reportWebVitals.ts",
  ],
  coverageThreshold: {
    global: {
      branches: 80,
      functions: 80,
      lines: 80,
      statements: 80,
    },
  },
};
```

#### Setup Files

```typescript
// src/setupTests.ts
import "@testing-library/jest-dom";

// Mock IntersectionObserver
global.IntersectionObserver = jest.fn().mockImplementation(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
}));

// Mock window.matchMedia
Object.defineProperty(window, "matchMedia", {
  writable: true,
  value: jest.fn().mockImplementation((query) => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: jest.fn(),
    removeListener: jest.fn(),
    addEventListener: jest.fn(),
    removeEventListener: jest.fn(),
    dispatchEvent: jest.fn(),
  })),
});
```

## Test Execution

### Running Backend Tests

```bash
# Run all tests
mvn test

# Run tests with coverage
mvn test jacoco:report

# Run only unit tests
mvn test -Dtest="**/*Test"

# Run only integration tests
mvn test -Dtest="**/*IT"

# Run specific test class
mvn test -Dtest="DiscogsQueryServiceImplTest"

# Run tests with specific profile
mvn test -Ptest

# Skip tests during build
mvn package -DskipTests
```

### Running Frontend Tests

```bash
cd src/main/frontend

# Run all tests
yarn test

# Run tests in watch mode
yarn test --watch

# Run tests with coverage
yarn test --coverage

# Run specific test file
yarn test App.test.tsx

# Run tests matching pattern
yarn test --testNamePattern="navigation"

# Run tests in CI mode
yarn test --ci --coverage --passWithNoTests
```

## Coverage Requirements

### Backend Coverage Targets

- **Line Coverage**: 85%
- **Branch Coverage**: 80%
- **Method Coverage**: 90%
- **Class Coverage**: 95%

### Frontend Coverage Targets

- **Statements**: 80%
- **Branches**: 80%
- **Functions**: 80%
- **Lines**: 80%

## Test Data Management

### Backend Test Data

```java
// Test data builders
public class TestDataBuilder {
    public static DiscogsQueryDTO createTestQuery() {
        return new DiscogsQueryDTO(
            "The Beatles",
            "Abbey Road",
            "Come Together",
            null,
            "LP",
            DiscogCountries.UK,
            DiscogsTypes.RELEASE,
            null
        );
    }

    public static DiscogsResult createMockResult() {
        return DiscogsResult.builder()
            .results(List.of(createMockEntry()))
            .pagination(createMockPagination())
            .build();
    }
}
```

### Frontend Test Data

```typescript
// Test utilities
export const createMockQuery = (overrides: Partial<Query> = {}): Query => ({
  id: 1,
  artist: "The Beatles",
  album: "Abbey Road",
  ...overrides,
});

export const createMockApiResponse = (): QueryResult[] => [
  {
    originalQuery: createMockQuery(),
    results: {
      "Abbey Road": [createMockEntry()],
    },
  },
];
```

## Continuous Integration

### GitHub Actions Integration

```yaml
name: Test Suite
on: [push, pull_request]

jobs:
  backend-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: "24"
      - name: Run backend tests
        run: mvn test jacoco:report
      - name: Upload coverage
        uses: codecov/codecov-action@v3

  frontend-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: "20"
      - name: Install dependencies
        run: cd src/main/frontend && yarn install
      - name: Run frontend tests
        run: cd src/main/frontend && yarn test --ci --coverage
```

## Best Practices

### Backend Testing Best Practices

1. **Test Naming**: Use descriptive test method names
2. **Test Structure**: Follow Given-When-Then pattern
3. **Mock Strategy**: Mock external dependencies, not internal services
4. **Test Data**: Use builders for consistent test data
5. **Coverage**: Focus on business logic coverage, not just line coverage

### Frontend Testing Best Practices

1. **User-Centric Testing**: Test behavior, not implementation
2. **Component Isolation**: Mock child components for focused testing
3. **Async Testing**: Proper handling of async operations with `waitFor`
4. **Accessibility Testing**: Include accessibility assertions
5. **Error Boundaries**: Test error scenarios and recovery

### General Testing Guidelines

1. **Test Pyramid**: More unit tests, fewer integration tests, minimal E2E
2. **Fast Feedback**: Tests should run quickly in development
3. **Deterministic**: Tests should be reliable and not flaky
4. **Maintainable**: Tests should be easy to understand and modify
5. **Documentation**: Tests serve as living documentation

This comprehensive testing strategy ensures high code quality, reliability, and maintainability across the entire Discogs Query API application.
