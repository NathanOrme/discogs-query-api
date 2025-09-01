package org.discogs.query.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import freemarker.template.Configuration;
import java.lang.reflect.Field;
import java.util.List;
import org.discogs.query.model.DiscogsEntryDTO;
import org.discogs.query.model.DiscogsMapResultDTO;
import org.discogs.query.model.DiscogsQueryDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MailtrapEmailServiceTest {

  private static void set(Object target, String field, Object value) {
    try {
      Field f = target.getClass().getDeclaredField(field);
      f.setAccessible(true);
      f.set(target, value);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private DiscogsMapResultDTO sampleMapDto() {
    DiscogsQueryDTO q =
        new DiscogsQueryDTO("Artist", "Album", "Track", null, null, null, null, null);
    DiscogsEntryDTO e =
        new DiscogsEntryDTO(1, "Title", List.of("Vinyl"), null, null, "UK", "1997", true, 10.0f, 1);
    return new DiscogsMapResultDTO(q, java.util.Map.of("matches", List.of(e)), e);
  }

  private Configuration fmConfig() {
    Configuration cfg = new Configuration(Configuration.VERSION_2_3_33);
    cfg.setDefaultEncoding("UTF-8");
    cfg.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "templates/email");
    return cfg;
  }

  @Test
  @DisplayName("sendResults: skips when recipient is blank")
  void sendResults_skips_whenRecipientBlank() {
    MailtrapEmailService svc = new MailtrapEmailService(fmConfig());
    set(svc, "apiToken", "token");
    set(svc, "fromEmail", "from@example.com");

    assertDoesNotThrow(() -> svc.sendResults(" ", "subject", List.of(sampleMapDto())));
  }

  @Test
  @DisplayName("sendResults: skips when apiToken missing")
  void sendResults_skips_whenTokenMissing() {
    MailtrapEmailService svc = new MailtrapEmailService(fmConfig());
    set(svc, "apiToken", null);
    set(svc, "fromEmail", "from@example.com");

    assertDoesNotThrow(() -> svc.sendResults("to@example.com", "subject", List.of(sampleMapDto())));
  }

  @Test
  @DisplayName("sendResults: skips when fromEmail missing")
  void sendResults_skips_whenFromEmailMissing() {
    MailtrapEmailService svc = new MailtrapEmailService(fmConfig());
    set(svc, "apiToken", "token");
    set(svc, "fromEmail", "");

    assertDoesNotThrow(() -> svc.sendResults("to@example.com", "subject", List.of(sampleMapDto())));
  }
}
