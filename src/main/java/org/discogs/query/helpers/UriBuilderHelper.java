package org.discogs.query.helpers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * A helper class for working with {@link UriComponentsBuilder}
 * to simplify adding query parameters to URIs.
 * <p>
 * This class provides utility methods to conditionally
 * add query parameters to a {@link UriComponentsBuilder}
 * instance based on the provided values.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UriBuilderHelper {

    private final StringHelper stringHelper;

    /**
     * Adds a query parameter to the given {@link UriComponentsBuilder}
     * if the provided value is not null and not blank.
     * <p>
     * This method is useful for adding query parameters
     * conditionally based on the value's content. If the value is null or
     * blank, the parameter will not be added to the URI.
     *
     * @param uriBuilder the {@link UriComponentsBuilder}
     *                   instance to which the query parameter should be added
     * @param paramName  the name of the query parameter to be added
     * @param paramValue the value of the query parameter to be added;
     *                   if null or blank, the parameter will not be added
     */
    public void addIfNotNullOrBlank(final UriComponentsBuilder uriBuilder,
                                    final String paramName,
                                    final String paramValue) {
        if (stringHelper.isNotNullOrBlank(paramValue)) {
            uriBuilder.queryParam(paramName, paramValue);
            if (log.isDebugEnabled()) {
                log.debug("Added query parameter: {}={}", paramName, paramValue);
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Skipped adding query parameter: {} (value is null or blank)", paramName);
            }
        }
    }

    /**
     * Adds a query parameter to the given {@link UriComponentsBuilder}
     * if the provided value is not null.
     * <p>
     * This method is useful for adding query parameters
     * conditionally based on the value's presence. If the value is null,
     * the parameter will not be added to the URI.
     *
     * @param uriBuilder the {@link UriComponentsBuilder} instance to
     *                   which the query parameter should be added
     * @param paramName  the name of the query parameter to be added
     * @param paramValue the value of the query parameter to be added;
     *                   if null, the parameter will not be added
     */
    public void addIfNotNull(final UriComponentsBuilder uriBuilder,
                             final String paramName, final String paramValue) {
        if (paramValue != null) {
            uriBuilder.queryParam(paramName, paramValue);
            if (log.isDebugEnabled()) {
                log.debug("Added query parameter: {}={}", paramName, paramValue);
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Skipped adding query parameter: {} (value is null)", paramName);
            }
        }
    }
}
