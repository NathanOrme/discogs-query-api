package org.discogs.query.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class NormalizationServiceTest {

    private final NormalizationService normalizationService = new NormalizationService();

    @Test
    void testNormalizeString() {
        // Test case with various transformations
        String input = " café-123 & test ";
        String expected = "cafe 123 and test";
        String result = normalizationService.normalizeString(input);
        assertEquals(expected, result);

        // Test case with null input
        assertNull(normalizationService.normalizeString(null));

        // Test case with no diacritical marks and simple replacements
        input = "hello-world & 'goodbye'";
        expected = "hello world and goodbye";
        result = normalizationService.normalizeString(input);
        assertEquals(expected, result);

        // Test case with extra whitespace
        input = "  multiple    spaces   ";
        expected = "multiple spaces";
        result = normalizationService.normalizeString(input);
        assertEquals(expected, result);
    }
}

