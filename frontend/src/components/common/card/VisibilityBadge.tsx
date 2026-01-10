import { useTranslation } from 'react-i18next'
import { IconEye, IconUsers } from '@tabler/icons-react'
import { Badge } from './Badge'

interface VisibilityBadgeProps {
  visibility: 'PUBLIC' | 'TEAM'
  showIcon?: boolean
}

export function VisibilityBadge({ visibility, showIcon = true }: VisibilityBadgeProps) {
  const { t } = useTranslation()

  const icon =
    showIcon && visibility === 'PUBLIC' ? (
      <IconEye size={12} />
    ) : showIcon && visibility === 'TEAM' ? (
      <IconUsers size={12} />
    ) : undefined

  return (
    <Badge variant={visibility === 'PUBLIC' ? 'indigo' : 'gray'} icon={icon}>
      {t(`visibility.${visibility.toLowerCase() as 'public' | 'team'}`)}
    </Badge>
  )
}
