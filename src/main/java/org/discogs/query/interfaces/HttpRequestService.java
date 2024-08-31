package org.discogs.query.interfaces;

public interface HttpRequestService {
    <T> T executeRequest(String url, Class<T> responseType);
}
