/**
 * Contains interfaces for querying and interacting with the Discogs API.
 * <p>
 * This package provides abstractions for various services related to querying and interacting
 * with the Discogs API. The interfaces define methods for executing HTTP requests, handling
 * retries, and managing rate limits, enabling flexible and reusable implementations for
 * interacting with the API.
 *
 * <p>
 * Interfaces in this package include:
 *
 * <ul>
 *     <li>{@link org.discogs.query.interfaces.HttpRequestService} -
 *     Defines methods for executing HTTP requests and handling responses.</li>
 *     <li>{@link org.discogs.query.interfaces.RateLimiterService} -
 *     Defines methods for managing rate limits and ensuring compliance with API quotas.</li>
 *     <li>{@link org.discogs.query.interfaces.RetryService} -
 *     Defines methods for executing actions with retry logic in case of failure.</li>
 *     <li>{@link org.discogs.query.interfaces.DiscogsQueryService} -
 *     Defines methods for general queries for the Discogs API</li>
 * </ul>
 */
package org.discogs.query.interfaces;