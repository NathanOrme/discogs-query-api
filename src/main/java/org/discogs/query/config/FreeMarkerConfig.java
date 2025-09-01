package org.discogs.query.config;

import freemarker.template.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Central FreeMarker configuration for template rendering.
 */
@org.springframework.context.annotation.Configuration
public class FreeMarkerConfig {

    @Bean
    @Primary
    public Configuration emailTemplateConfiguration() {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_33);
        cfg.setDefaultEncoding("UTF-8");
        // Load templates from classpath under src/main/resources/templates/email
        cfg.setClassLoaderForTemplateLoading(
                getClass().getClassLoader(),
                "templates/email"
        );
        return cfg;
    }
}
