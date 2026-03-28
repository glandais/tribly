package fr.pedalons.infrastructure.storage;

import java.io.InputStream;
import java.util.Map;

/** Storage service abstraction for file storage operations. */
public interface StorageService {

  /**
   * Stores content in the storage backend.
   *
   * @param key the storage key (path within the bucket)
   * @param content the input stream containing the content
   * @param contentType the MIME type of the content
   * @param contentLength the length of the content in bytes
   */
  void store(String key, InputStream content, String contentType, long contentLength);

  /**
   * Stores content in the storage backend with custom metadata.
   *
   * @param key the storage key (path within the bucket)
   * @param content the input stream containing the content
   * @param contentType the MIME type of the content
   * @param contentLength the length of the content in bytes
   * @param metadata custom metadata to attach to the object
   */
  void store(
      String key,
      InputStream content,
      String contentType,
      long contentLength,
      Map<String, String> metadata);

  /**
   * Retrieves content from the storage backend.
   *
   * @param key the storage key
   * @return an input stream for reading the content
   */
  InputStream retrieve(String key);

  /**
   * Deletes content from the storage backend.
   *
   * @param key the storage key
   */
  void delete(String key);

  /**
   * Checks if content exists in the storage backend.
   *
   * @param key the storage key
   * @return true if the content exists, false otherwise
   */
  boolean exists(String key);

  /**
   * Returns the S3 path for use with imgproxy.
   *
   * @param key the storage key
   * @return the S3 URI in format s3://bucket/key
   */
  String getS3Path(String key);
}
