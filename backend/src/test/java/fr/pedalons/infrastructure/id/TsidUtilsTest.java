package fr.pedalons.infrastructure.id;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.common.TsidUtils;
import io.hypersistence.tsid.TSID;
import org.junit.jupiter.api.Test;

class TsidUtilsTest {

  // ==================== toString ====================

  @Test
  void toString_shouldConvertValidLongToLowercaseString() {
    Long id = 459985892837466112L;

    String result = TsidUtils.toString(id);

    assertNotNull(result);
    assertEquals(result.toLowerCase(), result); // Verify lowercase
    assertFalse(result.isEmpty());
  }

  @Test
  void toString_shouldProduceDifferentStringsForDifferentIds() {
    Long id1 = 459985892837466112L;
    Long id2 = 459985892837466113L;

    String result1 = TsidUtils.toString(id1);
    String result2 = TsidUtils.toString(id2);

    assertNotEquals(result1, result2);
  }

  // ==================== toLong ====================

  @Test
  void toLong_shouldConvertValidStringToLong() {
    String tsid = "0h4a8xzk8jv80";

    Long result = TsidUtils.toLong(tsid);

    assertNotNull(result);
    assertTrue(result > 0);
  }

  @Test
  void toLong_shouldThrowForInvalidString() {
    String invalidTsid = "invalid-tsid-string";

    assertThrows(IllegalArgumentException.class, () -> TsidUtils.toLong(invalidTsid));
  }

  @Test
  void toLong_shouldThrowForEmptyString() {
    assertThrows(IllegalArgumentException.class, () -> TsidUtils.toLong(""));
  }

  // ==================== toLongNullable ====================

  @Test
  void toLongNullable_shouldReturnNullForNullInput() {
    Long result = TsidUtils.toLongNullable(null);

    assertNull(result);
  }

  @Test
  void toLongNullable_shouldConvertValidString() {
    String tsid = "0h4a8xzk8jv80";

    Long result = TsidUtils.toLongNullable(tsid);

    assertNotNull(result);
    assertTrue(result > 0);
  }

  @Test
  void toLongNullable_shouldThrowForInvalidString() {
    String invalidTsid = "invalid-tsid-string";

    assertThrows(IllegalArgumentException.class, () -> TsidUtils.toLongNullable(invalidTsid));
  }

  // ==================== Round-trip Conversion ====================

  @Test
  void roundTrip_shouldPreserveLongValue() {
    Long originalId = TSID.fast().toLong();

    String tsidString = TsidUtils.toString(originalId);
    Long convertedId = TsidUtils.toLong(tsidString);

    assertEquals(originalId, convertedId);
  }

  @Test
  void roundTrip_shouldProduceLowercaseString() {
    Long id = TSID.fast().toLong();

    String tsidString = TsidUtils.toString(id);

    assertEquals(tsidString, tsidString.toLowerCase());
    assertNotEquals(tsidString, tsidString.toUpperCase()); // Ensure it's not all uppercase
  }

  @Test
  void roundTrip_shouldWorkWithMultipleValues() {
    for (int i = 0; i < 10; i++) {
      Long originalId = TSID.fast().toLong();

      String tsidString = TsidUtils.toString(originalId);
      Long convertedId = TsidUtils.toLong(tsidString);

      assertEquals(originalId, convertedId);
    }
  }

  // ==================== Edge Cases ====================

  @Test
  void toString_shouldHandleMinimumValidTsid() {
    Long minId = 1L;

    String result = TsidUtils.toString(minId);

    assertNotNull(result);
    assertEquals(minId, TsidUtils.toLong(result));
  }

  @Test
  void toString_shouldHandleLargeValidTsid() {
    Long largeId = Long.MAX_VALUE;

    String result = TsidUtils.toString(largeId);

    assertNotNull(result);
    assertEquals(largeId, TsidUtils.toLong(result));
  }
}
