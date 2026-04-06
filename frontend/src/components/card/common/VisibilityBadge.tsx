import { useTranslation } from 'react-i18next'
import { IconEye, IconEyeOff, IconUsers } from '@tabler/icons-react'
import { Badge } from './Badge'
import { Visibility } from '@/api/dto'

interface VisibilityBadgeProps {
  visibility: Visibility
  showIcon?: boolean
}

export function VisibilityBadge({ visibility, showIcon = true }: VisibilityBadgeProps) {
  const { t } = useTranslation()

  const icon = showIcon
    ? {
        [Visibility.PUBLIC]: <IconEye size={12} />,
        [Visibility.PUBLIC_UNLISTED]: <IconEyeOff size={12} />,
        [Visibility.TEAM]: <IconUsers size={12} />,
      }[visibility]
    : undefined

  const variant =
    visibility === Visibility.PUBLIC
      ? 'primary'
      : visibility === Visibility.PUBLIC_UNLISTED
        ? 'orange'
        : 'gray'

  return (
    <Badge variant={variant} icon={icon}>
      {t(`visibility.${visibility.toLowerCase() as 'public' | 'public_unlisted' | 'team'}`)}
    </Badge>
  )
}
