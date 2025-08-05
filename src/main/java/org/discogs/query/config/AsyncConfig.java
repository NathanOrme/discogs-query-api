package org.discogs.query.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Configuration for dedicated thread pools to optimize async operations.
 * Replaces the default ForkJoinPool.commonPool() with properly sized
 * thread pools for different types of operations.
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig {

  @Value("${discogs.async.api-pool-size:8}")
  private int apiPoolSize;

  @Value("${discogs.async.scraper-pool-size:4}")
  private int scraperPoolSize;

  @Value("${discogs.async.queue-capacity:100}")
  private int queueCapacity;

  /**
   * Dedicated thread pool for Discogs API calls.
   * Optimized for I/O-bound operations with higher concurrency.
   */
  @Bean("discogsApiExecutor")
  public Executor discogsApiExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(apiPoolSize);
    executor.setMaxPoolSize(apiPoolSize * 2);
    executor.setQueueCapacity(queueCapacity);
    executor.setKeepAliveSeconds(60);
    executor.setThreadNamePrefix("DiscogAPI-");
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(30);
    executor.initialize();
    
    log.info("Configured Discogs API thread pool: core={}, max={}, queue={}",
        apiPoolSize, apiPoolSize * 2, queueCapacity);
    
    return executor;
  }

  /**
   * Dedicated thread pool for web scraping operations.
   * Optimized for slower, resource-intensive operations.
   */
  @Bean("discogsScraperExecutor")
  public Executor discogsScraperExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(scraperPoolSize);
    executor.setMaxPoolSize(scraperPoolSize * 2);
    executor.setQueueCapacity(queueCapacity / 2);
    executor.setKeepAliveSeconds(120);
    executor.setThreadNamePrefix("DiscogScraper-");
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(60);
    executor.initialize();
    
    log.info("Configured Discogs Scraper thread pool: core={}, max={}, queue={}",
        scraperPoolSize, scraperPoolSize * 2, queueCapacity / 2);
    
    return executor;
  }

  /**
   * General purpose thread pool for other async operations.
   */
  @Bean("generalExecutor")
  public Executor generalExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(4);
    executor.setMaxPoolSize(8);
    executor.setQueueCapacity(queueCapacity);
    executor.setKeepAliveSeconds(60);
    executor.setThreadNamePrefix("General-");
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(30);
    executor.initialize();
    
    log.info("Configured General thread pool: core=4, max=8, queue={}", queueCapacity);
    
    return executor;
  }
}