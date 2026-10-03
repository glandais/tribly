import { describe, it, expect, vi, afterEach } from 'vitest'
import { render, screen, cleanup } from '@testing-library/react'

const auth = vi.hoisted(() => ({ isAuthenticated: false }))
vi.mock('../../hooks/useAuth', () => ({
  useAuth: () => ({ isAuthenticated: auth.isAuthenticated }),
}))
vi.mock('./MemberHome', () => ({ MemberHome: () => <p data-testid="member-home" /> }))
vi.mock('./VisitorHome', () => ({ VisitorHome: () => <p data-testid="visitor-home" /> }))

import { HomePage } from './HomePage'

describe('HomePage', () => {
  afterEach(cleanup)

  it('a visitor gets the visitor home — what an anonymous SSR pass renders', () => {
    auth.isAuthenticated = false
    render(<HomePage />)
    expect(screen.getByTestId('visitor-home')).toBeTruthy()
    expect(screen.queryByTestId('member-home')).toBeNull()
  })

  it('a signed-in member gets the member home', () => {
    auth.isAuthenticated = true
    render(<HomePage />)
    expect(screen.getByTestId('member-home')).toBeTruthy()
    expect(screen.queryByTestId('visitor-home')).toBeNull()
  })
})
