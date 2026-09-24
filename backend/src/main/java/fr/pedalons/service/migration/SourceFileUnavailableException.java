package fr.pedalons.service.migration;

import java.io.IOException;

/**
 * A {@link SourceFile} this one element has to do without: missing on biketeam's side (its export
 * answered {@code 404 FILE_NOT_FOUND}), or not the bytes the snapshot described. Reported as a
 * {@code FILE_DOWNLOAD_FAILED} warning on the element, which carries on — distinct from a file that
 * was fetched and then failed to process ({@code GPX_FAILURE}, {@code IMAGE_FAILED}).
 *
 * <p>Not an export that cannot be reached: that is {@code BiketeamExportException}, unchecked, which
 * ends the job's attempt so that it is retried rather than completed without the file.
 */
public class SourceFileUnavailableException extends IOException {

  public SourceFileUnavailableException(String message, Throwable cause) {
    super(message, cause);
  }

  public SourceFileUnavailableException(String message) {
    super(message);
  }
}
