package fr.pedalons.service.migration.live;

import org.jspecify.annotations.Nullable;

/**
 * Everything a running job needs, as plain values — no entity, no lazy proxy: the job spans many
 * short transactions and minutes of network and GPX work. Loaded once, when the job is claimed.
 *
 * @param attempt the attempt this run is; a progress write from a run that no longer owns the row
 *     (requeued as stuck, then reclaimed) is refused on it
 * @param baseUrl {@code domains.base_url} snapshotted at confirmation — never an alias
 * @param targetTeamId the team an earlier attempt of this job created or reused, or null — a retry
 *     of a {@code reset} job must not reset the team that same job produced
 */
public record LiveJobContext(
    long jobId,
    String jobTsid,
    long domainId,
    long userId,
    String biketeamTeamId,
    String requestId,
    boolean dryRun,
    boolean reset,
    String baseUrl,
    int attempt,
    @Nullable Long targetTeamId) {}
