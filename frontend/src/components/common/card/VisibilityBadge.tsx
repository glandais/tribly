import { useTranslation } from 'react-i18next'
import { EyeIcon, UserGroupIcon } from '@heroicons/react/20/solid'
import { Badge } from './Badge'

interface VisibilityBadgeProps {
  visibility: 'PUBLIC' | 'TEAM'
  showIcon?: boolean
}

export function VisibilityBadge({ visibility, showIcon = true }: VisibilityBadgeProps) {
  const { t } = useTranslation('common')

  const icon =
    showIcon && visibility === 'PUBLIC' ? (
      <EyeIcon className="w-3 h-3" />
    ) : showIcon && visibility === 'TEAM' ? (
      <UserGroupIcon className="w-3 h-3" />
    ) : undefined

  return (
    <Badge variant={visibility === 'PUBLIC' ? 'indigo' : 'gray'} icon={icon}>
      {t(`visibility.${visibility.toLowerCase() as 'public' | 'team'}`)}
    </Badge>
  )
}
