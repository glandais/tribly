package fr.pedalons.dto.common;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.pedalons.dto.validation.ValidateSchema;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Objects;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

/**
 * A rendezvous date as a request carries it: a wall time without offset ({@code
 * 2026-10-11T08:00:00}), read by the backend in the zone it resolves for the entity — the start
 * place's, else the route's, else the team's (docs/LEDGER_*.md API-60).
 *
 * <p><b>Transition</b> (version N of docs/plans/2026-10-06-event-timezones.md §8): an instant with
 * {@code Z} or an offset — what the SPA sent before — is still accepted, and kept <em>as the
 * instant it is</em>. Round-tripping it through the wall time would move an instant that falls in
 * the second occurrence of a DST overlap by an hour. Version N+1 refuses it with a 400.
 *
 * <p>Deliberately not a {@link LocalDateTime} field: Jackson's lenient {@code
 * LocalDateTimeDeserializer} silently drops a trailing {@code Z} (06:30Z would be read as 06:30
 * wall time) and refuses {@code +02:00}. And the schema is a plain string, without {@code format:
 * date-time}, so that the generated clients accept both forms.
 */
@Schema(
    type = SchemaType.STRING,
    implementation = String.class,
    description =
        "Wall time without offset (2026-10-11T08:00:00), in the zone the backend resolves for the"
            + " entity. An instant with Z or an offset is still accepted during the transition and"
            + " kept as that instant.",
    examples = "2026-10-11T08:00:00")
@ValidateSchema
@JsonSerialize(using = EventDateTime.Serializer.class)
@JsonDeserialize(using = EventDateTime.Deserializer.class)
public final class EventDateTime {

  private final @Nullable LocalDateTime local;
  private final @Nullable Instant instant;

  private EventDateTime(@Nullable LocalDateTime local, @Nullable Instant instant) {
    this.local = local;
    this.instant = instant;
  }

  /** A wall time, read in the entity's zone. */
  public static EventDateTime local(LocalDateTime local) {
    return new EventDateTime(Objects.requireNonNull(local), null);
  }

  /** An instant in the old format, kept as is. */
  public static EventDateTime legacy(Instant instant) {
    return new EventDateTime(null, Objects.requireNonNull(instant));
  }

  public static @Nullable EventDateTime legacyNullable(@Nullable Instant instant) {
    return instant == null ? null : legacy(instant);
  }

  /**
   * Parses either form.
   *
   * @throws DateTimeException when the text is neither
   */
  public static EventDateTime parse(String text) {
    TemporalAccessor parsed =
        DateTimeFormatter.ISO_DATE_TIME.parseBest(
            text.trim(), ZonedDateTime::from, LocalDateTime::from);
    return switch (parsed) {
      case ZonedDateTime zoned -> legacy(zoned.toInstant());
      case LocalDateTime wall -> local(wall);
      default -> throw new DateTimeException("Not a date-time: " + text);
    };
  }

  /** Whether this came in the old format, with an offset. */
  public boolean isLegacy() {
    return instant != null;
  }

  /**
   * The instant this designates in {@code zone}: a legacy instant as it is; a wall time as {@link
   * ZonedDateTime#of(LocalDateTime, ZoneId)} resolves it — shifted by the length of the gap when it
   * does not exist, at the earlier offset when it exists twice (plan §6).
   */
  public Instant toInstant(ZoneId zone) {
    if (instant != null) {
      return instant;
    }
    return Objects.requireNonNull(local).atZone(zone).toInstant();
  }

  public static @Nullable Instant toInstant(@Nullable EventDateTime value, ZoneId zone) {
    return value == null ? null : value.toInstant(zone);
  }

  /**
   * The wire form. {@code @JsonValue} on top of the {@link Serializer}: it lives in
   * jackson-annotations, which every Jackson reads — RestAssured in the tests serialises requests
   * with its own mapper, which ignores databind's {@code @JsonSerialize} and wrote this as a bean.
   */
  @JsonValue
  @Override
  public String toString() {
    return instant != null ? instant.toString() : Objects.requireNonNull(local).toString();
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof EventDateTime other
        && Objects.equals(local, other.local)
        && Objects.equals(instant, other.instant);
  }

  @Override
  public int hashCode() {
    return Objects.hash(local, instant);
  }

  static final class Serializer extends JsonSerializer<EventDateTime> {
    @Override
    public void serialize(EventDateTime value, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
      gen.writeString(value.toString());
    }
  }

  static final class Deserializer extends JsonDeserializer<EventDateTime> {
    @Override
    public EventDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (p.currentToken() != JsonToken.VALUE_STRING) {
        return (EventDateTime) ctxt.handleUnexpectedToken(EventDateTime.class, p);
      }
      String text = p.getText();
      try {
        return parse(text);
      } catch (DateTimeException e) {
        // An InvalidFormatException, so a 400 like any other malformed body.
        throw ctxt.weirdStringException(
            text, EventDateTime.class, "expected a date-time such as 2026-10-11T08:00:00");
      }
    }
  }
}
