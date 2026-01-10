import { useTranslation } from 'react-i18next'
import { IconUsers } from '@tabler/icons-react'
import { Group } from '@mantine/core'
import type { TeamDetailDto } from '@/api/dto'
import { Card, CardContent, CardTitle, CardDescription } from '../common/card'
import { Badge, VisibilityBadge, Stat, CardSkeleton } from '../common/card'
import { TeamAvatar } from './TeamAvatar'
import { paths } from '@/config/paths'

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
  const { t } = useTranslation()

  const membersIcon = <IconUsers size={16} />

  return (
    <Card to={paths.team(team.slug)}>
      <CardContent>
        <Group gap="sm" mb="xs">
          <TeamAvatar team={team} size="lg" />
          <CardTitle>{team.name}</CardTitle>
        </Group>
        <CardDescription markdown={true} media={team.about} />

        <Group justify="space-between" mt="md">
          <Stat icon={membersIcon}>{t('memberCount', { count: team.memberCount })}</Stat>

          <Group gap="xs">
            {team.visibility === 'TEAM' && (
              <VisibilityBadge visibility={team.visibility} showIcon={false} />
            )}
            {showRole && team.role && (
              <Badge variant={roleBadgeVariants[team.role]}>
                {t(`roles.${team.role satisfies 'ADMIN' | 'ORGANIZER' | 'MEMBER'}`)}
              </Badge>
            )}
          </Group>
        </Group>
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
