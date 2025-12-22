import { useTranslation } from 'react-i18next'
import { UsersIcon } from '@heroicons/react/24/outline'
import type { TeamDetailDto } from '../../hooks/useTeam'
import { Card, CardContent, CardTitle, CardDescription } from '../common/card'
import { Badge, VisibilityBadge, Stat, CardSkeleton } from '../common/card'

interface TeamCardProps {
  team: TeamDetailDto
  showRole?: boolean
}

const roleBadgeVariants: Record<string, 'purple' | 'blue' | 'gray'> = {
  ADMIN: 'purple',
  ORGANIZER: 'blue',
  MEMBER: 'gray',
}

export function TeamCard({ team, showRole = false }: TeamCardProps) {
  const { t } = useTranslation('common')

  const membersIcon = <UsersIcon />

  return (
    <Card to={`/teams/${team.slug}`}>
      <div className="relative">
        {team.coverImageUrl ? (
          <img src={team.coverImageUrl} alt="" className="w-full h-32 object-cover rounded-t-lg" />
        ) : (
          <div className="w-full h-32 bg-linear-to-r from-indigo-500 to-purple-600 rounded-t-lg" />
        )}
        {team.logoUrl && (
          <img
            src={team.logoUrl}
            alt={team.name}
            className="absolute -bottom-6 left-4 w-12 h-12 rounded-full border-2 border-white bg-white"
          />
        )}
      </div>

      <CardContent className={team.logoUrl ? 'pt-8' : ''}>
        <CardTitle>{team.name}</CardTitle>
        {team.description && <CardDescription>{team.description}</CardDescription>}

        <div className="mt-4 flex items-center justify-between">
          <Stat icon={membersIcon}>{t('memberCount', { count: team.memberCount })}</Stat>

          <div className="flex items-center gap-2">
            {team.visibility === 'TEAM' && (
              <VisibilityBadge visibility={team.visibility} showIcon={false} />
            )}
            {showRole && team.role && (
              <Badge variant={roleBadgeVariants[team.role]}>{t(`roles.${team.role}`)}</Badge>
            )}
          </div>
        </div>
      </CardContent>
    </Card>
  )
}

interface TeamCardSkeletonProps {
  count?: number
}

export function TeamCardSkeleton({ count = 1 }: TeamCardSkeletonProps) {
  return <CardSkeleton count={count} hasImage imageHeight="h-32" statCount={1} badgeCount={1} />
}
