import { describe, expect, it } from 'vitest'
import { NotificationType } from '@/api/dto'
import type { NotificationPreferencesDto } from '@/api/dto'
import { NOTIFICATION_FAMILIES, familiesWithRows } from './notificationFamilies'

function preferences(overrides: Partial<NotificationPreferencesDto>): NotificationPreferencesDto {
  return { channels: [], preferences: [], emailDigest: false, teams: [], ...overrides }
}

describe('NOTIFICATION_FAMILIES', () => {
  it('puts every notification type in exactly one family', () => {
    const listed = NOTIFICATION_FAMILIES.flatMap((family) => family.types)
    // A type added to the contract and left out here would silently get no row.
    expect([...listed].sort()).toEqual(Object.values(NotificationType).sort())
    expect(new Set(listed).size).toBe(listed.length)
  })

  it('keeps the families of the app, in its order', () => {
    expect(NOTIFICATION_FAMILIES.map((family) => family.labelKey)).toEqual([
      'notifications.family.rides',
      'notifications.family.trips',
      'notifications.family.posts',
      'notifications.family.teams',
    ])
    expect(NOTIFICATION_FAMILIES[0].types).toEqual([
      'RIDE_PUBLISHED',
      'RIDE_UPDATED',
      'RIDE_CANCELLED',
      'RIDE_GROUP_REMOVED',
      'RIDE_REMINDER',
      'RIDE_JOINED',
    ])
    expect(NOTIFICATION_FAMILIES[3].types).toEqual(['TEAM_INVITATION', 'CONTENT_REPORTED'])
  })
})

describe('familiesWithRows', () => {
  it('keeps a type only when the server lists a cell for it on a declared channel', () => {
    const families = familiesWithRows(
      preferences({
        channels: ['PUSH'],
        preferences: [
          { type: 'RIDE_CANCELLED', channel: 'PUSH', enabled: true, enabledByDefault: true },
          { type: 'RIDE_PUBLISHED', channel: 'PUSH', enabled: false, enabledByDefault: true },
          // A cell on a channel the server does not declare gives no row.
          { type: 'TRIP_PUBLISHED', channel: 'EMAIL', enabled: true, enabledByDefault: true },
          { type: 'CONTENT_REPORTED', channel: 'PUSH', enabled: true, enabledByDefault: true },
        ],
      })
    )
    expect(families).toEqual([
      // The family's order, not the server's.
      { labelKey: 'notifications.family.rides', types: ['RIDE_PUBLISHED', 'RIDE_CANCELLED'] },
      { labelKey: 'notifications.family.teams', types: ['CONTENT_REPORTED'] },
    ])
  })

  it('has no family at all without a channel', () => {
    expect(
      familiesWithRows(
        preferences({
          preferences: [
            { type: 'RIDE_PUBLISHED', channel: 'EMAIL', enabled: true, enabledByDefault: true },
          ],
        })
      )
    ).toEqual([])
  })
})
