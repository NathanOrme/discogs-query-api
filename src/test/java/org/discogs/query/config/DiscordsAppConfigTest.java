package org.discogs.query.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class DiscordsAppConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void testRestTemplateBean() {
        // Act
        RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);

        // Assert
        assertNotNull(restTemplate, "RestTemplate bean should be created by the configuration class.");
    }
}