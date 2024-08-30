package org.discogs.query.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@SuppressWarnings("unused")
public class SecurityConfig {

    /**
     * Configures the security filter chain for the application.
     * <p>
     * - Disables CSRF protection.
     * - Requires authentication for all requests.
     * - Uses basic HTTP authentication.
     * - Configures session management to be stateless.
     *
     * @param http The {@link HttpSecurity} to modify.
     * @return A configured {@link SecurityFilterChain}.
     * @throws Exception If an error occurs during configuration.
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
