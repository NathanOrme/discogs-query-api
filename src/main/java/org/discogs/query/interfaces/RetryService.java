package org.discogs.query.interfaces;

import java.util.concurrent.Callable;

public interface RetryService {
    <T> T executeWithRetry(Callable<T> action, String actionDescription) throws Exception;
}
