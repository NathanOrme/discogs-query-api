/**
 * Contains REST controllers for handling HTTP requests and responses.
 *
 * <p>This package is responsible for defining the application's endpoints and processing incoming
 * HTTP requests. It includes:
 *
 * <ul>
 *   <li>Controller classes that handle specific routes and interact with the service layer.
 *   <li>Endpoints for various functionalities such as querying the Discogs API and returning
 *       results.
 * </ul>
 *
 * <p>The classes in this package use Spring's {@link
 * org.springframework.web.bind.annotation.RestController} and related annotations to define RESTful
 * endpoints. They manage request mappings, handle HTTP methods, and return appropriate responses,
 * including data transfer objects and HTTP status codes.
 */
package org.discogs.query.controller;
