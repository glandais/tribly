package fr.pedalons.dto.migration.internal;

/**
 * What a trigger did: {@code created} the job (202), or found it already created by the same grant
 * — an idempotent replay (200).
 */
public record BiketeamJobTriggerResult(BiketeamJobCreatedDto job, boolean created) {}
