package org.discogs.query.util;

import java.io.IOException;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

/**
 * Helper class for making HTTP connections using Jsoup. This component simplifies the process of
 * fetching HTML content from a URL.
 */
@Component
public class JsoupHelper {

  /**
   * Connects to the specified URL and retrieves the HTML content as a {@link Document}. This method
   * allows the inclusion of custom headers in the request.
   *
   * @param url the URL to connect to
   * @param headers a map of HTTP headers to include in the request
   * @return the HTML content of the page as a {@link Document}
   * @throws IOException if an I/O error occurs during the connection
   */
  public Document connect(final String url, final Map<String, String> headers) throws IOException {
    return Jsoup.connect(url).headers(headers).get();
  }
}