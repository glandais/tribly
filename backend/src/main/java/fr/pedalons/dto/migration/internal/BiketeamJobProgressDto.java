package fr.pedalons.dto.migration.internal;

/**
 * Where a job stands: its phase ({@code QUEUED, SNAPSHOT, TEAM, PLACES, ROUTES, RIDE_TEMPLATES,
 * PUBLICATIONS, RIDES, TRIPS, URLS, DONE}) and how many elements of that phase are done.
 */
public record BiketeamJobProgressDto(String phase, int done, int total) {}
