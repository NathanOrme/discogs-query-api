package org.discogs.query.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DiscogsQueryDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testValidDiscogsQueryDTO() {
        // Arrange
        DiscogsQueryDTO queryDTO = new DiscogsQueryDTO(
                "The Beatles",
                "Hey Jude",
                null,
                null,  // Format can be null or empty
                null,  // Country can be null
                null,  // Barcode can be null
                null,  // Types can be null
                "Vinyl" // Optional format
        );

        // Act
        Set<ConstraintViolation<DiscogsQueryDTO>> violations = validator.validate(queryDTO);

        // Assert
        assertTrue(violations.isEmpty(), "There should be no validation violations");
    }

    @Test
    void testInvalidDiscogsQueryDTO_EmptyArtist() {
        // Arrange
        DiscogsQueryDTO queryDTO = new DiscogsQueryDTO(
                "",  // Invalid artist
                "Hey Jude",
                "Vinyl",
                null,
                null,
                null,
                null,
                null
        );

        // Act
        Set<ConstraintViolation<DiscogsQueryDTO>> violations = validator.validate(queryDTO);

        // Assert
        assertEquals(1, violations.size(), "There should be one validation violation");
        ConstraintViolation<DiscogsQueryDTO> violation = violations.iterator().next();
        assertEquals("must not be blank", violation.getMessage(), "Artist field should not be blank");
        assertEquals("artist", violation.getPropertyPath().toString(), "Violation should be on the artist field");
    }

    @Test
    void testValidDiscogsQueryDTO_WithoutFormat() {
        // Arrange
        DiscogsQueryDTO queryDTO = new DiscogsQueryDTO(
                "The Beatles",
                "Hey Jude",
                null,  // Format can be null
                null,
                null,
                null,
                null,
                null
        );

        // Act
        Set<ConstraintViolation<DiscogsQueryDTO>> violations = validator.validate(queryDTO);

        // Assert
        assertTrue(violations.isEmpty(), "There should be no validation violations");
    }
}
