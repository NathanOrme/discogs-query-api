package org.discogs.query.domain.website;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Represents a result from the Discogs Marketplace scraping process. */
@Data
@AllArgsConstructor
public class DiscogsWebsiteResult {
  private String price;
  private String condition;
  private String shipsFrom;
  private String sellerName;
  private String sellerRating;
  private String ratingCount;
}
