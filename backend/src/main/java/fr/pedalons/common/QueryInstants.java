package fr.pedalons.common;

import fr.pedalons.common.exception.BadRequestException;
import fr.pedalons.dto.error.ErrorCode;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import org.jspecify.annotations.Nullable;

/**
 * Reads an ISO-8601 instant query parameter ({@code from}, {@code to}). A malformed one is the
 * caller's error: a 400, not the 500 a bare {@link Instant#parse} gives — {@link
 * DateTimeParseException} is no {@link IllegalArgumentException} (docs/LEDGER_*.md API-91).
 */
public final class QueryInstants {

  private QueryInstants() {}

  public static @Nullable Instant parse(@Nullable String value) {
    if (value == null) {
      return null;
    }
    try {
      return Instant.parse(value);
    } catch (DateTimeParseException e) {
      throw new BadRequestException(ErrorCode.BAD_REQUEST, e);
    }
  }
}
