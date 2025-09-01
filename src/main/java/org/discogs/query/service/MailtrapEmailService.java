package org.discogs.query.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.mailtrap.config.MailtrapConfig;
import io.mailtrap.factory.MailtrapClientFactory;
import io.mailtrap.model.request.emails.Address;
import io.mailtrap.model.request.emails.MailtrapMail;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.helpers.LogHelper;
import org.discogs.query.interfaces.EmailService;
import org.discogs.query.model.DiscogsEntryDTO;
import org.discogs.query.model.DiscogsMapResultDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailtrapEmailService implements EmailService {

  private final Configuration freeMarkerConfiguration;

  @Value("${mailtrap.apiToken}")
  private String apiToken;

  @Value("${mailtrap.fromEmail}")
  private String fromEmail;

  @Value("${mailtrap.fromName:Discogs Query}")
  private String fromName;

  /**
   * Sends an email with the provided results to the specified recipient using Mailtrap.
   *
   * @param recipientEmail the recipient's email address
   * @param subject the subject of the email
   * @param results the list of DiscogsMapResultDTO to include in the email body
   */
  @Override
  public void sendResults(
      final String recipientEmail, final String subject, final List<DiscogsMapResultDTO> results) {
    if (recipientEmail == null || recipientEmail.isBlank()) {
      LogHelper.warn(() -> "Recipient email is blank; skipping email send");
      return;
    }
    if (apiToken == null || apiToken.isBlank()) {
      LogHelper.warn(() -> "Mailtrap API token not configured; skipping email send");
      return;
    }
    if (fromEmail == null || fromEmail.isBlank()) {
      LogHelper.warn(() -> "Mailtrap fromEmail not configured; skipping email send");
      return;
    }

    try {
      final var config = new MailtrapConfig.Builder().token(apiToken).build();
      final var client = MailtrapClientFactory.createMailtrapClient(config);

      String text = buildText(results);
      String html = buildHtml(results);

      final var mail =
          MailtrapMail.builder()
              .from(new Address(fromEmail, resolveFromName()))
              .to(List.of(new Address(recipientEmail)))
              .subject(subject == null || subject.isBlank() ? "Discogs search results" : subject)
              .text(text)
              .html(html)
              .build();

      client.sendingApi().emails().send(mail);
      LogHelper.info(() -> "Mail sent successfully via Mailtrap");
    } catch (final Exception e) {
      log.warn("Exception while sending email via Mailtrap", e);
    }
  }

  /**
   * Builds the email body text from the list of DiscogsMapResultDTO.
   *
   * @param results the list of DiscogsMapResultDTO
   * @return the formatted email body text
   */
  private String buildText(final List<DiscogsMapResultDTO> results) {
    try {
      final List<Map<String, Object>> resultsView = buildResultsView(results);

      final Template template = freeMarkerConfiguration.getTemplate("results.txt.ftl");
      final Map<String, Object> model = Map.of("results", resultsView);

      try (StringWriter writer = new StringWriter()) {
        template.process(model, writer);
        return writer.toString();
      }
    } catch (final TemplateException | java.io.IOException e) {
      log.warn("Failed to render FreeMarker text template; falling back to simple text", e);
      return "Discogs Query Results\n\n"; // minimal fallback
    }
  }

  /**
   * Builds a nicely formatted HTML email body from the list of DiscogsMapResultDTO.
   *
   * @param results the list of DiscogsMapResultDTO
   * @return HTML string for email body
   */
  private String buildHtml(final List<DiscogsMapResultDTO> results) {
    try {
      final List<Map<String, Object>> resultsView = buildResultsView(results);

      final Template template = freeMarkerConfiguration.getTemplate("results.ftl");
      final Map<String, Object> model = Map.of("results", resultsView);

      try (StringWriter writer = new StringWriter()) {
        template.process(model, writer);
        return writer.toString();
      }
    } catch (final TemplateException | java.io.IOException e) {
      log.warn("Failed to render FreeMarker template for email; falling back to empty body", e);
      return "";
    }
  }

  private List<Map<String, Object>> buildResultsView(final List<DiscogsMapResultDTO> results) {
    final List<Map<String, Object>> resultsView = new ArrayList<>();
    if (results == null) {
      return resultsView;
    }
    for (final DiscogsMapResultDTO resultDto : results) {
      final Map<String, Object> resultModel = new LinkedHashMap<>();
      resultModel.put("queryLabel", buildQueryLabel(resultDto));

      final DiscogsEntryDTO cheapestEntry = resultDto.cheapestItem();
      if (cheapestEntry != null) {
        final Map<String, Object> cheapestModel = new LinkedHashMap<>();
        cheapestModel.put("title", safe(cheapestEntry.title()));
        cheapestModel.put("link", resolveLink(cheapestEntry));
        cheapestModel.put("price", formatPrice(cheapestEntry.lowestPrice()));
        cheapestModel.put("meta", buildMetaForCheapest(cheapestEntry));
        resultModel.put("cheapest", cheapestModel);
      }

      final List<Map<String, Object>> groupsModel = new ArrayList<>();
      final Map<String, List<DiscogsEntryDTO>> groupedEntries = resultDto.results();
      if (groupedEntries != null && !groupedEntries.isEmpty()) {
        for (final Map.Entry<String, List<DiscogsEntryDTO>> groupEntry :
            groupedEntries.entrySet()) {
          final Map<String, Object> groupModel = new LinkedHashMap<>();
          groupModel.put("name", safe(groupEntry.getKey()));

          final List<Map<String, Object>> itemsModel = new ArrayList<>();
          for (final DiscogsEntryDTO entry : groupEntry.getValue()) {
            final Map<String, Object> itemModel = new LinkedHashMap<>();
            itemModel.put("title", safe(entry.title()));
            itemModel.put("link", resolveLink(entry));
            itemModel.put("meta", buildMetaForItem(entry));
            itemsModel.add(itemModel);
          }
          groupModel.put("items", itemsModel);
          groupsModel.add(groupModel);
        }
      }
      resultModel.put("groups", groupsModel);
      resultsView.add(resultModel);
    }
    return resultsView;
  }

  /**
   * Returns a safe, non-null string. If the input is null, returns an empty string.
   *
   * @param s the input string
   * @return the original string or an empty string if the input is null
   */
  private String safe(final String s) {
    return s == null ? "" : s;
  }

  /** Build a clickable link for an entry, preferring absolute URLs if present. */
  private String resolveLink(final DiscogsEntryDTO entry) {
    if (entry == null) {
      return "";
    }
    String uri = safe(entry.uri()).trim();
    if (!uri.isBlank()) {
      if (uri.startsWith("http://") || uri.startsWith("https://")) {
        return uri;
      }
      // Discogs often returns relative URIs like "/master/123"; prefix domain
      return "https://www.discogs.com" + uri;
    }
    String link = safe(entry.url()).trim();
    if (!link.isBlank()) {
      return link;
    }
    return "";
  }

  /**
   * Builds a label for the query based on available fields in the search query.
   *
   * @param mapDto the DiscogsMapResultDTO containing the search query
   * @return a formatted string representing the query
   */
  private String buildQueryLabel(final DiscogsMapResultDTO mapDto) {
    if (mapDto == null || mapDto.searchQuery() == null) {
      return "";
    }
    var query = mapDto.searchQuery();
    if (query.title() != null && !query.title().isBlank()) {
      return safe(query.title());
    }
    StringBuilder sb = new StringBuilder();
    if (query.artist() != null && !query.artist().isBlank()) {
      sb.append(query.artist());
    }
    if (query.album() != null && !query.album().isBlank()) {
      if (!sb.isEmpty()) {
        sb.append(" - ");
      }
      sb.append(query.album());
    }
    if (query.track() != null && !query.track().isBlank()) {
      if (!sb.isEmpty()) {
        sb.append(" - ");
      }
      sb.append(query.track());
    }
    return safe(sb.toString());
  }

  private String formatPrice(final Float price) {
    if (price == null) {
      return "N/A";
    }
    try {
      return String.format("%.2f", price);
    } catch (final Exception e) {
      return price.toString();
    }
  }

  private String buildMetaForCheapest(final DiscogsEntryDTO entry) {
    if (entry == null) {
      return "";
    }
    String meta = "";
    if (entry.year() != null && !entry.year().isBlank()) {
      meta += (meta.isEmpty() ? "" : " · ") + entry.year();
    }
    if (entry.country() != null && !entry.country().isBlank()) {
      meta += (meta.isEmpty() ? "" : " · ") + entry.country();
    }
    if (entry.numberForSale() != null) {
      meta += (meta.isEmpty() ? "" : " · ") + entry.numberForSale() + " for sale";
    }
    if (entry.format() != null && !entry.format().isEmpty()) {
      meta += (meta.isEmpty() ? "" : " · ") + String.join(", ", entry.format());
    }
    return meta;
  }

  private String buildMetaForItem(final DiscogsEntryDTO entry) {
    if (entry == null) {
      return "";
    }
    String meta = "";
    if (entry.year() != null && !entry.year().isBlank()) {
      meta += (meta.isEmpty() ? "" : " · ") + entry.year();
    }
    if (entry.country() != null && !entry.country().isBlank()) {
      meta += (meta.isEmpty() ? "" : " · ") + entry.country();
    }
    if (entry.format() != null && !entry.format().isEmpty()) {
      meta += (meta.isEmpty() ? "" : " · ") + String.join(", ", entry.format());
    }
    meta += (meta.isEmpty() ? "" : " · ") + formatPrice(entry.lowestPrice());
    return meta;
  }

  private String resolveFromName() {
    if (fromName != null && !fromName.isBlank()) {
      return fromName;
    }
    // Fallback: use the fromEmail as display name when no name is provided
    return fromEmail;
  }
}
