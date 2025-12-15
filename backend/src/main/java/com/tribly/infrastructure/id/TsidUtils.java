package com.tribly.infrastructure.id;

import io.hypersistence.tsid.TSID;

/**
 * Utility class for converting between Long IDs and String TSID representations.
 *
 * <p>TSIDs (Time-Sorted IDs) are stored internally as Long (BIGINT) but exposed
 * via the API as lowercase strings for URL-safety and readability.
 *
 * <p>Example:
 * <ul>
 *   <li>Internal Long: 459985892837466112</li>
 *   <li>External String: "0h4a8xzk8jv80"</li>
 * </ul>
 */
public final class TsidUtils {

    private TsidUtils() {
        // Utility class
    }

    /**
     * Converts a Long ID to its lowercase TSID string representation.
     *
     * @param id the Long ID (may be null)
     * @return the lowercase TSID string, or null if id is null
     */
    public static String toString(Long id) {
        if (id == null) {
            return null;
        }
        return TSID.from(id).toLowerCase();
    }

    /**
     * Converts a TSID string to its Long representation.
     *
     * @param tsid the TSID string (may be null)
     * @return the Long ID, or null if tsid is null
     * @throws IllegalArgumentException if the string is not a valid TSID
     */
    public static Long toLong(String tsid) {
        if (tsid == null) {
            return null;
        }
        return TSID.from(tsid).toLong();
    }
}
