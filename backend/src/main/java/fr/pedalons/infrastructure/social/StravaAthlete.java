package fr.pedalons.infrastructure.social;

import org.jspecify.annotations.Nullable;

/**
 * The subset of the Strava athlete returned inline by the OAuth token exchange that we care about.
 * {@code athleteId} is the stable external identity; the rest is best-effort profile enrichment.
 */
public record StravaAthlete(
    String athleteId,
    @Nullable String firstName,
    @Nullable String lastName,
    @Nullable String profileImageUrl) {}
