package org.discogs.query.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum representing different query parameters available for searching in the Discogs API.
 *
 * <p>This enum provides predefined constants for various query parameters, such as track, format,
 * artist, and type. Each constant is associated with a query type string that represents its name
 * in the Discogs API.
 */
@Getter
@RequiredArgsConstructor
public enum DiscogQueryParams {

  /** Represents the "track" query parameter. */
  TRACK("track"),

  /** Represents the "title" query parameter. */
  TITLE("title"),

  /** Represents the "q" query parameter. */
  Q("q"),

  /** Represents the "format" query parameter. */
  FORMAT("format"),

  /** Represents the "artist" query parameter. */
  ARTIST("artist"),

  /** Represents the "type" query parameter. */
  TYPE("type"),

  /** Represents the "album" query parameter */
  ALBUM("release_title"),

  /** Represents the "country" query parameter */
  COUNTRY("country"),
  /** Represents the "barcode" query parameter */
  BARCODE("barcode");

  /** The query type string associated with the enum constant. */
  private final String queryType;
}
