import { Marker } from 'react-map-gl/maplibre'
import { Box, Text } from '@mantine/core'

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
        bg="green.5"
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
        bg="red.5"
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
        bg="blue.5"
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
  return (
    <Marker longitude={longitude} latitude={latitude} anchor="center">
      <Box style={{ display: 'flex', alignItems: 'center' }}>
        <Box
          w={20}
          h={20}
          bg="yellow.5"
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
              backgroundColor: 'rgba(255, 255, 255, 0.9)',
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
