import { describe, it, expect, vi, afterEach, beforeEach } from 'vitest'
import { render, screen, cleanup, fireEvent } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { MantineProvider } from '@mantine/core'
import type { PairedDeviceDto } from '@/api/dto'

vi.mock('@/lib/prefetch', () => ({ prefetchUrl: vi.fn() }))

// Keys, not wording: the assertions read which message is shown, not how it is phrased.
vi.mock('react-i18next', () => ({
  useTranslation: () => ({
    t: (key: string, options?: Record<string, string>) =>
      options && 'device' in options ? `${key}:${options.device}` : key,
    i18n: { language: 'fr' },
  }),
}))

const devices = vi.hoisted(() => ({ list: [] as PairedDeviceDto[], mutate: vi.fn() }))

vi.mock('@/api/endpoints/users/users', () => ({
  getListPairedDevicesQueryKey: () => ['/api/users/me/devices'],
  useListPairedDevices: () => ({ data: devices.list, isLoading: false }),
  useUnpairDevice: () => ({ mutate: devices.mutate, isPending: false }),
}))

import { PairedDevicesManager } from './PairedDevicesManager'

function renderManager() {
  return render(
    <MantineProvider>
      <QueryClientProvider client={new QueryClient()}>
        <MemoryRouter>
          <PairedDevicesManager />
        </MemoryRouter>
      </QueryClientProvider>
    </MantineProvider>
  )
}

describe('PairedDevicesManager', () => {
  beforeEach(() => {
    devices.list = []
    devices.mutate.mockReset()
  })
  afterEach(cleanup)

  it('says what a paired device is, apart from a connected service', () => {
    renderManager()

    expect(screen.getByRole('heading', { name: 'gps.devices.title' })).toBeTruthy()
    expect(screen.getByText('gps.devices.description')).toBeTruthy()
  })

  it('lists each paired device under its own name', () => {
    devices.list = [
      { id: 'k1', type: 'KAROO', pairedAt: '2026-09-01T08:00:00Z' },
      { id: 'g1', type: 'GARMIN', pairedAt: '2026-08-01T08:00:00Z' },
    ]
    renderManager()

    expect(screen.getByText('gps.devices.types.karoo')).toBeTruthy()
    expect(screen.getByText('gps.devices.types.garmin')).toBeTruthy()
    expect(screen.queryByText('gps.devices.empty')).toBeNull()
  })

  it('says how to pair one when none is', () => {
    renderManager()

    expect(screen.getByText('gps.devices.empty')).toBeTruthy()
    expect(screen.getByText('gps.devices.howToPair').closest('a')).toBeTruthy()
  })

  it('unpairs only the device confirmed', async () => {
    devices.list = [
      { id: 'k1', type: 'KAROO', pairedAt: '2026-09-01T08:00:00Z' },
      { id: 'g1', type: 'GARMIN', pairedAt: '2026-08-01T08:00:00Z' },
    ]
    renderManager()

    fireEvent.click(screen.getByLabelText('gps.devices.unpairLabel:gps.devices.types.garmin'))
    expect(devices.mutate).not.toHaveBeenCalled()
    fireEvent.click(await screen.findByText('gps.devices.unpairConfirm.confirm'))

    expect(devices.mutate).toHaveBeenCalledWith({ deviceId: 'g1' })
  })
})
