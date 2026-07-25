package fr.pedalons.dto.users.response;

import java.io.InputStream;
import org.jspecify.annotations.Nullable;

/**
 * A data-export archive ready to stream back. Mirrors {@code DownloadableAsset}: the stream is lazy
 * and comes straight from storage, so nothing is buffered in the backend.
 */
public record DownloadableExport(InputStream content, String fileName, @Nullable Long size) {}
