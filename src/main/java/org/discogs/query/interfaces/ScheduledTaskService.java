package org.discogs.query.interfaces;

import org.springframework.scheduling.annotation.Scheduled;

/**
 * Interface for scheduling tasks that require periodic execution.
 */
public interface ScheduledTaskService {

    /**
     * Sends an HTTP POST request to the specified endpoint every 30 minutes.
     */
    @Scheduled(fixedRate = 1800000)
    // 30 minutes in milliseconds
    void sendRequest();
}