package fr.pedalons.service.user;

/**
 * Everything a running export job needs, as plain values.
 *
 * <p>Deliberately holds no entity and no lazy proxy: the job spans several short transactions with
 * minutes of storage I/O in between, and a managed instance would either detach or drag a session
 * along with it. Loaded once, up front, inside one transaction.
 *
 * <p>{@code baseUrl}, {@code siteName} and {@code language} are copied from the {@code UserExport}
 * row rather than resolved — the scheduler has no HTTP request, so {@code DomainResolver} does not
 * exist for it.
 */
public record ExportJobContext(
    Long exportId,
    String exportTsid,
    Long userId,
    String userTsid,
    Long domainId,
    String email,
    String displayName,
    String baseUrl,
    String siteName,
    String language) {}
