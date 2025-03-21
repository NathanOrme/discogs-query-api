package org.discogs.query.domain.api.release;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a track in the release. This includes details such as the track title, duration,
 * position, and type.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Track {

  /** The duration of the track (e.g., "4:30"). */
  private String duration;

  /** The position of the track in the release (e.g., "A1", "B2"). */
  private String position;

  /** The title of the track. */
  private String title;

  private List<Artist> artists;

  /**
   * The type of track (e.g., "track" for a standard track, "index" for a track that is part of a
   * larger indexed track).
   */
  @JsonProperty("type_")
  private String type;
}
