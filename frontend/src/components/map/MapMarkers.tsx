import { Marker } from 'react-map-gl/maplibre'

interface MarkerProps {
  longitude: number
  latitude: number
}

export function StartMarker({ longitude, latitude }: MarkerProps) {
  return (
    <Marker longitude={longitude} latitude={latitude} anchor="center">
      <div className="w-6 h-6 bg-green-500 border-2 border-white rounded-full shadow-lg" />
    </Marker>
  )
}

export function EndMarker({ longitude, latitude }: MarkerProps) {
  return (
    <Marker longitude={longitude} latitude={latitude} anchor="center">
      <div className="w-6 h-6 bg-red-500 border-2 border-white rounded-full shadow-lg" />
    </Marker>
  )
}

export function HoverMarker({ longitude, latitude }: MarkerProps) {
  return (
    <Marker longitude={longitude} latitude={latitude} anchor="center">
      <div className="w-4 h-4 bg-blue-500 border-2 border-white rounded-full shadow-lg" />
    </Marker>
  )
}

interface WaypointMarkerProps extends MarkerProps {
  name?: string
}

export function WaypointMarker({ longitude, latitude, name }: WaypointMarkerProps) {
  return (
    <Marker longitude={longitude} latitude={latitude} anchor="center">
      <div className="flex items-center">
        <div className="w-5 h-5 bg-amber-500 border-2 border-white rounded-full shadow-lg" />
        {name && (
          <span className="ml-1 px-1 bg-white/90 rounded text-xs font-medium shadow-sm whitespace-nowrap">
            {name}
          </span>
        )}
      </div>
    </Marker>
  )
}
