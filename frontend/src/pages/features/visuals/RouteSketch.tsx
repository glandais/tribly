import { getColorFromGradient } from '@/components/map/mapUtils'

/** A fictional loop, in a 200 × 100 box: a hand-drawn stand-in for a route map, no map library. */
const TRACE =
  'M18 78 C 30 52, 52 60, 64 40 S 92 12, 118 22 S 160 18, 176 38 S 168 74, 140 80 S 96 70, 70 84 S 30 92, 18 78 Z'

interface RouteTraceProps {
  height?: number
  /** Stroke colour as a CSS value; the primary colour by default. */
  stroke?: string
}

/** A route trace with its start marker (green) — the colours of the real maps (BRANDING §3.7). */
export function RouteTrace({
  height = 96,
  stroke = 'var(--mantine-primary-color-filled)',
}: RouteTraceProps) {
  return (
    <svg
      viewBox="0 0 200 100"
      width="100%"
      height={height}
      preserveAspectRatio="xMidYMid meet"
      style={{ display: 'block' }}
    >
      <path
        d={TRACE}
        fill="none"
        stroke={stroke}
        strokeWidth={4}
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <circle cx={18} cy={78} r={6} fill="var(--mantine-color-green-filled)" />
    </svg>
  )
}

/** Fictional elevation samples (metres), one every 2 km. */
const ELEVATIONS = [
  420, 455, 520, 610, 700, 790, 860, 905, 930, 900, 850, 880, 960, 1040, 1110, 1150, 1120, 1020,
  900, 780, 690, 620, 560, 510, 470, 440,
]
const STEP_M = 2000

/**
 * An elevation profile coloured by gradient, with the very ramp of the route pages
 * (`getColorFromGradient`, BRANDING §3.7 « Profil altimétrique »). Static demo data.
 */
export function ElevationSketch({ height = 72 }: { height?: number }) {
  const width = 300
  const min = Math.min(...ELEVATIONS) - 60
  const max = Math.max(...ELEVATIONS) + 20
  const x = (i: number) => (i / (ELEVATIONS.length - 1)) * width
  const y = (e: number) => height - ((e - min) / (max - min)) * height

  return (
    <svg
      viewBox={`0 0 ${width} ${height}`}
      width="100%"
      height={height}
      preserveAspectRatio="none"
      style={{ display: 'block' }}
    >
      {ELEVATIONS.slice(1).map((e, i) => {
        const prev = ELEVATIONS[i]
        // Descents take their absolute gradient: a profile left half neutral reads as missing data.
        const gradient = Math.abs(((e - prev) / STEP_M) * 100)
        return (
          <polygon
            key={i}
            points={`${x(i)},${height} ${x(i)},${y(prev)} ${x(i + 1)},${y(e)} ${x(i + 1)},${height}`}
            fill={getColorFromGradient(gradient)}
          />
        )
      })}
    </svg>
  )
}
