package fr.pedalons.enums;

/** What an {@code AuthFailure} was a failed guess at (docs/LEDGER_*.md SEC-4, SEC-7). */
public enum AuthFailureKind {
  /** A wrong password, or a password tried on an address with no account. */
  PASSWORD,
  /** A pairing code that matched no pending device on the domain. */
  DEVICE_CODE
}
