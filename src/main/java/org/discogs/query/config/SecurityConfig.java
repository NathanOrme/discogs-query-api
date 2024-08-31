package org.discogs.query.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration class for the application.
 * <p>
 * This class configures the security settings for the application by implementing
 * {@link SecurityFilterChain}. It sets up basic HTTP authentication, disables CSRF protection,
 * enforces stateless session management, and configures CORS (Cross-Origin Resource Sharing)
 * settings to allow requests from specified origins.
 */
@Configuration
public class SecurityConfig {

    /**
     * Configures the security filter chain for the application.
     * <p>
     * This method sets up security configurations, including:
     * <ul>
     *     <li>Disabling CSRF (Cross-Site Request Forgery) protection, which is typically used
     *     to prevent CSRF attacks. This is often disabled for stateless APIs.</li>
     *     <li>Requiring authentication for all
     *     incoming requests to ensure that only authenticated
     *     users can access any part of the application.</li>
     *     <li>Using basic HTTP authentication,
     *     which involves a simple authentication mechanism
     *     using a username and password sent in HTTP headers.</li>
     *     <li>Configuring session management to be stateless,
     *     meaning the server will not maintain
     *     any session state between requests, which is common for REST APIs.</li>
     *     <li>Setting up CORS (Cross-Origin Resource Sharing)
     *     to allow requests from specified origins
     *     and methods, which is necessary for enabling front-end applications running on different
     *     origins to interact with this backend service.</li>
     * </ul>
     *
     * @param http The {@link HttpSecurity} object
     *             used to configure security settings for the application.
     * @return A {@link SecurityFilterChain} containing
     * the configured security settings for the application.
     * @throws Exception If an error occurs during the configuration of security settings.
     */
    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated() // Require authentication for all requests
                )
                .httpBasic(Customizer.withDefaults()) // Use basic HTTP authentication
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // Stateless session management
        return http.build();
    }
}
