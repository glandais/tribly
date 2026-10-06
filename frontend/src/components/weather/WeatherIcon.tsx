import { weatherIcon } from './weatherDisplay'
import { useWeatherLabels } from './useWeatherLabels'

interface WeatherIconProps {
  condition: string | undefined
  daylight?: boolean
  size?: number
}

/**
 * The sky of a forecast, labelled for screen readers by its condition (« Averses »). In the colour
 * of the surrounding text: no tint per condition (see `weatherDisplay.ts`).
 *
 * No `title` on a Tabler icon: it renders a `<title>` inside the `<svg>`, which React 19 hoists out
 * of the server's HTML, so every page showing a forecast failed to hydrate (#418). The
 * `aria-label` names it.
 */
export function WeatherIcon({ condition, daylight = true, size = 20 }: WeatherIconProps) {
  const labels = useWeatherLabels()
  const Icon = weatherIcon(condition, daylight)
  const label = labels.condition(condition)
  return <Icon size={size} role="img" aria-label={label} style={{ flexShrink: 0 }} />
}
