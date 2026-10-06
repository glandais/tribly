package fr.pedalons.common;

/**
 * The zone every {@code @Scheduled(cron = …)} is read in (docs/LEDGER_*.md API-92). Without it a
 * cron follows the JVM's default zone, which nothing configures — UTC in the container, the
 * developer's own on a workstation. The nightly jobs were written against the container: "3 AM" is
 * 3 AM UTC, 4 or 5 AM in Paris, still the quiet hours.
 */
public final class Crons {

  public static final String ZONE = "UTC";

  private Crons() {}
}
