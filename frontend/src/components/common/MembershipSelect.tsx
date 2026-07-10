import { useTranslation } from 'react-i18next'
import { Select } from '@mantine/core'
import { useAuth } from '@/hooks/useAuth'
import type { MembershipFilterValue } from '@/hooks/filters/membership'

interface MembershipSelectProps {
  value: MembershipFilterValue
  onChange: (value: MembershipFilterValue) => void
}

/** Restricts a listing to the teams the visitor belongs to. Nothing to offer a logged-out one. */
export function MembershipSelect({ value, onChange }: MembershipSelectProps) {
  const { t } = useTranslation()
  const { isAuthenticated } = useAuth()

  if (!isAuthenticated) {
    return null
  }

  return (
    <Select
      value={value}
      onChange={(next) => onChange(next as MembershipFilterValue)}
      data={[
        { value: 'all', label: t('filters.membership.all') },
        { value: 'member', label: t('roles.MEMBER') },
        { value: 'organizer', label: t('roles.ORGANIZER') },
        { value: 'admin', label: t('roles.ADMIN') },
      ]}
      aria-label={t('filters.membership.label')}
      allowDeselect={false}
      w={{ base: '100%', sm: 200 }}
    />
  )
}
