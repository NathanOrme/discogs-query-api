package org.discogs.query.helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UriBuilderHelperTest {

    private UriBuilderHelper uriBuilderHelper;
    private UriComponentsBuilder uriComponentsBuilder;

    @BeforeEach
    void setUp() {
        uriBuilderHelper = new UriBuilderHelper();
        uriComponentsBuilder = UriComponentsBuilder.newInstance();
    }

    @Test
    void testAddIfNotNullOrBlank_withNonNullNonBlankValue() {
        uriBuilderHelper.addIfNotNullOrBlank(uriComponentsBuilder, "name",
                "JohnDoe");
        String resultUri = uriComponentsBuilder.toUriString();
        assertEquals("?name=JohnDoe", resultUri, "Expected query parameter " +
                "'name=JohnDoe' to be added");
    }

    @Test
    void testAddIfNotNullOrBlank_withBlankValue() {
        uriBuilderHelper.addIfNotNullOrBlank(uriComponentsBuilder, "name", " " +
                "  ");
        String resultUri = uriComponentsBuilder.toUriString();
        assertEquals("", resultUri, "Expected no query parameter to be added " +
                "for blank value");
    }

    @Test
    void testAddIfNotNullOrBlank_withNullValue() {
        uriBuilderHelper.addIfNotNullOrBlank(uriComponentsBuilder, "name",
                null);
        String resultUri = uriComponentsBuilder.toUriString();
        assertEquals("", resultUri, "Expected no query parameter to be added " +
                "for null value");
    }

    @Test
    void testAddIfNotNull_withNonNullValue() {
        uriBuilderHelper.addIfNotNull(uriComponentsBuilder, "age", "25");
        String resultUri = uriComponentsBuilder.toUriString();
        assertEquals("?age=25", resultUri, "Expected query parameter 'age=25'" +
                " to be added");
    }

    @Test
    void testAddIfNotNull_withNullValue() {
        uriBuilderHelper.addIfNotNull(uriComponentsBuilder, "age", null);
        String resultUri = uriComponentsBuilder.toUriString();
        assertEquals("", resultUri, "Expected no query parameter to be added " +
                "for null value");
    }
}
