import { SegmentedControl } from '@mantine/core'
import { useTranslation } from 'react-i18next'
import type { PublicationScopeValue } from '@/hooks/filters/publicationFilters'
import { useAuth } from '@/hooks/useAuth'

interface PublicationScopeControlProps {
  value: PublicationScopeValue
  onChange: (value: PublicationScopeValue) => void
}

/**
 * "Tout / À venir / Je participe" over a publication feed.
 *
 * "Je participe" is hidden from anonymous visitors: the contract states that
 * `participating` yields nothing for them, so offering it would be a dead end.
 */
export function PublicationScopeControl({ value, onChange }: PublicationScopeControlProps) {
  const { t } = useTranslation()
  const { isAuthenticated } = useAuth()

  const data = [
    { value: 'all', label: t('publications.scope.all') },
    { value: 'upcoming', label: t('publications.scope.upcoming') },
    ...(isAuthenticated ? [{ value: 'me', label: t('publications.scope.me') }] : []),
  ]

  return (
    <SegmentedControl
      value={value}
      onChange={(next) => onChange(next as PublicationScopeValue)}
      data={data}
      aria-label={t('publications.scope.label')}
    />
  )
}
