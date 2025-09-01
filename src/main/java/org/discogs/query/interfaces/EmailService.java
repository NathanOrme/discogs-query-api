package org.discogs.query.interfaces;

import java.util.List;
import org.discogs.query.model.DiscogsMapResultDTO;

public interface EmailService {
  void sendResults(String recipientEmail, String subject, List<DiscogsMapResultDTO> results);
}
