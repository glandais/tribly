package fr.pedalons.service.feedback;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import fr.pedalons.enums.ClientPlatform;
import java.util.List;
import org.junit.jupiter.api.Test;

class ErrorFingerprintTest {

  private static final String DART_STACK =
      """
      #0      RideDetailPage.build (package:pedalons/features/rides/presentation/ride_detail_page.dart:120:14)
      #1      StatelessElement.build (package:flutter/src/widgets/framework.dart:5687:49)
      #2      _RideHeader.build (package:pedalons/features/rides/presentation/widgets/ride_header.dart:33:7)
      <asynchronous suspension>
      """;

  @Test
  void message_blanksIdsAndNumbers() {
    assertEquals(
        "Ride # not found (#) at #",
        ErrorFingerprint.normalizeMessage("Ride 0j6fq2zyd3kxb not found (404) at 0x1f2e"));
    assertEquals(
        "Cannot read properties of undefined (reading 'name')",
        ErrorFingerprint.normalizeMessage("Cannot read properties of undefined (reading 'name')"));
  }

  @Test
  void webFrames_keepTheChunkWithoutHashNorPosition() {
    assertEquals(
        List.of("/assets/RideDetailPage.js", "/assets/index.js"),
        ErrorFingerprint.appFrames(
            ClientPlatform.WEB,
            """
            TypeError: x is undefined
                at Xe (https://pedalons.fr/assets/RideDetailPage-Bx1_aZ9q.js:1:2345)
                at chrome-extension://abcdef/content.js:1:1
                Yf@https://pedalons.fr/assets/index-C9aa0Zz1.js:4:111
            """));
  }

  @Test
  void dartFrames_keepFunctionAndFileOfTheApp() {
    assertEquals(
        List.of(
            "RideDetailPage.build"
                + " package:pedalons/features/rides/presentation/ride_detail_page.dart",
            "_RideHeader.build"
                + " package:pedalons/features/rides/presentation/widgets/ride_header.dart"),
        ErrorFingerprint.appFrames(ClientPlatform.ANDROID, DART_STACK));
  }

  @Test
  void sameBugMovedByAnEdit_isTheSameFingerprint() {
    String moved = DART_STACK.replace(":120:14", ":131:14");
    assertEquals(
        ErrorFingerprint.compute(ClientPlatform.ANDROID, "_TypeError", "Null check", DART_STACK),
        ErrorFingerprint.compute(ClientPlatform.ANDROID, "_TypeError", "Null check", moved));
  }

  @Test
  void platformTypeAndPlace_tellErrorsApart() {
    String base =
        ErrorFingerprint.compute(ClientPlatform.ANDROID, "_TypeError", "Null", DART_STACK);
    assertNotEquals(
        base, ErrorFingerprint.compute(ClientPlatform.IOS, "_TypeError", "Null", DART_STACK));
    assertNotEquals(
        base, ErrorFingerprint.compute(ClientPlatform.ANDROID, "StateError", "Null", DART_STACK));
    assertNotEquals(
        base,
        ErrorFingerprint.compute(
            ClientPlatform.ANDROID,
            "_TypeError",
            "Null",
            DART_STACK.replace("RideDetailPage.build", "TripDetailPage.build")));
  }
}
