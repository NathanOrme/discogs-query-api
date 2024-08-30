package org.discogs.query.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DiscogsMarketplaceResult {

    @JsonProperty("lowest_price")
    private DiscogsLowestPriceResult result;
    @JsonProperty("num_for_sale")
    private Integer numberForSale;
}
