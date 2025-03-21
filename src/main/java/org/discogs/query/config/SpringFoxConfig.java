package org.discogs.query.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Configuration class for setting up Swagger documentation. This class configures Swagger to
 * generate API documentation for the Discogs Query application.
 */
@Configuration
public class SpringFoxConfig {

    /**
     * Creates a {@link Docket} bean to configure Swagger for the application. This bean sets up the
     * Swagger documentation to include all APIs and paths in the application.
     *
     * @return a {@link Docket} instance configured for Swagger 2 documentation
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }
}
