package org.discogs.query.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for the Discogs Query application.
 * This class is responsible for defining beans and configurations needed for the application.
 */
@Configuration
public class DiscordsAppConfig {

    /**
     * Creates a {@link RestTemplate} bean.
     * This bean is used to make HTTP requests to external services, such as the Discogs API.
     *
     * @return a {@link RestTemplate} instance configured for use in the application
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}