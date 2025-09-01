package org.discogs.query.service;

import io.mailtrap.config.MailtrapConfig;
import io.mailtrap.factory.MailtrapClientFactory;
import io.mailtrap.model.request.emails.Address;
import io.mailtrap.model.request.emails.MailtrapMail;
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
    StringBuilder sb = new StringBuilder();
    sb.append("Discogs Query Results\n");
    sb.append("======================\n\n");

    if (results == null || results.isEmpty()) {
      sb.append("No results to display.\n");
      return sb.toString();
    }

    int idx = 1;
    for (final DiscogsMapResultDTO mapDto : results) {
      String queryLabel = buildQueryLabel(mapDto);
      sb.append(idx++).append(") Query: ").append(queryLabel).append("\n");

      DiscogsEntryDTO cheapest = mapDto.cheapestItem();
      if (cheapest != null) {
        sb.append("  • Cheapest: ")
            .append(safe(cheapest.title()))
            .append(" — ")
            .append(formatPrice(cheapest.lowestPrice()));

        // metadata
        String meta = "";
        if (cheapest.year() != null && !cheapest.year().isBlank()) meta += (meta.isEmpty()?"":" · ") + cheapest.year();
        if (cheapest.country() != null && !cheapest.country().isBlank()) meta += (meta.isEmpty()?"":" · ") + cheapest.country();
        if (cheapest.format() != null && !cheapest.format().isEmpty()) meta += (meta.isEmpty()?"":" · ") + String.join(", ", cheapest.format());
        if (cheapest.numberForSale() != null) meta += (meta.isEmpty()?"":" · ") + cheapest.numberForSale() + " for sale";
        if (!meta.isEmpty()) {
          sb.append(" (" ).append(meta).append(")");
        }

        // link
        String link = resolveLink(cheapest);
        if (link != null && !link.isBlank()) {
          sb.append("\n    → ").append(link);
        }
        sb.append("\n");
      }

      Map<String, List<DiscogsEntryDTO>> grouped = mapDto.results();
      if (grouped != null && !grouped.isEmpty()) {
        for (final Map.Entry<String, List<DiscogsEntryDTO>> e : grouped.entrySet()) {
          sb.append("  ").append(safe(e.getKey())).append(":\n");
          for (DiscogsEntryDTO en : e.getValue()) {
            sb.append("    - ").append(safe(en.title()));
            String meta = "";
            if (en.year() != null && !en.year().isBlank()) meta += (meta.isEmpty()?"":" · ") + en.year();
            if (en.country() != null && !en.country().isBlank()) meta += (meta.isEmpty()?"":" · ") + en.country();
            if (en.format() != null && !en.format().isEmpty()) meta += (meta.isEmpty()?"":" · ") + String.join(", ", en.format());
            if (!meta.isEmpty()) {
              sb.append(" (" ).append(meta).append(")");
            }
            String link = resolveLink(en);
            if (link != null && !link.isBlank()) {
              sb.append("\n      → ").append(link);
            }
            sb.append("\n");
          }
        }
      } else {
        sb.append("  No grouped results.\n");
      }

      sb.append("\n");
      sb.append("------------------------------------------------------------\n\n");
    }
    return sb.toString();
  }

  /**
   * Builds a nicely formatted HTML email body from the list of DiscogsMapResultDTO.
   *
   * @param results the list of DiscogsMapResultDTO
   * @return HTML string for email body
   */
  private String buildHtml(final List<DiscogsMapResultDTO> results) {
    StringBuilder sb = new StringBuilder();
    sb.append("<!DOCTYPE html>");
    sb.append("<html lang=\"en\">\n");
    sb.append("<head>\n");
    sb.append("  <meta charset=\"UTF-8\"/>\n");
    sb.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/>\n");
    sb.append("  <title>Discogs Query Results</title>\n");
    sb.append("</head>\n");
    sb.append("<body style=\"margin:0;padding:24px;background:#f9fafb;color:#1f2937;font-family:Arial,Helvetica,sans-serif;\">\n");
    sb.append("  <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n");
    sb.append("    <tr>\n");
    sb.append("      <td align=\"center\">\n");
    sb.append("        <table role=\"presentation\" width=\"720\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"max-width:720px;width:100%;background:#ffffff;border:1px solid #e5e7eb;border-radius:10px;box-shadow:0 1px 2px rgba(0,0,0,0.06);overflow:hidden;\">\n");
    sb.append("          <tr>\n");
    sb.append("            <td style=\"background:#111827;color:#ffffff;padding:16px 20px;\"><h1 style=\"margin:0;font-size:20px;\">Discogs Query Results</h1></td>\n");
    sb.append("          </tr>\n");
    sb.append("          <tr>\n");
    sb.append("            <td style=\"padding:20px;\">\n");

    for (final DiscogsMapResultDTO mapDto : results) {
      sb.append("              <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"margin:0 0 16px 0;\">\n");
      sb.append("                <tr>\n");
      sb.append("                  <td style=\"background:#f3f4f6;border-left:4px solid #3b82f6;border-radius:6px;padding:12px;\">\n");
      sb.append("                    <div style=\"font-weight:700;color:#111827;margin:0 0 8px 0;\">Query: ")
          .append(escapeHtml(buildQueryLabel(mapDto)))
          .append("</div>\n");

      DiscogsEntryDTO cheapest = mapDto.cheapestItem();
      if (cheapest != null) {
        String cheapestTitle = escapeHtml(safe(cheapest.title()));
        String cheapestLink = resolveLink(cheapest);
        sb.append("                    <div style=\"color:#065f46;background:#ecfdf5;border:1px solid #a7f3d0;border-radius:6px;padding:8px 10px;margin:8px 0;display:inline-block;\">");
        if (!cheapestLink.isEmpty()) {
          sb.append("<a href=\"").append(escapeHtml(cheapestLink)).append("\" style=\"color:#065f46;text-decoration:none;font-weight:600;\">")
              .append(cheapestTitle)
              .append("</a>");
        } else {
          sb.append(cheapestTitle);
        }
        sb.append(" — ").append(escapeHtml(formatPrice(cheapest.lowestPrice())));
        // metadata line
        String meta = "";
        if (cheapest.year() != null && !cheapest.year().isBlank()) meta += (meta.isEmpty()?"":" · ") + escapeHtml(cheapest.year());
        if (cheapest.country() != null && !cheapest.country().isBlank()) meta += (meta.isEmpty()?"":" · ") + escapeHtml(cheapest.country());
        if (cheapest.numberForSale() != null) meta += (meta.isEmpty()?"":" · ") + escapeHtml(cheapest.numberForSale().toString()) + " for sale";
        if (cheapest.format() != null && !cheapest.format().isEmpty()) meta += (meta.isEmpty()?"":" · ") + escapeHtml(String.join(", ", cheapest.format()));
        if (!meta.isEmpty()) {
          sb.append("<div style=\"font-size:12px;color:#065f46;opacity:0.9;margin-top:4px;\">")
              .append(meta)
              .append("</div>");
        }
        sb.append("                    </div>\n");
      }

      Map<String, List<DiscogsEntryDTO>> grouped = mapDto.results();
      if (grouped != null && !grouped.isEmpty()) {
        for (final Map.Entry<String, List<DiscogsEntryDTO>> e : grouped.entrySet()) {
          sb.append("                    <div style=\"margin-top:8px;\">\n");
          sb.append("                      <div style=\"font-weight:600;color:#374151;margin-bottom:4px;\">")
              .append(escapeHtml(safe(e.getKey())))
              .append(":</div>\n");
          sb.append("                      <ul style=\"margin:6px 0 0 18px;padding:0;\">\n");
          for (DiscogsEntryDTO en : e.getValue()) {
            String title = escapeHtml(safe(en.title()));
            String link = resolveLink(en);
            sb.append("                        <li style=\"margin:2px 0;\">");
            if (!link.isEmpty()) {
              sb.append("<a href=\"").append(escapeHtml(link)).append("\" style=\"color:#1d4ed8;text-decoration:none;\">")
                  .append(title)
                  .append("</a>");
            } else {
              sb.append(title);
            }
            // small meta after title
            String meta = "";
            if (en.year() != null && !en.year().isBlank()) meta += (meta.isEmpty()?"":" · ") + escapeHtml(en.year());
            if (en.country() != null && !en.country().isBlank()) meta += (meta.isEmpty()?"":" · ") + escapeHtml(en.country());
            if (en.format() != null && !en.format().isEmpty()) meta += (meta.isEmpty()?"":" · ") + escapeHtml(String.join(", ", en.format()));
            if (!meta.isEmpty()) {
              sb.append(" <span style=\"color:#6b7280;font-size:12px;\">").append(meta).append("</span>");
            }
            sb.append("</li>\n");
          }
          sb.append("                      </ul>\n");
          sb.append("                    </div>\n");
        }
      }

      sb.append("                  </td>\n");
      sb.append("                </tr>\n");
      sb.append("              </table>\n");
    }

    sb.append("            </td>\n");
    sb.append("          </tr>\n");
    sb.append("          <tr>\n");
    sb.append("            <td style=\"font-size:12px;color:#6b7280;padding:16px 20px;border-top:1px solid #e5e7eb;background:#f9fafb;\">Sent by Discogs Query</td>\n");
    sb.append("          </tr>\n");
    sb.append("        </table>\n");
    sb.append("      </td>\n");
    sb.append("    </tr>\n");
    sb.append("  </table>\n");
    sb.append("</body>\n</html>");
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
   * Basic HTML escaping to avoid breaking markup in the email.
   */
  private String escapeHtml(final String input) {
    if (input == null) {
      return "";
    }
    String out = input;
    out = out.replace("&", "&amp;");
    out = out.replace("<", "&lt;");
    out = out.replace(">", "&gt;");
    out = out.replace("\"", "&quot;");
    out = out.replace("'", "&#39;");
    return out;
  }

  /**
   * Build a clickable link for an entry, preferring absolute URLs if present.
   */
  private String resolveLink(final DiscogsEntryDTO en) {
    if (en == null) return "";
    String link = en.url();
    if (link != null && !link.isBlank()) {
      return link;
    }
    String uri = en.uri();
    if (uri != null && !uri.isBlank()) {
      if (uri.startsWith("http://") || uri.startsWith("https://")) {
        return uri;
      }
      // Discogs often returns relative URIs like "/master/123"; prefix domain
      return "https://www.discogs.com" + uri;
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
