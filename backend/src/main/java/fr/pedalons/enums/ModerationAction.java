package fr.pedalons.enums;

/** What a moderator decides about a reported target, for all its open reports at once. */
public enum ModerationAction {
  /** Deletes the content. Not applicable to a member: a member is removed from the team page. */
  REMOVE_CONTENT,
  /** Keeps the content, and shows it again if enough reports had hidden it. */
  DISMISS
}
