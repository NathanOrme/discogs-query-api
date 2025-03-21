package org.discogs.query.model.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to validate {@link org.discogs.query.model.DiscogsQueryDTO} objects for the "Various
 * Artists" scenario. This annotation ensures that when the artist is identified as "Various
 * Artists", both a track and an album must be supplied in the DTO.
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = VariousArtistsValidator.class)
public @interface VariousArtistsValidation {

    /**
     * Default error message when the validation fails.
     *
     * @return the default error message
     */
    String message() default "A track or album must be supplied if dealing with various artists";

    /**
     * Groups of constraints that this annotation belongs to. This can be used to apply different sets
     * of validation rules based on different contexts.
     *
     * @return the groups of constraints
     */
    Class<?>[] groups() default {};

    /**
     * Payload that can carry additional metadata about the annotation. This is typically used by
     * validation clients to carry additional information about the validation constraint.
     *
     * @return the payload containing additional information
     */
    Class<? extends Payload>[] payload() default {};
}
