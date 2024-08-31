package org.discogs.query.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration class for setting up Cross-Origin Resource Sharing (CORS) settings.
 * <p>
 * This class configures CORS mappings to allow cross-origin requests from specific origins and methods.
 * It implements {@link WebMvcConfigurer} to provide custom configuration for Spring's web application context.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configures CORS settings for the application.
     * <p>
     * This method sets up CORS mappings to allow requests from specific origins and methods.
     * <ul>
     *     <li>Allows requests from "http://localhost:3000" (typically used by a React application).</li>
     *     <li>Permits HTTP methods: GET, POST, PUT, DELETE, and OPTIONS.</li>
     * </ul>
     *
     * @param registry The {@link CorsRegistry} object used to configure CORS mappings.
     */
    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")  // Allow requests from React app
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
}
