package org.discogs.query.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FreeMarkerConfigTest {

    private Configuration newCfg() {
        FreeMarkerConfig cfgBean = new FreeMarkerConfig();
        return cfgBean.emailTemplateConfiguration();
    }

    @Test
    @DisplayName("Bean provides a FreeMarker Configuration with template loader")
    void beanProvidesConfiguration() {
        Configuration cfg = newCfg();
        assertNotNull(cfg);
        assertDoesNotThrow(() -> cfg.getTemplate("results.ftl"));
        assertDoesNotThrow(() -> cfg.getTemplate("results.txt.ftl"));
    }

    @Test
    @DisplayName("Templates render with a minimal model without throwing")
    void templatesRenderWithMinimalModel() {
        Configuration cfg = newCfg();
        Map<String, Object> cheapest = Map.of(
                "title", "Sample Title",
                "link", "https://www.discogs.com/test",
                "price", "9.99",
                "meta", "1997 · UK · Vinyl"
        );
        Map<String, Object> item = Map.of(
                "title", "Item Title",
                "link", "https://www.discogs.com/item",
                "meta", "1997 · UK · Vinyl"
        );
        Map<String, Object> group = Map.of(
                "name", "matches",
                "items", List.of(item)
        );
        Map<String, Object> result = Map.of(
                "queryLabel", "Artist - Album - Track",
                "cheapest", cheapest,
                "groups", List.of(group)
        );
        Map<String, Object> model = Map.of("results", List.of(result));

        assertDoesNotThrow(() -> render(cfg, "results.ftl", model));
        assertDoesNotThrow(() -> render(cfg, "results.txt.ftl", model));
    }

    private String render(Configuration cfg, String templateName, Map<String, Object> model)
            throws IOException, TemplateException {
        Template t = cfg.getTemplate(templateName);
        try (StringWriter w = new StringWriter()) {
            t.process(model, w);
            return w.toString();
        }
    }
}
