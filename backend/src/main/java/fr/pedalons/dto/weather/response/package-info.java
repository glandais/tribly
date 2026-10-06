/**
 * The weather a ride shows, as the API carries it. <b>No coordinate anywhere</b>: a PUBLIC ride may
 * ride a TEAM route, so a point is placed by its {@code distance} on the geometry the client may
 * read. Every number is computed by the server; the clients only display, translate and convert
 * units.
 */
@NullMarked
package fr.pedalons.dto.weather.response;

import org.jspecify.annotations.NullMarked;
