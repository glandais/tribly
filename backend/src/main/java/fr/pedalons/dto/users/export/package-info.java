/**
 * Records written as JSON into a GDPR data-export ZIP.
 *
 * <p>These are <em>not</em> API surface: they carry no {@code @Schema} and must never be returned by
 * a REST resource, or ~20 types would leak into {@code contracts/openapi.yaml}.
 *
 * <p>They exist as explicit records rather than reflective entity serialization because they are the
 * redaction boundary: every exported field is one somebody wrote down on purpose, and a credential
 * added to an entity later cannot leak into an export by default.
 */
@NullMarked
package fr.pedalons.dto.users.export;

import org.jspecify.annotations.NullMarked;
