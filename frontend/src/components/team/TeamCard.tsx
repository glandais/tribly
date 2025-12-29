import { useTranslation } from 'react-i18next'
import { UsersIcon } from '@heroicons/react/24/outline'
import type { TeamDetailDto } from '../../hooks/useTeam'
import { Card, CardContent, CardTitle, CardDescription } from '../common/card'
import { Badge, VisibilityBadge, Stat, CardSkeleton } from '../common/card'
import { TeamAvatar } from './TeamAvatar'

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
      <CardContent>
        <div className="flex items-center gap-3 mb-2">
          <TeamAvatar team={team} size="lg" />
          <CardTitle>{team.name}</CardTitle>
        </div>
        <CardDescription markdown={true} media={team.media} />

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
