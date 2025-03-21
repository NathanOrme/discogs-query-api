package org.discogs.query.domain.api;

import java.util.List;
import java.util.Map;

/**
 * DTO to reflect the API response for what we get from Discogs Collection
 *
 * @param pagination Pagination details
 * @param releases The releases we got
 */
public record DiscogsCollectionRelease(Pagination pagination, List<Release> releases) {
  public record Pagination(
      int per_page, int items, int page, Map<String, String> urls, int pages) {}

  public record Release(
      long instance_id,
      int rating,
      BasicInformation basic_information,
      long folder_id,
      String date_added,
      long id) {}

  public record BasicInformation(
      List<Label> labels,
      List<Format> formats,
      String thumb,
      String title,
      List<Artist> artists,
      String resource_url,
      int year,
      long id) {}

  public record Label(
      String name,
      String entity_type,
      String catno,
      String resource_url,
      long id,
      String entity_type_name) {}

  public record Format(List<String> descriptions, String name, String qty) {}

  public record Artist(
      String join,
      String name,
      String anv,
      String tracks,
      String role,
      String resource_url,
      long id) {}
}
