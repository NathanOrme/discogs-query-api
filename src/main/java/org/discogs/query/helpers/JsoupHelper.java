package org.discogs.query.helpers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class JsoupHelper {

    public Document connect(final String url, final Map<String, String> headers) throws IOException {
        return Jsoup.connect(url).headers(headers).get();
    }
}
