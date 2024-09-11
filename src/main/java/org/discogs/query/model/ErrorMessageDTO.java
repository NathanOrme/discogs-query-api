package org.discogs.query.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing an error message.
 * This class is used to encapsulate error messages for response
 * objects in the application.
 *
 * <p>It includes a single field {@code errorMessage} that stores
 * the details of the error encountered.
 *
 * <p>Annotations:
 * <ul>
 *  <li>{@link Getter} - Generates getters for all fields.</li>
 *  <li>{@link Setter} - Generates setters for all fields.</li>
 *  <li>{@link Builder} - Provides the builder pattern for object creation.</li>
 *  <li>{@link NoArgsConstructor} - Creates a no-argument constructor.</li>
 *  <li>{@link AllArgsConstructor} - Creates an all-argument constructor.</li>
 * </ul>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMessageDTO {

    /**
     * The message describing the error.
     */
    private String errorMessage;

}
