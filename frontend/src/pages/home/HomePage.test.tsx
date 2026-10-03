import { describe, it, expect, vi, afterEach } from 'vitest'
import { render, screen, cleanup } from '@testing-library/react'

const auth = vi.hoisted(() => ({ isAuthenticated: false }))
vi.mock('../../hooks/useAuth', () => ({
  useAuth: () => ({ isAuthenticated: auth.isAuthenticated }),
}))
vi.mock('./MemberHome', () => ({ MemberHome: () => <p>member home</p> }))
vi.mock('./VisitorHome', () => ({ VisitorHome: () => <p>visitor home</p> }))

import { HomePage } from './HomePage'

describe('HomePage', () => {
  afterEach(cleanup)

  it('a visitor gets the visitor home — what an anonymous SSR pass renders', () => {
    auth.isAuthenticated = false
    render(<HomePage />)
    expect(screen.getByText('visitor home')).toBeTruthy()
    expect(screen.queryByText('member home')).toBeNull()
  })

  it('a signed-in member gets the member home', () => {
    auth.isAuthenticated = true
    render(<HomePage />)
    expect(screen.getByText('member home')).toBeTruthy()
    expect(screen.queryByText('visitor home')).toBeNull()
  })
})
