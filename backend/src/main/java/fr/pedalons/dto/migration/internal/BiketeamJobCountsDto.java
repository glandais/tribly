package fr.pedalons.dto.migration.internal;

/** Per-kind outcome counts of a job. */
public record BiketeamJobCountsDto(
    BiketeamJobCountDto teamPages,
    BiketeamJobCountDto places,
    BiketeamJobCountDto routes,
    BiketeamJobCountDto rideTemplates,
    BiketeamJobCountDto publications,
    BiketeamJobCountDto rides,
    BiketeamJobCountDto trips,
    BiketeamJobCountDto tripStages,
    BiketeamJobCountDto images) {

  public static BiketeamJobCountsDto zero() {
    BiketeamJobCountDto z = BiketeamJobCountDto.zero();
    return new BiketeamJobCountsDto(z, z, z, z, z, z, z, z, z);
  }
}
