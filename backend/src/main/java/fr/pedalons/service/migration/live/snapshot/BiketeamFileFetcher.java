package fr.pedalons.service.migration.live.snapshot;

import java.io.IOException;
import java.nio.file.Path;

/** Downloads one file of biketeam's export into {@code target}. */
@FunctionalInterface
public interface BiketeamFileFetcher {

  /**
   * @param path the {@code FileRef.path}, already checked to lie under the team's file endpoint
   * @throws fr.pedalons.service.migration.SourceFileUnavailableException when the file is missing
   *     on biketeam's side — this element does without it
   * @throws fr.pedalons.service.migration.live.BiketeamExportException (unchecked) when the export
   *     refuses or cannot be reached — the job's attempt ends
   */
  void download(String path, Path target) throws IOException;
}
