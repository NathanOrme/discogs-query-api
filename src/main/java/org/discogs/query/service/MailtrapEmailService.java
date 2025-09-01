package org.discogs.query.service;

import io.mailtrap.config.MailtrapConfig;
import io.mailtrap.factory.MailtrapClientFactory;
import io.mailtrap.model.request.emails.Address;
import io.mailtrap.model.request.emails.MailtrapMail;
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
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailtrapEmailService implements EmailService {

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

      final var mail =
          MailtrapMail.builder()
              .from(new Address(fromEmail, resolveFromName()))
              .to(List.of(new Address(recipientEmail)))
              .subject(subject == null || subject.isBlank() ? "Discogs search results" : subject)
              .text(text)
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
    StringBuilder sb = new StringBuilder();
    sb.append("Discogs Query Results\n\n");
    for (final DiscogsMapResultDTO mapDto : results) {
      sb.append("Query: ").append(buildQueryLabel(mapDto)).append("\n");

      DiscogsEntryDTO cheapest = mapDto.cheapestItem();
      if (cheapest != null) {
        sb.append("  Cheapest: ")
            .append(safe(cheapest.title()))
            .append(" — ")
            .append(formatPrice(cheapest.lowestPrice()))
            .append("\n");
      }

      Map<String, List<DiscogsEntryDTO>> grouped = mapDto.results();
      if (grouped != null && !grouped.isEmpty()) {
        for (final Map.Entry<String, List<DiscogsEntryDTO>> e : grouped.entrySet()) {
          sb.append("  ")
              .append(safe(e.getKey()))
              .append(": ")
              .append(
                  e.getValue().stream()
                      .map(en -> safe(en.title()))
                      .collect(Collectors.joining(", ")))
              .append("\n");
        }
      }
      sb.append("\n");
    }
    return sb.toString();
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
    var q = mapDto.searchQuery();
    if (q.title() != null && !q.title().isBlank()) {
      return safe(q.title());
    }
    StringBuilder sb = new StringBuilder();
    if (q.artist() != null && !q.artist().isBlank()) {
      sb.append(q.artist());
    }
    if (q.album() != null && !q.album().isBlank()) {
      if (!sb.isEmpty()) {
        sb.append(" - ");
      }
      sb.append(q.album());
    }
    if (q.track() != null && !q.track().isBlank()) {
      if (!sb.isEmpty()) {
        sb.append(" - ");
      }
      sb.append(q.track());
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

  private String resolveFromName() {
    if (fromName != null && !fromName.isBlank()) {
      return fromName;
    }
    // Fallback: use the fromEmail as display name when no name is provided
    return fromEmail;
  }
}
