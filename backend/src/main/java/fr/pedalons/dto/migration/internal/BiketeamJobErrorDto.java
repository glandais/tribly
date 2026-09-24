package fr.pedalons.dto.migration.internal;

/** Why a job FAILED — see docs/plans/2026-09-22-biketeam-live-migration.md §5.2 for the codes. */
public record BiketeamJobErrorDto(String code, String message) {}
