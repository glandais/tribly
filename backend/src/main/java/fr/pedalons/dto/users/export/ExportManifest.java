package fr.pedalons.dto.users.export;

import fr.pedalons.dto.users.export.MembershipExport.MissingFile;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * {@code manifest.json} — what this ZIP contains and what it could not.
 *
 * <p>{@code missingFiles} matters: a binary whose storage object has gone is reported rather than
 * silently skipped, so an incomplete export never looks complete.
 */
public record ExportManifest(
    Instant generatedAt,
    String exportId,
    String siteName,
    String userId,
    Map<String, Integer> counts,
    List<MissingFile> missingFiles) {}
