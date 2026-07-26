package fr.pedalons.dto.users.request;

import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.ThemePreference;
import fr.pedalons.enums.UnitSystem;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

/**
 * A partial update of the display preferences: only the fields present are written.
 *
 * <p>Every field is nullable and null means "leave it alone", which is what makes this a PATCH. It
 * also means a preference cannot be <em>cleared</em> back to "never chosen" through this endpoint —
 * an intentional omission, since no screen offers it and the alternative (a tri-state wrapper per
 * field) costs far more than it buys.
 */
@Schema(description = "Partial update of the current user's display preferences")
@ValidateSchema
@Builder
public record UserPreferencesRequest(
    @Nullable
        @Schema(
            description = "Preferred unit system. Omit or send null to leave it unchanged.",
            examples = "METRIC")
        UnitSystem unitSystem,
    @Nullable
        @Schema(
            description = "Preferred colour scheme. Omit or send null to leave it unchanged.",
            examples = "SYSTEM")
        ThemePreference theme,
    @Nullable
        @Schema(
            description =
                "Preferred language as a BCP-47 tag ('fr', 'en', 'fr-CA'). Omit or send null to"
                    + " leave it unchanged. Not validated against the set of translations the app"
                    + " ships: a client asking for a language nobody has translated yet falls back"
                    + " on its own, which is better than a 400 the day a translation lands.",
            examples = "fr")
        @Size(min = 2, max = 10)
        @Pattern(regexp = "^[A-Za-z]{2,3}(-[A-Za-z0-9]{2,8})*$", message = "must be a BCP-47 tag")
        String language) {}
