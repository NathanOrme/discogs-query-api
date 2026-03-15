# Test Coverage Improvements

**Priority:** P1 — High
**Category:** Testing
**Estimated Effort:** 2–3 days
**Target:** 80%+ line coverage

---

## Context

The service has `TESTING.md` documentation. This doc tracks what's missing to
reach the 80% coverage target.

---

## Priority Gaps

### 1. RateLimiterServiceImpl — Concurrent Load

The token bucket at 60 req/min is critical. A single-threaded test won't catch
thread-safety issues.

```java
@Test
void rateLimiterIsThreadSafeUnder65ConcurrentRequests() throws InterruptedException {
    int threadCount = 65;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch latch = new CountDownLatch(1);
    AtomicInteger allowed = new AtomicInteger(0);
    AtomicInteger throttled = new AtomicInteger(0);

    for (int i = 0; i < threadCount; i++) {
        executor.submit(() -> {
            try {
                latch.await();
                if (rateLimiterService.tryAcquire()) {
                    allowed.incrementAndGet();
                } else {
                    throttled.incrementAndGet();
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });
    }
    latch.countDown();
    executor.awaitTermination(5, TimeUnit.SECONDS);
    assertThat(allowed.get()).isLessThanOrEqualTo(60);
    assertThat(throttled.get()).isGreaterThanOrEqualTo(5);
}
```

### 2. DiscogsWebScraperClientImpl — Error Handling

Web scraping is brittle. Test malformed HTML responses:

```java
@Test
void scraperReturnsEmptyWhenResponseHtmlIsMalformed() {
    // Mock HttpClient to return malformed HTML
    // Verify service returns empty list rather than throwing
}
```

### 3. MappingService — Edge Cases

- Empty `formats` array from Discogs API
- Null `community` field (price data unavailable)
- Special characters in artist/title strings
- Non-standard country codes in `DiscogsCountryDeserializer`

### 4. ResultCalculationService — Empty Results

```java
@Test
void calculationWithZeroResultsReturnsEmptyNotException() {
    DiscogsResult emptyResult = new DiscogsResult();
    emptyResult.setResults(Collections.emptyList());

    assertDoesNotThrow(() -> resultCalculationService.calculate(emptyResult));
}
```

### 5. Integration Test — Full Search Flow with WireMock

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)  // or use WireMockExtension
class DiscogsSearchIT {
    @Test
    void fullSearchFlowReturnsResults() {
        // Stub Discogs API response with WireMock
        stubFor(get(urlPathMatching("/database/search.*"))
            .willReturn(okJson(/* sample Discogs response JSON */)));

        // Call POST /discogs-query/search
        // Verify response contains expected results
    }
}
```

---

## Work Items

- [ ] Add `com.github.tomakehurst:wiremock-standalone` to test dependencies
- [ ] Write concurrent rate limiter test
- [ ] Write scraper malformed HTML test
- [ ] Write MappingService edge case tests (empty formats, null community, special chars)
- [ ] Write ResultCalculationService empty input test
- [ ] Write WireMock integration test for full search flow
- [ ] Run `./mvnw test jacoco:report` and identify remaining gaps
- [ ] Aim for 80%+ line coverage

---

## Acceptance Criteria

- [ ] `./mvnw test` passes all new tests
- [ ] JaCoCo coverage report shows ≥ 80% line coverage
- [ ] Rate limiter test verifies thread safety under concurrent load
- [ ] Integration test mocks Discogs API (no real Discogs calls in CI)

---

## Dependencies

None — all testing work, no new features.
