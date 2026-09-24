package fr.pedalons.common.exception;

import fr.pedalons.dto.error.ErrorCode;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

/**
 * {@code 409 BIKETEAM_MIGRATION_RUNNING}, carrying the job that holds the team — the M2M trigger
 * adds it to the body as {@code activeJobId}.
 */
@Getter
public class BiketeamMigrationRunningException extends ConflictException {

  private final @Nullable String activeJobId;

  public BiketeamMigrationRunningException(@Nullable String activeJobId) {
    super(ErrorCode.BIKETEAM_MIGRATION_RUNNING);
    this.activeJobId = activeJobId;
  }
}
