package org.discogs.query.domain.api.release;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class ArtistDiffblueTest {
  /**
   * Methods under test:
   *
   * <ul>
   *   <li>{@link Artist#equals(Object)}
   *   <li>{@link Artist#hashCode()}
   * </ul>
   */
  @Test
  void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual() {
    // Arrange
    Artist artist = new Artist();
    artist.setAnv("Anv");
    artist.setId(1);
    artist.setJoin("Join");
    artist.setName("Name");
    artist.setResourceUrl("https://example.org/example");
    artist.setRole("Role");
    artist.setTracks("Tracks");

    Artist artist2 = new Artist();
    artist2.setAnv("Anv");
    artist2.setId(1);
    artist2.setJoin("Join");
    artist2.setName("Name");
    artist2.setResourceUrl("https://example.org/example");
    artist2.setRole("Role");
    artist2.setTracks("Tracks");

    // Act and Assert
    assertEquals(artist, artist2);
    int expectedHashCodeResult = artist.hashCode();
    assertEquals(expectedHashCodeResult, artist2.hashCode());
  }

  /**
   * Methods under test:
   *
   * <ul>
   *   <li>{@link Artist#equals(Object)}
   *   <li>{@link Artist#hashCode()}
   * </ul>
   */
  @Test
  void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual2() {
    // Arrange
    Artist artist = new Artist();
    artist.setAnv(null);
    artist.setId(1);
    artist.setJoin("Join");
    artist.setName("Name");
    artist.setResourceUrl("https://example.org/example");
    artist.setRole("Role");
    artist.setTracks("Tracks");

    Artist artist2 = new Artist();
    artist2.setAnv(null);
    artist2.setId(1);
    artist2.setJoin("Join");
    artist2.setName("Name");
    artist2.setResourceUrl("https://example.org/example");
    artist2.setRole("Role");
    artist2.setTracks("Tracks");

    // Act and Assert
    assertEquals(artist, artist2);
    int expectedHashCodeResult = artist.hashCode();
    assertEquals(expectedHashCodeResult, artist2.hashCode());
  }

  /**
   * Methods under test:
   *
   * <ul>
   *   <li>{@link Artist#equals(Object)}
   *   <li>{@link Artist#hashCode()}
   * </ul>
   */
  @Test
  void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual3() {
    // Arrange
    Artist artist = new Artist();
    artist.setAnv("Anv");
    artist.setId(1);
    artist.setJoin(null);
    artist.setName("Name");
    artist.setResourceUrl("https://example.org/example");
    artist.setRole("Role");
    artist.setTracks("Tracks");

    Artist artist2 = new Artist();
    artist2.setAnv("Anv");
    artist2.setId(1);
    artist2.setJoin(null);
    artist2.setName("Name");
    artist2.setResourceUrl("https://example.org/example");
    artist2.setRole("Role");
    artist2.setTracks("Tracks");

    // Act and Assert
    assertEquals(artist, artist2);
    int expectedHashCodeResult = artist.hashCode();
    assertEquals(expectedHashCodeResult, artist2.hashCode());
  }

  /**
   * Methods under test:
   *
   * <ul>
   *   <li>{@link Artist#equals(Object)}
   *   <li>{@link Artist#hashCode()}
   * </ul>
   */
  @Test
  void testEqualsAndHashCode_whenOtherIsSame_thenReturnEqual() {
    // Arrange
    Artist artist = new Artist();
    artist.setAnv("Anv");
    artist.setId(1);
    artist.setJoin("Join");
    artist.setName("Name");
    artist.setResourceUrl("https://example.org/example");
    artist.setRole("Role");
    artist.setTracks("Tracks");

    // Act and Assert
    assertEquals(artist, artist);
    int expectedHashCodeResult = artist.hashCode();
    assertEquals(expectedHashCodeResult, artist.hashCode());
  }

  /** Method under test: {@link Artist#equals(Object)} */
  @Test
  void testEquals_whenOtherIsDifferent_thenReturnNotEqual() {
    // Arrange
    Artist artist = new Artist();
    artist.setAnv("Join");
    artist.setId(1);
    artist.setJoin("Join");
    artist.setName("Name");
    artist.setResourceUrl("https://example.org/example");
    artist.setRole("Role");
    artist.setTracks("Tracks");

    Artist artist2 = new Artist();
    artist2.setAnv("Anv");
    artist2.setId(1);
    artist2.setJoin("Join");
    artist2.setName("Name");
    artist2.setResourceUrl("https://example.org/example");
    artist2.setRole("Role");
    artist2.setTracks("Tracks");

    // Act and Assert
    assertNotEquals(artist, artist2);
  }

  /** Method under test: {@link Artist#equals(Object)} */
  @Test
  void testEquals_whenOtherIsDifferent_thenReturnNotEqual2() {
    // Arrange
    Artist artist = new Artist();
    artist.setAnv(null);
    artist.setId(1);
    artist.setJoin("Join");
    artist.setName("Name");
    artist.setResourceUrl("https://example.org/example");
    artist.setRole("Role");
    artist.setTracks("Tracks");

    Artist artist2 = new Artist();
    artist2.setAnv("Anv");
    artist2.setId(1);
    artist2.setJoin("Join");
    artist2.setName("Name");
    artist2.setResourceUrl("https://example.org/example");
    artist2.setRole("Role");
    artist2.setTracks("Tracks");

    // Act and Assert
    assertNotEquals(artist, artist2);
  }

  /** Method under test: {@link Artist#equals(Object)} */
  @Test
  void testEquals_whenOtherIsDifferent_thenReturnNotEqual3() {
    // Arrange
    Artist artist = new Artist();
    artist.setAnv("Anv");
    artist.setId(2);
    artist.setJoin("Join");
    artist.setName("Name");
    artist.setResourceUrl("https://example.org/example");
    artist.setRole("Role");
    artist.setTracks("Tracks");

    Artist artist2 = new Artist();
    artist2.setAnv("Anv");
    artist2.setId(1);
    artist2.setJoin("Join");
    artist2.setName("Name");
    artist2.setResourceUrl("https://example.org/example");
    artist2.setRole("Role");
    artist2.setTracks("Tracks");

    // Act and Assert
    assertNotEquals(artist, artist2);
  }

  /** Method under test: {@link Artist#equals(Object)} */
  @Test
  void testEquals_whenOtherIsDifferent_thenReturnNotEqual4() {
    // Arrange
    Artist artist = new Artist();
    artist.setAnv("Anv");
    artist.setId(1);
    artist.setJoin("Anv");
    artist.setName("Name");
    artist.setResourceUrl("https://example.org/example");
    artist.setRole("Role");
    artist.setTracks("Tracks");

    Artist artist2 = new Artist();
    artist2.setAnv("Anv");
    artist2.setId(1);
    artist2.setJoin("Join");
    artist2.setName("Name");
    artist2.setResourceUrl("https://example.org/example");
    artist2.setRole("Role");
    artist2.setTracks("Tracks");

    // Act and Assert
    assertNotEquals(artist, artist2);
  }

  /** Method under test: {@link Artist#equals(Object)} */
  @Test
  void testEquals_whenOtherIsDifferent_thenReturnNotEqual5() {
    // Arrange
    Artist artist = new Artist();
    artist.setAnv("Anv");
    artist.setId(1);
    artist.setJoin(null);
    artist.setName("Name");
    artist.setResourceUrl("https://example.org/example");
    artist.setRole("Role");
    artist.setTracks("Tracks");

    Artist artist2 = new Artist();
    artist2.setAnv("Anv");
    artist2.setId(1);
    artist2.setJoin("Join");
    artist2.setName("Name");
    artist2.setResourceUrl("https://example.org/example");
    artist2.setRole("Role");
    artist2.setTracks("Tracks");

    // Act and Assert
    assertNotEquals(artist, artist2);
  }

  /** Method under test: {@link Artist#equals(Object)} */
  @Test
  void testEquals_whenOtherIsDifferent_thenReturnNotEqual6() {
    // Arrange
    Artist artist = new Artist();
    artist.setAnv("Anv");
    artist.setId(1);
    artist.setJoin("Join");
    artist.setName("Anv");
    artist.setResourceUrl("https://example.org/example");
    artist.setRole("Role");
    artist.setTracks("Tracks");

    Artist artist2 = new Artist();
    artist2.setAnv("Anv");
    artist2.setId(1);
    artist2.setJoin("Join");
    artist2.setName("Name");
    artist2.setResourceUrl("https://example.org/example");
    artist2.setRole("Role");
    artist2.setTracks("Tracks");

    // Act and Assert
    assertNotEquals(artist, artist2);
  }

  /** Method under test: {@link Artist#equals(Object)} */
  @Test
  void testEquals_whenOtherIsDifferent_thenReturnNotEqual7() {
    // Arrange
    Artist artist = new Artist();
    artist.setAnv("Anv");
    artist.setId(1);
    artist.setJoin("Join");
    artist.setName(null);
    artist.setResourceUrl("https://example.org/example");
    artist.setRole("Role");
    artist.setTracks("Tracks");

    Artist artist2 = new Artist();
    artist2.setAnv("Anv");
    artist2.setId(1);
    artist2.setJoin("Join");
    artist2.setName("Name");
    artist2.setResourceUrl("https://example.org/example");
    artist2.setRole("Role");
    artist2.setTracks("Tracks");

    // Act and Assert
    assertNotEquals(artist, artist2);
  }

  /** Method under test: {@link Artist#equals(Object)} */
  @Test
  void testEquals_whenOtherIsDifferent_thenReturnNotEqual8() {
    // Arrange
    Artist artist = new Artist();
    artist.setAnv("Anv");
    artist.setId(1);
    artist.setJoin("Join");
    artist.setName("Name");
    artist.setResourceUrl("Anv");
    artist.setRole("Role");
    artist.setTracks("Tracks");

    Artist artist2 = new Artist();
    artist2.setAnv("Anv");
    artist2.setId(1);
    artist2.setJoin("Join");
    artist2.setName("Name");
    artist2.setResourceUrl("https://example.org/example");
    artist2.setRole("Role");
    artist2.setTracks("Tracks");

    // Act and Assert
    assertNotEquals(artist, artist2);
  }

  /** Method under test: {@link Artist#equals(Object)} */
  @Test
  void testEquals_whenOtherIsDifferent_thenReturnNotEqual9() {
    // Arrange
    Artist artist = new Artist();
    artist.setAnv("Anv");
    artist.setId(1);
    artist.setJoin("Join");
    artist.setName("Name");
    artist.setResourceUrl(null);
    artist.setRole("Role");
    artist.setTracks("Tracks");

    Artist artist2 = new Artist();
    artist2.setAnv("Anv");
    artist2.setId(1);
    artist2.setJoin("Join");
    artist2.setName("Name");
    artist2.setResourceUrl("https://example.org/example");
    artist2.setRole("Role");
    artist2.setTracks("Tracks");

    // Act and Assert
    assertNotEquals(artist, artist2);
  }

  /** Method under test: {@link Artist#equals(Object)} */
  @Test
  void testEquals_whenOtherIsDifferent_thenReturnNotEqual10() {
    // Arrange
    Artist artist = new Artist();
    artist.setAnv("Anv");
    artist.setId(1);
    artist.setJoin("Join");
    artist.setName("Name");
    artist.setResourceUrl("https://example.org/example");
    artist.setRole("Anv");
    artist.setTracks("Tracks");

    Artist artist2 = new Artist();
    artist2.setAnv("Anv");
    artist2.setId(1);
    artist2.setJoin("Join");
    artist2.setName("Name");
    artist2.setResourceUrl("https://example.org/example");
    artist2.setRole("Role");
    artist2.setTracks("Tracks");

    // Act and Assert
    assertNotEquals(artist, artist2);
  }

  /** Method under test: {@link Artist#equals(Object)} */
  @Test
  void testEquals_whenOtherIsDifferent_thenReturnNotEqual11() {
    // Arrange
    Artist artist = new Artist();
    artist.setAnv("Anv");
    artist.setId(1);
    artist.setJoin("Join");
    artist.setName("Name");
    artist.setResourceUrl("https://example.org/example");
    artist.setRole(null);
    artist.setTracks("Tracks");

    Artist artist2 = new Artist();
    artist2.setAnv("Anv");
    artist2.setId(1);
    artist2.setJoin("Join");
    artist2.setName("Name");
    artist2.setResourceUrl("https://example.org/example");
    artist2.setRole("Role");
    artist2.setTracks("Tracks");

    // Act and Assert
    assertNotEquals(artist, artist2);
  }

  /** Method under test: {@link Artist#equals(Object)} */
  @Test
  void testEquals_whenOtherIsDifferent_thenReturnNotEqual12() {
    // Arrange
    Artist artist = new Artist();
    artist.setAnv("Anv");
    artist.setId(1);
    artist.setJoin("Join");
    artist.setName("Name");
    artist.setResourceUrl("https://example.org/example");
    artist.setRole("Role");
    artist.setTracks("Anv");

    Artist artist2 = new Artist();
    artist2.setAnv("Anv");
    artist2.setId(1);
    artist2.setJoin("Join");
    artist2.setName("Name");
    artist2.setResourceUrl("https://example.org/example");
    artist2.setRole("Role");
    artist2.setTracks("Tracks");

    // Act and Assert
    assertNotEquals(artist, artist2);
  }

  /** Method under test: {@link Artist#equals(Object)} */
  @Test
  void testEquals_whenOtherIsDifferent_thenReturnNotEqual13() {
    // Arrange
    Artist artist = new Artist();
    artist.setAnv("Anv");
    artist.setId(1);
    artist.setJoin("Join");
    artist.setName("Name");
    artist.setResourceUrl("https://example.org/example");
    artist.setRole("Role");
    artist.setTracks(null);

    Artist artist2 = new Artist();
    artist2.setAnv("Anv");
    artist2.setId(1);
    artist2.setJoin("Join");
    artist2.setName("Name");
    artist2.setResourceUrl("https://example.org/example");
    artist2.setRole("Role");
    artist2.setTracks("Tracks");

    // Act and Assert
    assertNotEquals(artist, artist2);
  }

  /** Method under test: {@link Artist#equals(Object)} */
  @Test
  void testEquals_whenOtherIsNull_thenReturnNotEqual() {
    // Arrange
    Artist artist = new Artist();
    artist.setAnv("Anv");
    artist.setId(1);
    artist.setJoin("Join");
    artist.setName("Name");
    artist.setResourceUrl("https://example.org/example");
    artist.setRole("Role");
    artist.setTracks("Tracks");

    // Act and Assert
    assertNotEquals(artist, null);
  }

  /** Method under test: {@link Artist#equals(Object)} */
  @Test
  void testEquals_whenOtherIsWrongType_thenReturnNotEqual() {
    // Arrange
    Artist artist = new Artist();
    artist.setAnv("Anv");
    artist.setId(1);
    artist.setJoin("Join");
    artist.setName("Name");
    artist.setResourceUrl("https://example.org/example");
    artist.setRole("Role");
    artist.setTracks("Tracks");

    // Act and Assert
    assertNotEquals(artist, "Different type to Artist");
  }

  /**
   * Methods under test:
   *
   * <ul>
   *   <li>default or parameterless constructor of {@link Artist}
   *   <li>{@link Artist#setAnv(String)}
   *   <li>{@link Artist#setId(int)}
   *   <li>{@link Artist#setJoin(String)}
   *   <li>{@link Artist#setName(String)}
   *   <li>{@link Artist#setResourceUrl(String)}
   *   <li>{@link Artist#setRole(String)}
   *   <li>{@link Artist#setTracks(String)}
   *   <li>{@link Artist#toString()}
   *   <li>{@link Artist#getAnv()}
   *   <li>{@link Artist#getId()}
   *   <li>{@link Artist#getJoin()}
   *   <li>{@link Artist#getName()}
   *   <li>{@link Artist#getResourceUrl()}
   *   <li>{@link Artist#getRole()}
   *   <li>{@link Artist#getTracks()}
   * </ul>
   */
  @Test
  void testGettersAndSetters() {
    // Arrange and Act
    Artist actualArtist = new Artist();
    actualArtist.setAnv("Anv");
    actualArtist.setId(1);
    actualArtist.setJoin("Join");
    actualArtist.setName("Name");
    actualArtist.setResourceUrl("https://example.org/example");
    actualArtist.setRole("Role");
    actualArtist.setTracks("Tracks");
    String actualToStringResult = actualArtist.toString();
    String actualAnv = actualArtist.getAnv();
    int actualId = actualArtist.getId();
    String actualJoin = actualArtist.getJoin();
    String actualName = actualArtist.getName();
    String actualResourceUrl = actualArtist.getResourceUrl();
    String actualRole = actualArtist.getRole();

    // Assert that nothing has changed
    assertEquals("Anv", actualAnv);
    assertEquals(
        "Artist(anv=Anv, id=1, join=Join, name=Name, resourceUrl=https://example.org/example,"
            + " role=Role, tracks=Tracks)",
        actualToStringResult);
    assertEquals("Join", actualJoin);
    assertEquals("Name", actualName);
    assertEquals("Role", actualRole);
    assertEquals("Tracks", actualArtist.getTracks());
    assertEquals("https://example.org/example", actualResourceUrl);
    assertEquals(1, actualId);
  }
}
