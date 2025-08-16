package org.discogs.query.service.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.enums.DiscogCountries;
import org.discogs.query.model.enums.DiscogsTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {NormalizationServiceImpl.class})
@ExtendWith(SpringExtension.class)
class NormalizationServiceImplDiffblueTest {
  @Autowired private NormalizationServiceImpl normalizationService;

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String)")
  void testNormalizeString() {
    // Arrange, Act and Assert
    assertEquals("", normalizationService.normalizeString("̀̀̀"));
    assertEquals(
        "p{InCombiningDiacriticalMarks} p{InCombiningDiacriticalMarks}",
        normalizationService.normalizeString(
            "̀\\p{InCombiningDiacriticalMarks}\\p" + "{InCombiningDiacriticalMarks}"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>Then return {@code 42 p{InCombiningDiacriticalMarks}}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); then return '42 p{InCombiningDiacriticalMarks}'")
  void testNormalizeString_thenReturn42PInCombiningDiacriticalMarks() {
    // Arrange, Act and Assert
    assertEquals(
        "42 p{InCombiningDiacriticalMarks}",
        normalizationService.normalizeString("̀42\\p{InCombiningDiacriticalMarks}"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>Then return {@code Input p{InCombiningDiacriticalMarks}}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); then return 'Input p{InCombiningDiacriticalMarks}'")
  void testNormalizeString_thenReturnInputPInCombiningDiacriticalMarks() {
    // Arrange, Act and Assert
    assertEquals(
        "Input p{InCombiningDiacriticalMarks}",
        normalizationService.normalizeString("̀Input\\p{InCombiningDiacriticalMarks}"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>Then return {@code p{InCombiningDiacriticalMarks}42}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); then return 'p{InCombiningDiacriticalMarks}42'")
  void testNormalizeString_thenReturnPInCombiningDiacriticalMarks42() {
    // Arrange, Act and Assert
    assertEquals(
        "p{InCombiningDiacriticalMarks}42",
        normalizationService.normalizeString("̀\\p{InCombiningDiacriticalMarks}42"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>Then return {@code p{InCombiningDiacriticalMarks}Input}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); then return 'p{InCombiningDiacriticalMarks}Input'")
  void testNormalizeString_thenReturnPInCombiningDiacriticalMarksInput() {
    // Arrange, Act and Assert
    assertEquals(
        "p{InCombiningDiacriticalMarks}Input",
        normalizationService.normalizeString("̀\\p{InCombiningDiacriticalMarks}Input"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>Then return {@code p{InCombiningDiacriticalMarks} s+}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); then return 'p{InCombiningDiacriticalMarks} s+'")
  void testNormalizeString_thenReturnPInCombiningDiacriticalMarksS() {
    // Arrange, Act and Assert
    assertEquals(
        "p{InCombiningDiacriticalMarks} s+",
        normalizationService.normalizeString("̀\\p{InCombiningDiacriticalMarks}\\s+"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>Then return {@code s+ p{InCombiningDiacriticalMarks}}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); then return 's+ p{InCombiningDiacriticalMarks}'")
  void testNormalizeString_thenReturnSPInCombiningDiacriticalMarks() {
    // Arrange, Act and Assert
    assertEquals(
        "s+ p{InCombiningDiacriticalMarks}",
        normalizationService.normalizeString("̀\\s+\\p{InCombiningDiacriticalMarks}"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code ̀42Input}.
   *   <li>Then return {@code 42Input}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '̀42Input'; then return '42Input'")
  void testNormalizeString_when42Input_thenReturn42Input() {
    // Arrange, Act and Assert
    assertEquals("42Input", normalizationService.normalizeString("̀42Input"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code ̀42\s+}.
   *   <li>Then return {@code 42 s+}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '̀42\\s+'; then return '42 s+'")
  void testNormalizeString_when42S_thenReturn42S() {
    // Arrange, Act and Assert
    assertEquals("42 s+", normalizationService.normalizeString("̀42\\s+"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code ̀42}.
   *   <li>Then return {@code 42}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '̀42'; then return '42'")
  void testNormalizeString_when42_thenReturn42() {
    // Arrange, Act and Assert
    assertEquals("42", normalizationService.normalizeString("̀42"));
    assertEquals("42", normalizationService.normalizeString("42̀"));
    assertEquals("42", normalizationService.normalizeString("̀̀42"));
    assertEquals("42", normalizationService.normalizeString("̀ 42"));
    assertEquals("42", normalizationService.normalizeString("̀42̀"));
    assertEquals("42", normalizationService.normalizeString("̀42 "));
    assertEquals("42", normalizationService.normalizeString("̀42!"));
    assertEquals("42", normalizationService.normalizeString("̀42*"));
    assertEquals("42", normalizationService.normalizeString("̀42\\"));
    assertEquals("42", normalizationService.normalizeString("̀!42"));
    assertEquals("42", normalizationService.normalizeString("̀*42"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code ̀4242}.
   *   <li>Then return {@code 4242}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '̀4242'; then return '4242'")
  void testNormalizeString_when4242_thenReturn4242() {
    // Arrange, Act and Assert
    assertEquals("4242", normalizationService.normalizeString("̀4242"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code *̀}.
   *   <li>Then return empty string.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '*̀'; then return empty string")
  void testNormalizeString_whenAsteriskCombiningGraveAccent_thenReturnEmptyString() {
    // Arrange, Act and Assert
    assertEquals("", normalizationService.normalizeString("*̀"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code \̀}.
   *   <li>Then return empty string.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '\\̀'; then return empty string")
  void testNormalizeString_whenBackslashCombiningGraveAccent_thenReturnEmptyString() {
    // Arrange, Act and Assert
    assertEquals("", normalizationService.normalizeString("\\̀"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code ̀**}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '̀**'")
  void testNormalizeString_whenCombiningGraveAccentAsteriskAsterisk() {
    // Arrange, Act and Assert
    assertEquals("", normalizationService.normalizeString("̀**"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code ̀*\}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '̀*\\'")
  void testNormalizeString_whenCombiningGraveAccentAsteriskBackslash() {
    // Arrange, Act and Assert
    assertEquals("", normalizationService.normalizeString("̀*\\"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code ̀*̀}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '̀*̀'")
  void testNormalizeString_whenCombiningGraveAccentAsteriskCombiningGraveAccent() {
    // Arrange, Act and Assert
    assertEquals("", normalizationService.normalizeString("̀*̀"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code ̀*!}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '̀*!'")
  void testNormalizeString_whenCombiningGraveAccentAsteriskExclamationMark() {
    // Arrange, Act and Assert
    assertEquals("", normalizationService.normalizeString("̀*!"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code ̀*}.
   *   <li>Then return empty string.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '̀*'; then return empty string")
  void testNormalizeString_whenCombiningGraveAccentAsterisk_thenReturnEmptyString() {
    // Arrange, Act and Assert
    assertEquals("", normalizationService.normalizeString("̀*"));
    assertEquals("", normalizationService.normalizeString("̀* "));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code ̀\̀}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '̀\\̀'")
  void testNormalizeString_whenCombiningGraveAccentBackslashCombiningGraveAccent() {
    // Arrange, Act and Assert
    assertEquals("", normalizationService.normalizeString("̀\\̀"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code ̀\}.
   *   <li>Then return empty string.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '̀\\'; then return empty string")
  void testNormalizeString_whenCombiningGraveAccentBackslash_thenReturnEmptyString() {
    // Arrange, Act and Assert
    assertEquals("", normalizationService.normalizeString("̀\\"));
    assertEquals("", normalizationService.normalizeString("̀\\ "));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code ̀̀}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '̀̀'")
  void testNormalizeString_whenCombiningGraveAccentCombiningGraveAccent() {
    // Arrange, Act and Assert
    assertEquals("", normalizationService.normalizeString("̀̀"));
    assertEquals("", normalizationService.normalizeString("̀̀ "));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code ̀̀*}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '̀̀*'")
  void testNormalizeString_whenCombiningGraveAccentCombiningGraveAccentAsterisk() {
    // Arrange, Act and Assert
    assertEquals("", normalizationService.normalizeString("̀̀*"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code ̀̀\}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '̀̀\\'")
  void testNormalizeString_whenCombiningGraveAccentCombiningGraveAccentBackslash() {
    // Arrange, Act and Assert
    assertEquals("", normalizationService.normalizeString("̀̀\\"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code ̀̀!}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '̀̀!'")
  void testNormalizeString_whenCombiningGraveAccentCombiningGraveAccentExclamationMark() {
    // Arrange, Act and Assert
    assertEquals("", normalizationService.normalizeString("̀̀!"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code ̀!}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '̀!'")
  void testNormalizeString_whenCombiningGraveAccentExclamationMark() {
    // Arrange, Act and Assert
    assertEquals("", normalizationService.normalizeString("̀!"));
    assertEquals("", normalizationService.normalizeString("̀! "));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code ̀!*}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '̀!*'")
  void testNormalizeString_whenCombiningGraveAccentExclamationMarkAsterisk() {
    // Arrange, Act and Assert
    assertEquals("", normalizationService.normalizeString("̀!*"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code ̀!\}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '̀!\\'")
  void testNormalizeString_whenCombiningGraveAccentExclamationMarkBackslash() {
    // Arrange, Act and Assert
    assertEquals("", normalizationService.normalizeString("̀!\\"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code ̀!̀}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '̀!̀'")
  void testNormalizeString_whenCombiningGraveAccentExclamationMarkCombiningGraveAccent() {
    // Arrange, Act and Assert
    assertEquals("", normalizationService.normalizeString("̀!̀"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code ̀!!}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '̀!!'")
  void testNormalizeString_whenCombiningGraveAccentExclamationMarkExclamationMark() {
    // Arrange, Act and Assert
    assertEquals("", normalizationService.normalizeString("̀!!"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code ̀ *}.
   *   <li>Then return empty string.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '̀ *'; then return empty string")
  void testNormalizeString_whenCombiningGraveAccentSpaceAsterisk_thenReturnEmptyString() {
    // Arrange, Act and Assert
    assertEquals("", normalizationService.normalizeString("̀ *"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code ̀ \}.
   *   <li>Then return empty string.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '̀ \\'; then return empty string")
  void testNormalizeString_whenCombiningGraveAccentSpaceBackslash_thenReturnEmptyString() {
    // Arrange, Act and Assert
    assertEquals("", normalizationService.normalizeString("̀ \\"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code ̀ ̀}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '̀ ̀'")
  void testNormalizeString_whenCombiningGraveAccentSpaceCombiningGraveAccent() {
    // Arrange, Act and Assert
    assertEquals("", normalizationService.normalizeString("̀ ̀"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code ̀ !}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '̀ !'")
  void testNormalizeString_whenCombiningGraveAccentSpaceExclamationMark() {
    // Arrange, Act and Assert
    assertEquals("", normalizationService.normalizeString("̀ !"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code ̀}.
   *   <li>Then return empty string.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '̀'; then return empty string")
  void testNormalizeString_whenCombiningGraveAccent_thenReturnEmptyString() {
    // Arrange, Act and Assert
    assertEquals("", normalizationService.normalizeString("̀"));
    assertEquals("", normalizationService.normalizeString("̀ "));
    assertEquals("", normalizationService.normalizeString(" ̀"));
    assertEquals("", normalizationService.normalizeString("̀  "));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code !̀}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '!̀'")
  void testNormalizeString_whenExclamationMarkCombiningGraveAccent() {
    // Arrange, Act and Assert
    assertEquals("", normalizationService.normalizeString("!̀"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code ̀Input42}.
   *   <li>Then return {@code Input42}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '̀Input42'; then return 'Input42'")
  void testNormalizeString_whenInput42_thenReturnInput42() {
    // Arrange, Act and Assert
    assertEquals("Input42", normalizationService.normalizeString("̀Input42"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code ̀InputInput}.
   *   <li>Then return {@code InputInput}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '̀InputInput'; then return 'InputInput'")
  void testNormalizeString_whenInputInput_thenReturnInputInput() {
    // Arrange, Act and Assert
    assertEquals("InputInput", normalizationService.normalizeString("̀InputInput"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code ̀Input\s+}.
   *   <li>Then return {@code Input s+}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '̀Input\\s+'; then return 'Input s+'")
  void testNormalizeString_whenInputS_thenReturnInputS() {
    // Arrange, Act and Assert
    assertEquals("Input s+", normalizationService.normalizeString("̀Input\\s+"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code Input}.
   *   <li>Then return {@code Input}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when 'Input'; then return 'Input'")
  void testNormalizeString_whenInput_thenReturnInput() {
    // Arrange, Act and Assert
    assertEquals("Input", normalizationService.normalizeString("Input"));
    assertEquals("Input", normalizationService.normalizeString("̀Input"));
    assertEquals("Input", normalizationService.normalizeString("Input̀"));
    assertEquals("Input", normalizationService.normalizeString("̀̀Input"));
    assertEquals("Input", normalizationService.normalizeString("̀ Input"));
    assertEquals("Input", normalizationService.normalizeString("̀Input̀"));
    assertEquals("Input", normalizationService.normalizeString("̀Input "));
    assertEquals("Input", normalizationService.normalizeString("̀Input!"));
    assertEquals("Input", normalizationService.normalizeString("̀Input*"));
    assertEquals("Input", normalizationService.normalizeString("̀Input\\"));
    assertEquals("Input", normalizationService.normalizeString("̀!Input"));
    assertEquals("Input", normalizationService.normalizeString("̀*Input"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code null}.
   *   <li>Then return {@code null}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when 'null'; then return 'null'")
  void testNormalizeString_whenNull_thenReturnNull() {
    // Arrange, Act and Assert
    assertNull(normalizationService.normalizeString(null));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code ̀\p{InCombiningDiacriticalMarks}}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '̀\\p{InCombiningDiacriticalMarks}'")
  void testNormalizeString_whenPInCombiningDiacriticalMarks() {
    // Arrange, Act and Assert
    assertEquals(
        "p{InCombiningDiacriticalMarks}",
        normalizationService.normalizeString("̀\\p{InCombiningDiacriticalMarks}"));
    assertEquals(
        "p{InCombiningDiacriticalMarks}",
        normalizationService.normalizeString(" \\p{InCombiningDiacriticalMarks}"));
    assertEquals(
        "p{InCombiningDiacriticalMarks}",
        normalizationService.normalizeString("\\p{InCombiningDiacriticalMarks}̀"));
    assertEquals(
        "p{InCombiningDiacriticalMarks}",
        normalizationService.normalizeString("̀̀\\p{InCombiningDiacriticalMarks}"));
    assertEquals(
        "p{InCombiningDiacriticalMarks}",
        normalizationService.normalizeString("̀ \\p{InCombiningDiacriticalMarks}"));
    assertEquals(
        "p{InCombiningDiacriticalMarks}",
        normalizationService.normalizeString("̀\\p{InCombiningDiacriticalMarks}̀"));
    assertEquals(
        "p{InCombiningDiacriticalMarks}",
        normalizationService.normalizeString("̀\\p{InCombiningDiacriticalMarks} "));
    assertEquals(
        "p{InCombiningDiacriticalMarks}",
        normalizationService.normalizeString("̀\\p{InCombiningDiacriticalMarks}!"));
    assertEquals(
        "p{InCombiningDiacriticalMarks}",
        normalizationService.normalizeString("̀\\p{InCombiningDiacriticalMarks}*"));
    assertEquals(
        "p{InCombiningDiacriticalMarks}",
        normalizationService.normalizeString("̀\\p{InCombiningDiacriticalMarks}\\"));
    assertEquals(
        "p{InCombiningDiacriticalMarks}",
        normalizationService.normalizeString("̀!\\p{InCombiningDiacriticalMarks}"));
    assertEquals(
        "p{InCombiningDiacriticalMarks}",
        normalizationService.normalizeString("̀*\\p{InCombiningDiacriticalMarks}"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code ̀\s+42}.
   *   <li>Then return {@code s+42}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '̀\\s+42'; then return 's+42'")
  void testNormalizeString_whenS42_thenReturnS42() {
    // Arrange, Act and Assert
    assertEquals("s+42", normalizationService.normalizeString("̀\\s+42"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code ̀\s+Input}.
   *   <li>Then return {@code s+Input}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '̀\\s+Input'; then return 's+Input'")
  void testNormalizeString_whenSInput_thenReturnSInput() {
    // Arrange, Act and Assert
    assertEquals("s+Input", normalizationService.normalizeString("̀\\s+Input"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code ̀\s+\s+}.
   *   <li>Then return {@code s+ s+}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '̀\\s+\\s+'; then return 's+ s+'")
  void testNormalizeString_whenSS_thenReturnSS() {
    // Arrange, Act and Assert
    assertEquals("s+ s+", normalizationService.normalizeString("̀\\s+\\s+"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When {@code ̀\s+}.
   *   <li>Then return {@code s+}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when '̀\\s+'; then return 's+'")
  void testNormalizeString_whenS_thenReturnS() {
    // Arrange, Act and Assert
    assertEquals("s+", normalizationService.normalizeString("̀\\s+"));
    assertEquals("s+", normalizationService.normalizeString(" \\s+"));
    assertEquals("s+", normalizationService.normalizeString("\\s+̀"));
    assertEquals("s+", normalizationService.normalizeString("̀̀\\s+"));
    assertEquals("s+", normalizationService.normalizeString("̀ \\s+"));
    assertEquals("s+", normalizationService.normalizeString("̀\\s+̀"));
    assertEquals("s+", normalizationService.normalizeString("̀\\s+ "));
    assertEquals("s+", normalizationService.normalizeString("̀\\s+!"));
    assertEquals("s+", normalizationService.normalizeString("̀\\s+*"));
    assertEquals("s+", normalizationService.normalizeString("̀\\s+\\"));
    assertEquals("s+", normalizationService.normalizeString("̀!\\s+"));
    assertEquals("s+", normalizationService.normalizeString("̀*\\s+"));
  }

  /**
   * Test {@link NormalizationService#normalizeString(String)}.
   *
   * <ul>
   *   <li>When space space.
   *   <li>Then return empty string.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeString(String)}
   */
  @Test
  @DisplayName("Test normalizeString(String); when space space; then return empty string")
  void testNormalizeString_whenSpaceSpace_thenReturnEmptyString() {
    // Arrange, Act and Assert
    assertEquals("", normalizationService.normalizeString("  "));
  }

  /**
   * Test {@link NormalizationService#normalizeQuery(DiscogsQueryDTO)}.
   *
   * <p>Method under test: {@link NormalizationService#normalizeQuery(DiscogsQueryDTO)}
   */
  @Test
  @DisplayName("Test normalizeQuery(DiscogsQueryDTO)")
  void testNormalizeQuery() {
    // Arrange
    DiscogsQueryDTO query =
        new DiscogsQueryDTO(
            "Artist",
            "Album",
            "Track",
            "Dr",
            "Format",
            DiscogCountries.UK,
            DiscogsTypes.RELEASE,
            "Barcode");

    // Act and Assert
    assertEquals(query, normalizationService.normalizeQuery(query));
  }

  /**
   * Test {@link NormalizationService#normalizeQuery(DiscogsQueryDTO)}.
   *
   * <p>Method under test: {@link NormalizationService#normalizeQuery(DiscogsQueryDTO)}
   */
  @Test
  @DisplayName("Test normalizeQuery(DiscogsQueryDTO)")
  void testNormalizeQuery2() {
    // Arrange
    DiscogsQueryDTO query =
        new DiscogsQueryDTO(
            null,
            "Album",
            "Track",
            "Dr",
            "Format",
            DiscogCountries.UK,
            DiscogsTypes.RELEASE,
            "Barcode");

    // Act and Assert
    assertEquals(query, normalizationService.normalizeQuery(query));
  }

  /**
   * Test {@link NormalizationService#normalizeQuery(DiscogsQueryDTO)}.
   *
   * <ul>
   *   <li>Then return artist is empty string.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeQuery(DiscogsQueryDTO)}
   */
  @Test
  @DisplayName("Test normalizeQuery(DiscogsQueryDTO); then return artist is empty string")
  void testNormalizeQuery_thenReturnArtistIsEmptyString() {
    // Arrange and Act
    DiscogsQueryDTO actualNormalizeQueryResult =
        normalizationService.normalizeQuery(
            new DiscogsQueryDTO(
                "̀",
                "Album",
                "Track",
                "Dr",
                "Format",
                DiscogCountries.UK,
                DiscogsTypes.RELEASE,
                "Barcode"));

    // Assert
    assertEquals("", actualNormalizeQueryResult.artist());
    assertEquals("Album", actualNormalizeQueryResult.album());
    assertEquals("Barcode", actualNormalizeQueryResult.barcode());
    assertEquals("Dr", actualNormalizeQueryResult.title());
    assertEquals("Format", actualNormalizeQueryResult.format());
    assertEquals("Track", actualNormalizeQueryResult.track());
    assertEquals(DiscogCountries.UK, actualNormalizeQueryResult.country());
    assertEquals(DiscogsTypes.RELEASE, actualNormalizeQueryResult.types());
  }

  /**
   * Test {@link NormalizationService#normalizeQuery(DiscogsQueryDTO)}.
   *
   * <ul>
   *   <li>When {@code null}.
   *   <li>Then return {@code null}.
   * </ul>
   *
   * <p>Method under test: {@link NormalizationService#normalizeQuery(DiscogsQueryDTO)}
   */
  @Test
  @DisplayName("Test normalizeQuery(DiscogsQueryDTO); when 'null'; then return 'null'")
  void testNormalizeQuery_whenNull_thenReturnNull() {
    // Arrange, Act and Assert
    assertNull(normalizationService.normalizeQuery(null));
  }
}
