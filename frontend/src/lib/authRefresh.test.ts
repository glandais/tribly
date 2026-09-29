import { describe, expect, it } from 'vitest'
import { refreshesOn401 } from './authRefresh'

describe('refreshesOn401', () => {
  it('refreshes outside /api/auth/', () => {
    expect(refreshesOn401('/api/users/me')).toBe(true)
    expect(refreshesOn401('/api/teams/gaby/rides')).toBe(true)
  })

  it('refreshes on the /api/auth/ endpoints that need the access token', () => {
    expect(refreshesOn401('/api/auth/logout-all')).toBe(true)
    expect(refreshesOn401('/api/auth/email/change-request')).toBe(true)
    expect(refreshesOn401('/api/auth/passkeys')).toBe(true)
    expect(refreshesOn401('/api/auth/passkeys/0abc123')).toBe(true)
    expect(refreshesOn401('/api/auth/passkeys/register')).toBe(true)
    expect(refreshesOn401('/api/auth/passkeys/registration-options')).toBe(true)
  })

  it('does not refresh on the public /api/auth/ endpoints', () => {
    expect(refreshesOn401('/api/auth/login')).toBe(false)
    expect(refreshesOn401('/api/auth/refresh')).toBe(false)
    expect(refreshesOn401('/api/auth/logout')).toBe(false)
    expect(refreshesOn401('/api/auth/otp/verify')).toBe(false)
    expect(refreshesOn401('/api/auth/passkeys/authenticate')).toBe(false)
    expect(refreshesOn401('/api/auth/passkeys/authentication-options')).toBe(false)
  })

  it('refreshes a request without a URL, as before', () => {
    expect(refreshesOn401(undefined)).toBe(true)
  })
})
