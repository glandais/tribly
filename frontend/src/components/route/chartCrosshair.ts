import type { Chart as ChartJS, Plugin } from 'chart.js'

/** A chart the crosshair plugin can draw on: `chart.crosshair` is set by whoever drives the hover. */
export type ChartWithCrosshair = ChartJS<'line'> & {
  crosshair?: { x: number; color?: string } | null
}

/**
 * Draws the vertical cursor line on an elevation profile. Shared by the single-route chart
 * (`ElevationChart`) and the multi-route one (`RoutesMapView`), which both sync it with a
 * `HoverMarker` on their map.
 */
export const crosshairPlugin: Plugin<'line'> = {
  id: 'crosshair',
  afterDraw: (chart) => {
    const chartWithCrosshair = chart as ChartWithCrosshair
    if (chartWithCrosshair.crosshair?.x) {
      const ctx = chart.ctx
      const x = chartWithCrosshair.crosshair.x
      const yAxis = chart.scales.y

      ctx.save()
      ctx.beginPath()
      ctx.moveTo(x, yAxis.top)
      ctx.lineTo(x, yAxis.bottom)
      ctx.lineWidth = 1
      ctx.strokeStyle = chartWithCrosshair.crosshair.color || 'rgba(0, 0, 0, 0.3)'
      ctx.stroke()
      ctx.restore()
    }
  },
}
