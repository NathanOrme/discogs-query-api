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
 * This class configures the security filter chain for the application with the following settings:
 * <ul>
 *     <li>Disables CSRF protection.</li>
 *     <li>Requires authentication for all incoming requests.</li>
 *     <li>Uses basic HTTP authentication.</li>
 *     <li>Configures session management to be stateless.</li>
 * </ul>
 */
@Configuration
@SuppressWarnings("unused")
public class SecurityConfig {

    /**
     * Configures the security filter chain for the application.
     * <p>
     * - Disables CSRF protection, which is typically used to prevent cross-site request forgery attacks.
     * - Requires authentication for all requests to ensure that only
     * authenticated users can access any part of the application.
     * - Uses basic HTTP authentication, which is a simple authentication
     * mechanism using username and password.
     * - Configures session management to be stateless,
     * meaning the server will not maintain any session state between requests.
     * </p>
     *
     * @param http The {@link HttpSecurity} object to modify for configuring security settings.
     * @return A {@link SecurityFilterChain} that contains the configured security settings for the application.
     * @throws Exception If an error occurs during the configuration of the security settings.
     */
    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}
