package org.discogs.query.interfaces;

/**
 * Service interface for handling HTTP requests.
 *
 * <p>This interface defines methods for executing HTTP requests and handling responses.
 */
public interface HttpRequestService {

    /**
     * Executes an HTTP request to the specified URL and returns the response as an instance of the
     * specified type.
     *
     * @param url          the URL to query
     * @param responseType the class type of the response
     * @param <T>          the type of the response
     * @return an instance of the response type containing the API response data
     * @throws Exception if an error occurs while executing the request
     */
    <T> T executeRequest(String url, Class<T> responseType) throws Exception;
}
