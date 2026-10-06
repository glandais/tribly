package fr.pedalons.dto.teams.response;

/** What kind of event a team zone change moves (docs/LEDGER_*.md API-60, plan §9). */
public enum TimezoneChangeEntityType {
  RIDE,
  TRIP,
  TRIP_STAGE,
  POST
}
