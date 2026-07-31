package fr.pedalons.enums;

/**
 * Where a team invitation stands. {@code PENDING} is the only non-terminal value: the other three
 * are ends, and nothing transitions out of them.
 *
 * <p>{@code EXPIRED} is stored rather than derived from {@code expiresAt}. Two reasons: the unique
 * "one live invitation per (team, address)" index keys on this column, so a lapsed invitation has to
 * stop occupying the slot before the address can be invited again; and an admin looking at the list
 * should read the same word the server uses, not a date they have to compare against now.
 */
public enum InvitationStatus {
  PENDING,
  ACCEPTED,
  REVOKED,
  EXPIRED
}
