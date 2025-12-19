import { useTranslation } from 'react-i18next'
import type { TeamDto, TeamWithRoleDto } from '../../hooks/useTeam'
import { Card, CardContent, CardTitle, CardDescription } from '../common/card'
import { Badge, VisibilityBadge, Stat, CardSkeleton } from '../common/card'

interface TeamCardProps {
  team: TeamDto | TeamWithRoleDto
  showRole?: boolean
}

function isTeamWithRole(team: TeamDto | TeamWithRoleDto): team is TeamWithRoleDto {
  return 'role' in team
}

const roleBadgeVariants: Record<string, 'purple' | 'blue' | 'gray'> = {
  ADMIN: 'purple',
  ORGANIZER: 'blue',
  MEMBER: 'gray',
}

export function TeamCard({ team, showRole = false }: TeamCardProps) {
  const { t } = useTranslation('common')

  const membersIcon = (
    <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
      <path
        strokeLinecap="round"
        strokeLinejoin="round"
        strokeWidth={2}
        d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"
      />
    </svg>
  )

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
            {showRole && isTeamWithRole(team) && (
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
