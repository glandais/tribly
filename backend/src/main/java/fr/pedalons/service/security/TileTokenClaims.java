package fr.pedalons.service.security;

import java.time.Instant;

/**
 * What a verified tile token asserts: who is asking, on which domain, and until when.
 *
 * <p>{@code domainId} travels inside the signature rather than being re-derived from the request:
 * a token minted on one site must be worthless on another, and that check is only possible if the
 * issuing domain is part of what was signed.
 */
public record TileTokenClaims(long userId, long domainId, Instant expiresAt) {}
