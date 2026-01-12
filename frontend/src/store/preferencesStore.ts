import { UnitSystem } from '@/api/dto'
import { create } from 'zustand'

const STORAGE_KEY = 'tribly-unit-system'

interface PreferencesState {
  unitSystem: UnitSystem
  setUnitSystem: (unitSystem: UnitSystem) => void
  syncFromServer: (unitSystem: string | null | undefined) => void
}

function parseUnitSystem(value: string | null | undefined): UnitSystem {
  return value === 'IMPERIAL' ? 'IMPERIAL' : 'METRIC'
}

function getStoredUnitSystem(): UnitSystem {
  if (typeof window === 'undefined') return 'METRIC'
  return parseUnitSystem(localStorage.getItem(STORAGE_KEY))
}

export const usePreferencesStore = create<PreferencesState>()((set) => ({
  unitSystem: getStoredUnitSystem(),

  setUnitSystem: (unitSystem) => {
    localStorage.setItem(STORAGE_KEY, unitSystem)
    set({ unitSystem })
  },

  syncFromServer: (serverUnitSystem) => {
    if (!serverUnitSystem) return
    const unitSystem = parseUnitSystem(serverUnitSystem)
    localStorage.setItem(STORAGE_KEY, unitSystem)
    set({ unitSystem })
  },
}))
