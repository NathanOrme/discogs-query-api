package org.discogs.query.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Security configuration class for the application.
 */
@Configuration
public class SecurityConfig {

    private static final List<String> ALLOWED_ORIGINS = Arrays.asList("http://localhost:3000",
            "https://discogsqueryapi1-fthsfv0p.b4a.run",
            "https://discogs-query-api-rgbnathan.koyeb.app",
            "https://discogs-query-api.onrender.com",
            "https://rgbnathan-discogs-api.netlify.app/",
            "https://rgbnathan-discogs-api.co.uk/",
            "https://rgbnathan.co.uk", "https://rgbnathan.uk");

    /**
     * Configures CORS (Cross-Origin Resource Sharing) settings for the application.
     *
     * @return A {@link CorsConfigurationSource} that contains the CORS configuration.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(ALLOWED_ORIGINS);
        configuration.setAllowedMethods(Arrays.asList(HttpMethod.GET.name(), HttpMethod.POST.name(),
                HttpMethod.PUT.name(), HttpMethod.DELETE.name()));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Configures the security filter chain for the application.
     *
     * @param http The {@link HttpSecurity} object used to configure security settings for the application.
     * @return A {@link SecurityFilterChain} containing the configured security settings for the application.
     * @throws Exception If an error occurs during the configuration of security settings.
     */
    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        http.cors() // Enable CORS
                .and()
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}
