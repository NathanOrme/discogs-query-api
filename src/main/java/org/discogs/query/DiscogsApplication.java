package org.discogs.query;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point for the Discogs Query Application.
 * This class contains the `main` method that starts the Spring Boot
 * application.
 */
@EnableCaching
@EnableScheduling
@SpringBootApplication
public class DiscogsApplication {

    /**
     * Main method that serves as the entry point for the Spring Boot
     * application.
     *
     * @param args command-line arguments passed to the application (if any)
     */
    public static void main(final String[] args) {
        SpringApplication.run(DiscogsApplication.class, args);
    }
}