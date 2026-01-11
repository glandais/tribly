import { Marker } from 'react-map-gl/maplibre'
import { Box, Text, useComputedColorScheme } from '@mantine/core'
import { getOverlayBg } from '@/lib/colors'

interface MarkerProps {
  longitude: number
  latitude: number
}

export function StartMarker({ longitude, latitude }: MarkerProps) {
  return (
    <Marker longitude={longitude} latitude={latitude} anchor="center">
      <Box
        w={24}
        h={24}
        bg="var(--mantine-color-green-filled)"
        style={{
          borderRadius: '50%',
          border: '2px solid white',
          boxShadow: 'var(--mantine-shadow-lg)',
        }}
      />
    </Marker>
  )
}

export function EndMarker({ longitude, latitude }: MarkerProps) {
  return (
    <Marker longitude={longitude} latitude={latitude} anchor="center">
      <Box
        w={24}
        h={24}
        bg="var(--mantine-color-red-filled)"
        style={{
          borderRadius: '50%',
          border: '2px solid white',
          boxShadow: 'var(--mantine-shadow-lg)',
        }}
      />
    </Marker>
  )
}

export function HoverMarker({ longitude, latitude }: MarkerProps) {
  return (
    <Marker longitude={longitude} latitude={latitude} anchor="center">
      <Box
        w={16}
        h={16}
        bg="var(--mantine-color-blue-filled)"
        style={{
          borderRadius: '50%',
          border: '2px solid white',
          boxShadow: 'var(--mantine-shadow-lg)',
        }}
      />
    </Marker>
  )
}

interface WaypointMarkerProps extends MarkerProps {
  name?: string
}

export function WaypointMarker({ longitude, latitude, name }: WaypointMarkerProps) {
  const colorScheme = useComputedColorScheme('light')
  const labelBg = getOverlayBg(colorScheme)

  return (
    <Marker longitude={longitude} latitude={latitude} anchor="center">
      <Box style={{ display: 'flex', alignItems: 'center' }}>
        <Box
          w={20}
          h={20}
          bg="var(--mantine-color-yellow-filled)"
          style={{
            borderRadius: '50%',
            border: '2px solid white',
            boxShadow: 'var(--mantine-shadow-lg)',
          }}
        />
        {name && (
          <Text
            size="xs"
            fw={500}
            ml={4}
            px={4}
            style={{
              backgroundColor: labelBg,
              borderRadius: 'var(--mantine-radius-sm)',
              boxShadow: 'var(--mantine-shadow-sm)',
              whiteSpace: 'nowrap',
            }}
          >
            {name}
          </Text>
        )}
      </Box>
    </Marker>
  )
}
