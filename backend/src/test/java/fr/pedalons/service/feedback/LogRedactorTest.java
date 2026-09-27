package fr.pedalons.service.feedback;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class LogRedactorTest {

  @Test
  void tokensAndAddressesAreRedacted() {
    assertEquals(
        "Authorization: Bearer [redacted]",
        LogRedactor.redact(
            "Authorization: Bearer eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIxIn0.c2lnbmF0dXJl"));
    assertEquals("jwt [redacted] seen", LogRedactor.redact("jwt eyJhbGciOiJI.eyJzdWIiOi.abc seen"));
    assertEquals("mail to [redacted] failed", LogRedactor.redact("mail to jane@doe.fr failed"));
    assertEquals(
        "GET /verify?token=[redacted]&lang=fr",
        LogRedactor.redact("GET /verify?token=abc123&lang=fr"));
    assertEquals(
        "{\"password\": \"[redacted]\"}", LogRedactor.redact("{\"password\": \"hunter2\"}"));
  }

  @Test
  void ordinaryTextIsUntouched() {
    String line = "GET /api/teams/team1/rides 500 INTERNAL_ERROR";
    assertEquals(line, LogRedactor.redact(line));
  }
}
