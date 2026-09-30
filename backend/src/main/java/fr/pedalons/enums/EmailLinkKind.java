package fr.pedalons.enums;

/** What a link mailed to verify an address does once followed. */
public enum EmailLinkKind {
  /** Activates a new account: the page asks for its password, then signs in. */
  SIGN_UP,
  /** Confirms a signed-up member's new address: no password, no session. */
  EMAIL_CHANGE
}
