package org.discogs.query.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test class for {@link JacksonConfig}.
 */
class JacksonConfigTest {

    /**
     * Test that the {@link ObjectMapper} bean is correctly configured.
     */
    @Test
    void testObjectMapperConfiguration() {
        // Initialize the Spring application context with the configuration
        // class
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(JacksonConfig.class)) {

            // Retrieve the ObjectMapper bean
            ObjectMapper objectMapper = context.getBean(ObjectMapper.class);

            // Assert that the ObjectMapper bean is not null
            assertNotNull(objectMapper, "ObjectMapper should not be null");
        }
    }
}
