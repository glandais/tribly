package fr.pedalons.api.gpx;

import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.service.route.GpxLimits;
import java.nio.file.Path;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.jspecify.annotations.Nullable;

/**
 * The size bound of {@link GpxLimits} on an uploaded GPX, applied by every resource that takes one
 * (docs/LEDGER_*.md SEC-6). On the API side: resources only call access-checked service methods.
 */
public final class GpxUploads {

  private GpxUploads() {}

  /**
   * The path of an uploaded GPX, or null when none came with the request. Refuses a file over
   * {@link GpxLimits#MAX_GPX_SIZE_BYTES} with {@code FILE_TOO_LARGE}, before anything reads it.
   */
  public static @Nullable Path uploadedGpx(@Nullable FileUpload upload) {
    if (upload == null) {
      return null;
    }
    if (upload.size() > GpxLimits.MAX_GPX_SIZE_BYTES) {
      throw new BusinessException(ErrorCode.FILE_TOO_LARGE);
    }
    return upload.filePath();
  }
}
