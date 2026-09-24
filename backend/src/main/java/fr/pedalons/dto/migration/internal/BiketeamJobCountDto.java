package fr.pedalons.dto.migration.internal;

/**
 * One kind of content: {@code skipped} is deleted on biketeam's side or deliberately left out,
 * {@code failed} is an element that failed alone while the team carried on.
 */
public record BiketeamJobCountDto(int total, int migrated, int skipped, int failed) {

  public static BiketeamJobCountDto zero() {
    return new BiketeamJobCountDto(0, 0, 0, 0);
  }
}
