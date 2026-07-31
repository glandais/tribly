package fr.pedalons.service.security;

import java.time.Instant;

/**
 * A freshly minted tile token.
 *
 * <p>Both {@code expiresAt} and {@code expiresInSeconds} are carried on purpose. The client must
 * schedule its renewal on {@code expiresInSeconds} — a phone's clock can be minutes off without
 * anyone noticing, and an absolute instant compared against a wrong clock renews either far too
 * early or never. {@code expiresAt} is for the human reading a log.
 */
public record TileToken(String value, Instant expiresAt, int expiresInSeconds) {}
