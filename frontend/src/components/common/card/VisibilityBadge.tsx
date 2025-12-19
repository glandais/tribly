import { useTranslation } from 'react-i18next'
import { Badge } from './Badge'

interface VisibilityBadgeProps {
  visibility: 'PUBLIC' | 'TEAM'
  showIcon?: boolean
}

export function VisibilityBadge({ visibility, showIcon = true }: VisibilityBadgeProps) {
  const { t } = useTranslation('common')

  const icon =
    showIcon && visibility === 'PUBLIC' ? (
      <svg className="w-3 h-3" fill="currentColor" viewBox="0 0 20 20">
        <path d="M10 12a2 2 0 100-4 2 2 0 000 4z" />
        <path
          fillRule="evenodd"
          d="M.458 10C1.732 5.943 5.522 3 10 3s8.268 2.943 9.542 7c-1.274 4.057-5.064 7-9.542 7S1.732 14.057.458 10zM14 10a4 4 0 11-8 0 4 4 0 018 0z"
          clipRule="evenodd"
        />
      </svg>
    ) : showIcon && visibility === 'TEAM' ? (
      <svg className="w-3 h-3" fill="currentColor" viewBox="0 0 20 20">
        <path d="M9 6a3 3 0 11-6 0 3 3 0 016 0zM17 6a3 3 0 11-6 0 3 3 0 016 0zM12.93 17c.046-.327.07-.66.07-1a6.97 6.97 0 00-1.5-4.33A5 5 0 0119 16v1h-6.07zM6 11a5 5 0 015 5v1H1v-1a5 5 0 015-5z" />
      </svg>
    ) : undefined

  return (
    <Badge variant={visibility === 'PUBLIC' ? 'indigo' : 'gray'} icon={icon}>
      {t(`visibility.${visibility.toLowerCase()}`)}
    </Badge>
  )
}
