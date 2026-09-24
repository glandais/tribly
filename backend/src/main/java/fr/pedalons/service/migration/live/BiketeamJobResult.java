package fr.pedalons.service.migration.live;

import fr.pedalons.dto.migration.internal.BiketeamJobCountsDto;
import fr.pedalons.dto.migration.internal.BiketeamJobWarningDto;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

/**
 * The {@code biketeam_migrations.result} document: counts and warnings, written as the job goes, and
 * the URL table once it has succeeded.
 */
public record BiketeamJobResult(
    BiketeamJobCountsDto counts,
    List<BiketeamJobWarningDto> warnings,
    boolean warningsTruncated,
    @Nullable Map<String, Map<String, String>> urlMap) {}
