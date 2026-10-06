/**
 * The Open-Meteo forecast cache behind the ride weather (docs/plans/archive/2026-10-05-weather.md §2).
 *
 * <p><b>The one deliberate exception to "every query filters by domainId".</b> None of these tables
 * carries a domain: a forecast is a function of a place and an hour and of nothing a tenant owns,
 * so two teams on two domains riding from the same village share one cell and one provider call.
 * What keeps this from leaking anything across tenants is the read path: a cell is only ever read
 * starting from a ride the caller is already allowed to read, and what it stores is the centre of
 * a ~5 km cell, never a meeting point or a route. The rows never reach the API with coordinates.
 */
@NullMarked
package fr.pedalons.domain.weather;

import org.jspecify.annotations.NullMarked;
