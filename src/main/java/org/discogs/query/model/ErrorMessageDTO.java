package org.discogs.query.model;

/**
 * Data Transfer Object (DTO) representing an error message. This class is used to encapsulate error
 * messages for response objects in the application.
 *
 * <p>It includes a single field {@code errorMessage} that stores the details of the error
 * encountered.
 */
public record ErrorMessageDTO(String errorMessage) {
}
