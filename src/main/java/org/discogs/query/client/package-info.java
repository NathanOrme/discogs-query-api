/**
 * Contains client components for interacting with external APIs and services.
 * <p>
 * This package includes classes responsible for communicating with the Discogs API to fetch and process data.
 * It includes:
 * <ul>
 *     <li>API client implementations that handle HTTP requests and responses.</li>
 *     <li>Error handling and logging related to API interactions.</li>
 * </ul>
 * <p>
 * The classes in this package use {@link org.springframework.web.client.RestTemplate} to send requests and handle
 * responses from external APIs. They encapsulate the logic required to interact with the Discogs API, including
 * constructing requests, processing responses, and managing exceptions.
 */
package org.discogs.query.client;