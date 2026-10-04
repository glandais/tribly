import { useEffect } from 'react'
import { useMap } from 'react-map-gl/maplibre'
import { ROUTE_ARROW_IMAGE } from './routeArrowLayout'

/** Drawn at twice its displayed size (`pixelRatio: 2`), pointing east: a `line` placement turns
 * the icon's x axis along the line, in the direction of its coordinates. */
const SIZE = 32

function drawArrow(): ImageData | null {
  const canvas = document.createElement('canvas')
  canvas.width = SIZE
  canvas.height = SIZE
  const ctx = canvas.getContext('2d')
  if (!ctx) return null
  ctx.lineCap = 'round'
  ctx.lineJoin = 'round'
  const chevron = () => {
    ctx.beginPath()
    ctx.moveTo(12, 8)
    ctx.lineTo(21, 16)
    ctx.lineTo(12, 24)
  }
  // A dark rim under a white stroke: readable on every trace colour and every basemap.
  ctx.strokeStyle = 'rgba(0, 0, 0, 0.55)'
  ctx.lineWidth = 7
  chevron()
  ctx.stroke()
  ctx.strokeStyle = '#fff'
  ctx.lineWidth = 4
  chevron()
  ctx.stroke()
  return ctx.getImageData(0, 0, SIZE, SIZE)
}

/**
 * Registers the chevron with the map. A basemap switch replaces the style and drops its images, so
 * it is also handed to MapLibre's missing-image resolver, which re-adds it whenever a layer asks for
 * it. The resolver alone is not enough: a layer that asked before this effect ran has already been
 * told « missing », and only an `addImage` makes its tiles reload with the chevron.
 * `PedalonsMap` renders it; a bare `Map` that draws a trace renders it itself.
 */
export function RouteArrowImage() {
  const { current: mapRef } = useMap()

  useEffect(() => {
    if (!mapRef) return
    const map = mapRef.getMap()
    const add = () => {
      if (map.hasImage(ROUTE_ARROW_IMAGE)) return
      const image = drawArrow()
      if (image) map.addImage(ROUTE_ARROW_IMAGE, image, { pixelRatio: 2 })
    }
    map.setMissingStyleImageResolver((id) => {
      if (id === ROUTE_ARROW_IMAGE) add()
    })
    if (map.style) add()
    return () => {
      map.setMissingStyleImageResolver(null)
    }
  }, [mapRef])

  return null
}
