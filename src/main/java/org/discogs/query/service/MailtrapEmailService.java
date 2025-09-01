package org.discogs.query.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.helpers.LogHelper;
import org.discogs.query.interfaces.EmailService;
import org.discogs.query.model.DiscogsEntryDTO;
import org.discogs.query.model.DiscogsMapResultDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailtrapEmailService implements EmailService {

  @Value("${mailtrap.apiToken}")
  private String apiToken;

  @Value("${mailtrap.endpoint:https://send.api.mailtrap.io/api/send}")
  private String endpoint;

  @Value("${mailtrap.fromEmail}")
  private String fromEmail;

  @Value("${mailtrap.fromName:Discogs Query}")
  private String fromName;

  private final HttpClient httpClient =
      HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

  @Override
  public void sendResults(String recipientEmail, String subject, List<DiscogsMapResultDTO> results) {
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

    String html = buildHtml(results);

    String jsonPayload = buildJsonPayload(recipientEmail, subject, html);

    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create(endpoint))
            .timeout(Duration.ofSeconds(15))
            .header("Authorization", "Bearer " + apiToken)
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .POST(HttpRequest.BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8))
            .build();

    try {
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      int status = response.statusCode();
      if (status >= 200 && status < 300) {
        LogHelper.info(() -> "Mail sent successfully via Mailtrap");
      } else {
        final String body = response.body();
        LogHelper.warn(() -> "Mailtrap send failed with status {} and body {}", status, body);
      }
    } catch (Exception e) {
      log.warn("Exception while sending email via Mailtrap", e);
    }
  }

  private String buildJsonPayload(String to, String subject, String html) {
    // Minimal JSON for Mailtrap Send API v1
    // See: https://mailtrap.io/api
    String escapedHtml = escapeForJson(html);
    String escapedSubject = escapeForJson(subject);
    String escapedFromName = escapeForJson(fromName);
    return "{" +
        "\"from\": {\"email\": \"" + escapeForJson(fromEmail) + "\", \"name\": \"" + escapedFromName + "\"}," +
        "\"to\":[{\"email\":\"" + escapeForJson(to) + "\"}]," +
        "\"subject\": \"" + escapedSubject + "\"," +
        "\"content\":[{\"type\":\"text/html\",\"value\":\"" + escapedHtml + "\"}]" +
        "}";
  }

  private String escapeForJson(String s) {
    if (s == null) return "";
    return s.replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r");
  }

  private String buildHtml(List<DiscogsMapResultDTO> results) {
    StringBuilder sb = new StringBuilder();
    sb.append("<html><body>");
    sb.append("<h2>Discogs Query Results</h2>");

    for (DiscogsMapResultDTO mapDto : results) {
      sb.append("<div style='margin-bottom:16px'>");
      sb.append("<h3>Query: ")
          .append(buildQueryLabel(mapDto))
          .append("</h3>");

      DiscogsEntryDTO cheapest = mapDto.cheapestItem();
      if (cheapest != null) {
        sb.append("<p><b>Cheapest:</b> ")
            .append(safe(cheapest.title()))
            .append(" — ")
            .append(formatPrice(cheapest.lowestPrice()))
            .append("</p>");
      }

      Map<String, List<DiscogsEntryDTO>> grouped = mapDto.results();
      if (grouped != null && !grouped.isEmpty()) {
        for (Map.Entry<String, List<DiscogsEntryDTO>> e : grouped.entrySet()) {
          sb.append("<p><b>")
              .append(safe(e.getKey()))
              .append(":</b> ")
              .append(e.getValue().stream()
                  .map(en -> safe(en.title()))
                  .collect(Collectors.joining(", ")))
              .append("</p>");
        }
      }
      sb.append("</div>");
    }

    sb.append("</body></html>");
    return sb.toString();
  }

  private String safe(String s) {
    return s == null ? "" : s;
  }

  private String buildQueryLabel(DiscogsMapResultDTO mapDto) {
    if (mapDto == null || mapDto.searchQuery() == null) return "";
    var q = mapDto.searchQuery();
    // Prefer explicit title; otherwise build from artist, album/track
    if (q.title() != null && !q.title().isBlank()) {
      return safe(q.title());
    }
    StringBuilder sb = new StringBuilder();
    if (q.artist() != null && !q.artist().isBlank()) sb.append(q.artist());
    if (q.album() != null && !q.album().isBlank()) {
      if (!sb.isEmpty()) sb.append(" - ");
      sb.append(q.album());
    }
    if (q.track() != null && !q.track().isBlank()) {
      if (!sb.isEmpty()) sb.append(" - ");
      sb.append(q.track());
    }
    return safe(sb.toString());
  }

  private String formatPrice(Float price) {
    if (price == null) return "N/A";
    try {
      return String.format("%.2f", price);
    } catch (Exception e) {
      return price.toString();
    }
  }
}
