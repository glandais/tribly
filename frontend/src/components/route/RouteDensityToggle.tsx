import { SegmentedControl } from '@mantine/core'
import { useTranslation } from 'react-i18next'
import type { RouteDensity } from '@/hooks/filters/routeFilters'

interface RouteDensityToggleProps {
  value: RouteDensity
  onChange: (value: RouteDensity) => void
}

/** Thumbnail cards or dense rows. Lives in the URL, so the choice is shareable. */
export function RouteDensityToggle({ value, onChange }: RouteDensityToggleProps) {
  const { t } = useTranslation()

  return (
    <SegmentedControl
      value={value}
      onChange={(next) => onChange(next as RouteDensity)}
      data={[
        { value: 'card', label: t('routes.density.card') },
        { value: 'row', label: t('routes.density.row') },
      ]}
      aria-label={t('routes.density.label')}
    />
  )
}
