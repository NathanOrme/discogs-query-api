package org.discogs.query.domain.api.release;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the format of a release, such as CD, vinyl, etc. It also includes descriptions related
 * to the format.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Format {

  /** A list of descriptions associated with the format (e.g., "Limited Edition", "Remastered"). */
  private List<String> descriptions;

  /** The name of the format (e.g., "Vinyl", "CD"). */
  private String name;

  /** The quantity of the format available in the release. */
  private String qty;
}
