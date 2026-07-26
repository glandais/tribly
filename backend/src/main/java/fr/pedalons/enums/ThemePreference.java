package fr.pedalons.enums;

/**
 * The colour scheme a user asked for.
 *
 * <p>{@code null} on the user is not {@link #SYSTEM}: null means the question was never answered, so
 * a client is free to follow whatever its platform says. {@link #SYSTEM} means the user was asked
 * and chose to follow the platform — which matters the day the default changes.
 */
public enum ThemePreference {
  SYSTEM,
  LIGHT,
  DARK
}
