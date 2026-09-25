import type { Locator, Page } from '@playwright/test'
import type {
  CommentDto,
  CommentRequest,
  GroupRequest,
  RideDto,
  RideParticipationDto,
  RideRequest,
} from '../../src/api/dto'
import { apiContext, expectOk, type AuthResponse } from './api'

/**
 * Rides seeded through the REST API as the ride editor creates them, participations, comments, and
 * the locators of the ride detail page.
 */

/**
 * A published, members-only ride two days ahead — comfortably inside "upcoming" whatever the
 * hour-aligned `from` boundary of the feeds — with one group, « Groupe A », unless `overrides` say
 * otherwise.
 */
export function rideRequest(name: string, overrides: Partial<RideRequest> = {}): RideRequest {
  const group: GroupRequest = { name: 'Groupe A' }
  return {
    name,
    media: { markdown: '', assets: { images: [], attachments: [] } },
    dateTime: new Date(Date.now() + 2 * 24 * 3600 * 1000).toISOString(),
    status: 'PUBLISHED',
    visibility: 'TEAM',
    groups: [group],
    ...overrides,
  }
}

export async function newRide(
  by: AuthResponse,
  teamSlug: string,
  name: string,
  overrides: Partial<RideRequest> = {}
): Promise<RideDto> {
  const api = await apiContext(by.accessToken)
  try {
    return await expectOk<RideDto>(
      await api.post(`/api/teams/${teamSlug}/rides`, { data: rideRequest(name, overrides) })
    )
  } finally {
    await api.dispose()
  }
}

export function groupId(ride: RideDto, name: string): string {
  const group = ride.groups.find((g) => g.name === name)
  if (!group) throw new Error(`ride ${ride.slug} has no group named ${name}`)
  return group.id
}

export async function joinGroup(
  who: AuthResponse,
  teamSlug: string,
  ride: RideDto,
  groupName: string
): Promise<RideParticipationDto> {
  const api = await apiContext(who.accessToken)
  try {
    return await expectOk<RideParticipationDto>(
      await api.post(
        `/api/teams/${teamSlug}/rides/${ride.slug}/groups/${groupId(ride, groupName)}/join`
      )
    )
  } finally {
    await api.dispose()
  }
}

/** Top-level comments, posted one after the other so their creation order is their text order. */
export async function postComments(
  who: AuthResponse,
  teamSlug: string,
  rideSlug: string,
  contents: string[]
) {
  const api = await apiContext(who.accessToken)
  try {
    for (const content of contents) {
      const request: CommentRequest = { content }
      await expectOk<CommentDto>(
        await api.post(`/api/teams/${teamSlug}/rides/${rideSlug}/comments`, { data: request })
      )
    }
  } finally {
    await api.dispose()
  }
}

export const ridePath = (teamSlug: string, rideSlug: string) =>
  `/equipes/${teamSlug}/sorties/${rideSlug}`

/** Opens the ride page and waits for its title: every later absence check needs a loaded page. */
export async function openRide(page: Page, teamSlug: string, ride: RideDto) {
  await page.goto(ridePath(teamSlug, ride.slug))
  await page.getByRole('heading', { level: 2, name: ride.name }).waitFor()
  await page.getByRole('heading', { level: 4, name: 'Groupes' }).waitFor()
}

/**
 * The card of one ride group (`RideGroupCard`, a Mantine Paper holding the group's name as its own
 * text). Group names are unique per test, so a second match is a strict-mode error, not a guess.
 */
export function groupCard(page: Page, groupName: string): Locator {
  return page
    .locator('.mantine-Paper-root')
    .filter({ has: page.getByText(groupName, { exact: true }) })
}
