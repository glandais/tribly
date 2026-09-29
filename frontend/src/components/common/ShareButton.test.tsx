import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { render, screen, fireEvent, cleanup, waitFor } from '@testing-library/react'
import { MantineProvider } from '@mantine/core'

const show = vi.fn()
vi.mock('@mantine/notifications', () => ({
  notifications: { show: (...a: unknown[]) => show(...a) },
}))

import { ShareButton } from './ShareButton'

function renderButton() {
  render(
    <MantineProvider>
      <ShareButton title="Sortie du dimanche" />
    </MantineProvider>
  )
  return screen.getByRole('button')
}

describe('ShareButton', () => {
  const writeText = vi.fn()

  beforeEach(() => {
    show.mockClear()
    writeText.mockReset().mockResolvedValue(undefined)
    window.history.replaceState(null, '', '/equipes/np/sorties/dimanche?tab=groupes')
    Object.defineProperty(navigator, 'clipboard', { value: { writeText }, configurable: true })
  })
  afterEach(() => {
    cleanup()
    Reflect.deleteProperty(navigator, 'share')
  })

  it('opens the share sheet with the address bar URL when the browser has one', async () => {
    const share = vi.fn().mockResolvedValue(undefined)
    Object.defineProperty(navigator, 'share', { value: share, configurable: true })
    fireEvent.click(renderButton())
    await waitFor(() => expect(share).toHaveBeenCalledTimes(1))
    expect(share).toHaveBeenCalledWith({
      title: 'Sortie du dimanche',
      url: `${window.location.origin}/equipes/np/sorties/dimanche?tab=groupes`,
    })
    expect(writeText).not.toHaveBeenCalled()
  })

  it('says nothing when the visitor closes the sheet', async () => {
    const share = vi.fn().mockRejectedValue(new DOMException('closed', 'AbortError'))
    Object.defineProperty(navigator, 'share', { value: share, configurable: true })
    fireEvent.click(renderButton())
    await waitFor(() => expect(share).toHaveBeenCalled())
    expect(writeText).not.toHaveBeenCalled()
    expect(show).not.toHaveBeenCalled()
  })

  it('copies the link where there is no share sheet', async () => {
    fireEvent.click(renderButton())
    await waitFor(() =>
      expect(show).toHaveBeenCalledWith(expect.objectContaining({ color: 'green' }))
    )
    expect(writeText).toHaveBeenCalledWith(
      `${window.location.origin}/equipes/np/sorties/dimanche?tab=groupes`
    )
  })

  it('reports a refused copy instead of claiming success', async () => {
    writeText.mockRejectedValue(new Error('denied'))
    fireEvent.click(renderButton())
    await waitFor(() =>
      expect(show).toHaveBeenCalledWith(expect.objectContaining({ color: 'red' }))
    )
  })
})
