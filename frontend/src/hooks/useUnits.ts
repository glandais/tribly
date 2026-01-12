import { useCallback, useMemo } from 'react'
import { useTranslation } from 'react-i18next'
import { usePreferencesStore } from '../store/preferencesStore'
import {
  formatDistance,
  formatElevation,
  formatSpeed,
  getUnitConfig,
  speedToDisplay as speedToDisplayFn,
  speedFromDisplay as speedFromDisplayFn,
  distanceToDisplay as distanceToDisplayFn,
  distanceFromDisplay as distanceFromDisplayFn,
  elevationToDisplay as elevationToDisplayFn,
  elevationFromDisplay as elevationFromDisplayFn,
} from '../utils/unitFormat'

export function useUnits() {
  const { t } = useTranslation()
  const unitSystem = usePreferencesStore((state) => state.unitSystem)
  const config = useMemo(() => getUnitConfig(unitSystem), [unitSystem])
  const isImperial = unitSystem === 'IMPERIAL'

  const distance = useCallback(
    (meters: number) => {
      const key = isImperial ? 'distance_imperial' : 'distance'
      return t(key satisfies 'distance' | 'distance_imperial', {
        distance: formatDistance(meters, unitSystem),
      })
    },
    [t, unitSystem, isImperial]
  )

  const elevation = useCallback(
    (meters: number) => {
      const key = isImperial ? 'elevation_imperial' : 'elevation'
      return t(key satisfies 'elevation' | 'elevation_imperial', {
        elevation: formatElevation(meters, unitSystem),
      })
    },
    [t, unitSystem, isImperial]
  )

  const speed = useCallback(
    (kmh: number) => {
      const key = isImperial ? 'speed_imperial' : 'speed'
      return t(key satisfies 'speed' | 'speed_imperial', {
        speed: formatSpeed(kmh, unitSystem),
      })
    },
    [t, unitSystem, isImperial]
  )

  const distanceUnit = useCallback(() => {
    const key = isImperial
      ? 'routes.list.filters.distance.unit_imperial'
      : 'routes.list.filters.distance.unit'
    return t(
      key satisfies
        | 'routes.list.filters.distance.unit'
        | 'routes.list.filters.distance.unit_imperial'
    )
  }, [t, isImperial])

  const elevationUnit = useCallback(() => {
    const key = isImperial
      ? 'routes.list.filters.elevationGain.unit_imperial'
      : 'routes.list.filters.elevationGain.unit'
    return t(
      key satisfies
        | 'routes.list.filters.elevationGain.unit'
        | 'routes.list.filters.elevationGain.unit_imperial'
    )
  }, [t, isImperial])

  // Raw formatters for special cases (charts, inputs)
  const formatDistanceFn = useCallback((m: number) => formatDistance(m, unitSystem), [unitSystem])

  const formatElevationFn = useCallback((m: number) => formatElevation(m, unitSystem), [unitSystem])

  const formatSpeedFn = useCallback((kmh: number) => formatSpeed(kmh, unitSystem), [unitSystem])

  // Converters for form inputs
  const speedToDisplay = useCallback(
    (kmh: number | undefined) => speedToDisplayFn(kmh, unitSystem),
    [unitSystem]
  )

  const speedFromDisplay = useCallback(
    (displayVal: string | number | undefined) => speedFromDisplayFn(displayVal, unitSystem),
    [unitSystem]
  )

  const distanceToDisplay = useCallback(
    (meters: number | undefined) => distanceToDisplayFn(meters, unitSystem),
    [unitSystem]
  )

  const distanceFromDisplay = useCallback(
    (displayVal: string | number | undefined) => distanceFromDisplayFn(displayVal, unitSystem),
    [unitSystem]
  )

  const elevationToDisplay = useCallback(
    (meters: number | undefined) => elevationToDisplayFn(meters, unitSystem),
    [unitSystem]
  )

  const elevationFromDisplay = useCallback(
    (displayVal: string | number | undefined) => elevationFromDisplayFn(displayVal, unitSystem),
    [unitSystem]
  )

  return {
    unitSystem,
    config,
    distance,
    elevation,
    speed,
    distanceUnit,
    elevationUnit,
    // Raw formatters for special cases (charts, inputs)
    formatDistance: formatDistanceFn,
    formatElevation: formatElevationFn,
    formatSpeed: formatSpeedFn,
    // Converters for form inputs (to/from display units)
    speedToDisplay,
    speedFromDisplay,
    distanceToDisplay,
    distanceFromDisplay,
    elevationToDisplay,
    elevationFromDisplay,
  }
}
