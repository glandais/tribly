package fr.pedalons.dto.migration.internal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

/**
 * M2M, biketeam → Pédalons: redeem a grant and queue the migration. Hidden from the public
 * contract; see docs/plans/2026-09-22-biketeam-live-migration.md §5.1. Every field must equal what
 * the grant was minted for.
 *
 * <p>Boxed booleans so that a missing flag is a 400, not a silent {@code false}.
 */
public record BiketeamJobTriggerRequest(
    @NotBlank @Size(max = 64) @Nullable String requestId,
    @NotBlank @Size(max = 200) @Nullable String grant,
    @NotBlank @Size(max = 255) @Nullable String teamId,
    @NotNull @Nullable Boolean dryRun,
    @NotNull @Nullable Boolean reset) {}
