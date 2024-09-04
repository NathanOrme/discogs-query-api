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
        try (ValidatorFactory factory =
                     Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testValidDiscogsQueryDTO() {
        // Arrange
        DiscogsQueryDTO queryDTO = DiscogsQueryDTO.builder()
                .artist("The Beatles")
                .track("Hey Jude")
                .format("Vinyl")
                .build();

        // Act
        Set<ConstraintViolation<DiscogsQueryDTO>> violations =
                validator.validate(queryDTO);

        // Assert
        assertTrue(violations.isEmpty(), "There should be no validation " +
                "violations");
    }

    @Test
    void testInvalidDiscogsQueryDTO_EmptyArtist() {
        // Arrange
        DiscogsQueryDTO queryDTO = DiscogsQueryDTO.builder()
                .artist("")
                .track("Hey Jude")
                .format("Vinyl")
                .build();

        // Act
        Set<ConstraintViolation<DiscogsQueryDTO>> violations =
                validator.validate(queryDTO);

        // Assert
        assertEquals(1, violations.size(), "There should be one validation " +
                "violation");
        ConstraintViolation<DiscogsQueryDTO> violation =
                violations.iterator().next();
        assertEquals("must not be blank", violation.getMessage(), "Artist " +
                "field should not be blank");
        assertEquals("artist", violation.getPropertyPath().toString(),
                "Violation should be on the artist field");
    }

    @Test
    void testValidDiscogsQueryDTO_WithoutFormat() {
        // Arrange
        DiscogsQueryDTO queryDTO = DiscogsQueryDTO.builder()
                .artist("The Beatles")
                .track("Hey Jude")
                .build();

        // Act
        Set<ConstraintViolation<DiscogsQueryDTO>> violations =
                validator.validate(queryDTO);

        // Assert
        assertTrue(violations.isEmpty(), "There should be no validation " +
                "violations");
    }
}