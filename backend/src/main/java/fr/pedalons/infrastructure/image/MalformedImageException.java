package fr.pedalons.infrastructure.image;

import java.io.IOException;

/** The file announces an image format but its structure cannot be walked to strip metadata. */
public class MalformedImageException extends IOException {

  public MalformedImageException(String message) {
    super(message);
  }
}
