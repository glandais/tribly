package fr.pedalons.dto.migration.internal;

/** The Pédalons team a job writes into, with its absolute URL. */
public record BiketeamJobTargetTeamDto(String slug, String name, String url) {}
