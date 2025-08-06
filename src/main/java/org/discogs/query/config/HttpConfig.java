package org.discogs.query.config;

import java.time.Duration;
import java.util.List;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/** Configuration class for setting up HTTP-related beans with connection pooling and timeouts. */
@Configuration
public class HttpConfig {

  @Value("${discogs.agent:defaultAgent}")
  private String userAgent;

  @Value("${http.connection.pool.max-total:100}")
  private int maxTotalConnections;

  @Value("${http.connection.pool.max-per-route:20}")
  private int maxConnectionsPerRoute;

  @Value("${http.connection.timeout:30000}")
  private int connectionTimeout;

  @Value("${http.read.timeout:60000}")
  private int readTimeout;

  /**
   * Creates a connection manager with pooling configuration.
   *
   * @return configured connection manager
   */
  @Bean
  public PoolingHttpClientConnectionManager connectionManager() {
    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    connectionManager.setMaxTotal(maxTotalConnections);
    connectionManager.setDefaultMaxPerRoute(maxConnectionsPerRoute);
    return connectionManager;
  }

  /**
   * Creates an HTTP client with connection pooling and timeout configuration.
   *
   * @return configured HTTP client
   */
  @Bean
  public CloseableHttpClient httpClient() {
    RequestConfig requestConfig = RequestConfig.custom()
        .setConnectionRequestTimeout(Timeout.ofMilliseconds(connectionTimeout))
        .setResponseTimeout(Timeout.ofMilliseconds(readTimeout))
        .build();

    return HttpClients.custom()
        .setConnectionManager(connectionManager())
        .setDefaultRequestConfig(requestConfig)
        .evictExpiredConnections()
        .evictIdleConnections(TimeValue.ofSeconds(30))
        .build();
  }

  /**
   * Creates a {@link RestTemplate} bean with connection pooling and timeout configuration.
   * This bean is used to make HTTP requests to external services, such as the Discogs API.
   *
   * @return a {@link RestTemplate} instance configured for use in the application
   */
  @Bean
  public RestTemplate restTemplate() {
    HttpComponentsClientHttpRequestFactory factory = 
        new HttpComponentsClientHttpRequestFactory(httpClient());
    return new RestTemplate(factory);
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
