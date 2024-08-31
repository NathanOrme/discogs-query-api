package org.discogs.query;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class UriBuilderHelper {

    public void addIfNotNullOrBlank(final UriComponentsBuilder uriBuilder, final String paramName, final String paramValue) {
        if (paramValue != null && !paramValue.isBlank()) {
            uriBuilder.queryParam(paramName, paramValue);
        }
    }

    public void addIfNotNull(final UriComponentsBuilder uriBuilder, final String paramName, final String paramValue) {
        if (paramValue != null) {
            uriBuilder.queryParam(paramName, paramValue);
        }
    }
}
