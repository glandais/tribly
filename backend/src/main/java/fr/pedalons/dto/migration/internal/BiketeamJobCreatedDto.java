package fr.pedalons.dto.migration.internal;

/** M2M: the job a trigger created — or, on an idempotent replay, the one it already created. */
public record BiketeamJobCreatedDto(String jobId, String status, String targetTeamSlug) {}
