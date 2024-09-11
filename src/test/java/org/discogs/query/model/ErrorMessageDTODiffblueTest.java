package org.discogs.query.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorMessageDTODiffblueTest {
    /**
     * Methods under test:
     * <ul>
     *   <li>{@link ErrorMessageDTO#ErrorMessageDTO()}
     *   <li>{@link ErrorMessageDTO#setErrorMessage(String)}
     *   <li>{@link ErrorMessageDTO#getErrorMessage()}
     * </ul>
     */
    @Test
    void testGettersAndSetters() {
        // Arrange and Act
        ErrorMessageDTO actualErrorMessageDTO = new ErrorMessageDTO();
        actualErrorMessageDTO.setErrorMessage("An error occurred");

        // Assert that nothing has changed
        assertEquals("An error occurred", actualErrorMessageDTO.getErrorMessage());
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link ErrorMessageDTO#ErrorMessageDTO(String)}
     *   <li>{@link ErrorMessageDTO#setErrorMessage(String)}
     *   <li>{@link ErrorMessageDTO#getErrorMessage()}
     * </ul>
     */
    @Test
    void testGettersAndSetters2() {
        // Arrange and Act
        ErrorMessageDTO actualErrorMessageDTO = new ErrorMessageDTO("An error occurred");
        actualErrorMessageDTO.setErrorMessage("An error occurred");

        // Assert that nothing has changed
        assertEquals("An error occurred", actualErrorMessageDTO.getErrorMessage());
    }
}
