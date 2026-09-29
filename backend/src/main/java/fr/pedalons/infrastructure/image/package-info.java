/**
 * Which uploaded files are images, read from their first bytes: storage re-encodes them to remove
 * their metadata (EXIF, XMP, IPTC, comments, GPS position). See {@link
 * fr.pedalons.infrastructure.image.ImageFormat}.
 */
@NullMarked
package fr.pedalons.infrastructure.image;

import org.jspecify.annotations.NullMarked;
