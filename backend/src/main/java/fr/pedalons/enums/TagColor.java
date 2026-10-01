package fr.pedalons.enums;

/**
 * A tag's colour: one of the nine families of {@code contracts/brand-colors.yaml}, lower-cased by
 * the clients. Clients render a tag differently from a business badge (plan D10), so a green tag
 * never reads as « Publié ».
 */
public enum TagColor {
  INDIGO,
  BLUE,
  GREEN,
  RED,
  YELLOW,
  ORANGE,
  GRAPE,
  TEAL,
  GRAY
}
