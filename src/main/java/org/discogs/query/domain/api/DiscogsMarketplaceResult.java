package org.discogs.query.domain.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents the result of a query to the Discogs Marketplace API.
 *
 * <p>This class encapsulates information about the lowest price and the number of items for sale in
 * the Discogs Marketplace. It uses Jackson annotations to map JSON properties to the fields of the
 * class.
 */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DiscogsMarketplaceResult {

  /**
   * The lowest price result for an item.
   *
   * <p>This field contains details about the lowest price of the item, including the currency and
   * the value.
   */
  @JsonProperty("lowest_price")
  private DiscogsLowestPriceResult result;

  /**
   * The number of items available for sale.
   *
   * <p>This field contains the total number of items currently listed for sale in the Discogs
   * Marketplace.
   */
  @JsonProperty("num_for_sale")
  private Integer numberForSale;
}
