import { Text } from '@mantine/core'
import { useTranslation } from 'react-i18next'

/** Resources whose list pages show a total. One plural key pair per entry. */
type CountedResource = 'publications' | 'routes' | 'ads' | 'teams' | 'participations'

interface ResultCountProps {
  /** `total` of the list response, or a `CountResponse.total`. Undefined while loading. */
  total: number | undefined
  resource: CountedResource
}

/**
 * The total above a list — "2 585 parcours". Renders nothing until the count is
 * known, so the header does not flicker between a wrong number and the right one.
 */
export function ResultCount({ total, resource }: ResultCountProps) {
  const { t, i18n } = useTranslation()

  if (total === undefined) return null

  return (
    <Text size="sm" c="dimmed">
      {t(`list.count.${resource satisfies CountedResource}`, {
        count: total,
        formatted: total.toLocaleString(i18n.language),
      })}
    </Text>
  )
}
