package org.discogs.query.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

/** Configuration class for setting up HTTP-related beans such as {@link RestTemplate}. */
@Configuration
public class HttpConfig {

  @Value("${discogs.agent:defaultAgent}")
  private String userAgent;

  /**
   * Creates a {@link RestTemplate} bean. This bean is used to make HTTP requests to external
   * services, such as the Discogs API.
   *
   * @return a {@link RestTemplate} instance configured for use in the application
   */
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  /**
   * Creates a {@link HttpHeaders} bean. This bean is used to configure HTTP headers, including the
   * User-Agent, for requests to external services.
   *
   * @return a {@link HttpHeaders} instance with default configurations
   */
  @Bean
  public HttpHeaders buildHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(List.of(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("User-Agent", userAgent);
    return headers;
  }
}
