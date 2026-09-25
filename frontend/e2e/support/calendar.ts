import { expect, test, type Page } from '@playwright/test'
import type { CalendarEventDto, CalendarEventsResponse } from '../../src/api/dto'
import { apiGet, type AuthResponse } from './api'
import { monthName, parisWallClock, type WallClock } from './dates'
import { hydrated } from './ui'

/**
 * The team calendar (/equipes/{slug}/calendrier), on both layouts: the desktop month grid lists
 * every event of the month; the phone layout (`layout="responsive"`) lists only the selected day's,
 * under a grid whose other months are reached through the year view.
 *
 * Its events are loaded once, for a window running from last month to six months ahead
 * (useCalendarDateRange), and that load is prefetched by the SSR server: paging months sends no
 * request from the browser at all. So nothing on the network says « the events are in » — a test
 * that checks an event is *absent* shows another event of the same view first (an anchor).
 */

export const teamCalendarPath = (teamSlug: string) => `/equipes/${teamSlug}/calendrier`

/**
 * Opens the team calendar on `day`: the month holding it (months ahead are reached with « Suivant »
 * on a desktop, through the year view on a phone) and, on a phone, that day selected.
 */
export async function openCalendar(page: Page, teamSlug: string, day: WallClock) {
  await page.goto(teamCalendarPath(teamSlug))
  const main = page.getByRole('main')
  await expect(main.getByRole('heading', { name: 'Calendrier', level: 2 })).toBeVisible()
  const today = parisWallClock(new Date())
  const monthsAhead = day.year * 12 + day.month - (today.year * 12 + today.month)
  expect(monthsAhead, 'the calendar only pages forward here').toBeGreaterThanOrEqual(0)
  const visibleButton = (name: string) =>
    main.getByRole('button', { name, exact: true }).filter({ visible: true })

  if (test.info().project.use.isMobile) {
    if (monthsAhead > 0) {
      await hydrated(visibleButton(String(day.year)))
      await visibleButton(String(day.year)).click()
      await visibleButton(monthName(day.month, 'fr-FR')).click()
    }
    // Day buttons are labelled « septembre 27, 2026 ».
    const dayButton = visibleButton(`${monthName(day.month, 'fr-FR')} ${day.day}, ${day.year}`)
    await hydrated(dayButton)
    await dayButton.click()
  } else {
    for (let i = 0; i < monthsAhead; i++) {
      await hydrated(visibleButton('Suivant'))
      await visibleButton('Suivant').click()
    }
  }
}

/** An event of the calendar, by its title — the visible one of the desktop and phone layouts. */
export const calendarEvent = (page: Page, title: string) =>
  page.getByRole('main').getByRole('button').filter({ hasText: title }).filter({ visible: true })

/** The team calendar's events between two instants, as `who` reads them. */
export async function teamEvents(
  who: AuthResponse,
  teamSlug: string,
  from: Date,
  to: Date
): Promise<CalendarEventDto[]> {
  const response = await apiGet<CalendarEventsResponse>(
    who,
    `/api/teams/${teamSlug}/calendar/events`,
    { from: from.toISOString(), to: to.toISOString() }
  )
  return response.events
}
