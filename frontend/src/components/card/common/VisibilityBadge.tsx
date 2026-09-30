import { useTranslation } from 'react-i18next'
import { IconEye, IconEyeOff, IconUsers } from '@tabler/icons-react'
import { Badge } from './Badge'
import { Visibility } from '@/api/dto'
import { VISIBILITY_COLORS } from '@/lib/badgeColors.generated'

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

  return (
    <Badge variant={VISIBILITY_COLORS[visibility]} icon={icon}>
      {t(`visibility.${visibility.toLowerCase() as 'public' | 'public_unlisted' | 'team'}`)}
    </Badge>
  )
}
