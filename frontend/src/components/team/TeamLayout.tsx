import { useMemo, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { Stack, Group, Title, Button, Box } from '@mantine/core'
import {
  IconNews,
  IconCalendar,
  IconRoute,
  IconTags,
  IconInfoCircle,
  IconFileText,
} from '@tabler/icons-react'
import { getGetTeamQueryKey } from '@/api/endpoints/teams/teams'
import {
  useLeaveTeam,
  useJoinTeam,
  getGetMembersQueryKey,
} from '@/api/endpoints/team-members/team-members'
import { useAuth } from '../../hooks/useAuth'
import { useFavicon } from '../../hooks/useFavicon'
import { ConfirmDialog } from '../common/ConfirmDialog'
import { NavButtons, type NavButtonItem } from '../common/NavButtons'
import { VisibilityBadge } from '../card/common'
import { TeamAvatar } from './TeamAvatar'
import type { TeamDetailDto } from '@/api/dto'
import { paths } from '@/config/paths'

interface TeamLayoutProps {
  team: TeamDetailDto
  /** Tab identifier: 'publications', 'routes', 'about', or a page slug for dynamic pages */
  currentTab: string
  children: React.ReactNode
}

export function TeamLayout({ team, currentTab, children }: TeamLayoutProps) {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const { isAuthenticated } = useAuth()
  const [showLeaveConfirm, setShowLeaveConfirm] = useState(false)

  // Set favicon to team logo
  useFavicon(team.about?.assets?.logo?.imageUrl?.replace('{size}', String(128)))

  const isMember = !!team.role
  const isAdmin = team.role === 'ADMIN'
  const isOrganizer = team.role === 'ADMIN' || team.role === 'ORGANIZER'
  const canJoin = isAuthenticated && !isMember && team.visibility === 'PUBLIC'
  const canLeave = isMember && !isAdmin

  const joinMutation = useJoinTeam()
  const leaveMutation = useLeaveTeam()

  const handleJoin = () => {
    joinMutation.mutate(
      { teamSlug: team.slug },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetTeamQueryKey(team.slug) })
          queryClient.invalidateQueries({ queryKey: getGetMembersQueryKey(team.slug) })
        },
      }
    )
  }

  const handleLeave = () => {
    leaveMutation.mutate(
      { teamSlug: team.slug },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetTeamQueryKey(team.slug) })
          queryClient.invalidateQueries({ queryKey: getGetMembersQueryKey(team.slug) })
          navigate(paths.teams())
        },
      }
    )
  }

  // Build tabs list with dynamic pages
  const tabs = useMemo(() => {
    const baseTabs: NavButtonItem[] = [
      {
        id: 'publications',
        path: paths.team(team.slug),
        label: t('teams.publications.list.title'),
        icon: IconNews,
      },
      ...(isMember && (team.enableRides || team.enableTrips)
        ? [
            {
              id: 'calendar',
              path: paths.teamCalendar(team.slug),
              label: t('teams.detail.tabs.calendar'),
              icon: IconCalendar,
            },
          ]
        : []),
      ...(team.enableRoutes
        ? [
            {
              id: 'routes',
              path: paths.routes(team.slug),
              label: t('teams.detail.tabs.routes'),
              icon: IconRoute,
            },
          ]
        : []),
      ...(isMember && team.enableAds
        ? [
            {
              id: 'ads',
              path: paths.ads(team.slug),
              label: t('ads.title'),
              icon: IconTags,
            },
          ]
        : []),
      {
        id: 'about',
        path: paths.teamAbout(team.slug),
        label: t('teams.detail.tabs.about'),
        icon: IconInfoCircle,
      },
    ]

    // Add dynamic pages - filter by visibility (PUBLIC pages or member can see TEAM pages)
    const visiblePages = (team.pages ?? []).filter(
      (page) => page.visibility === 'PUBLIC' || isMember
    )

    const pageTabs: NavButtonItem[] = visiblePages.map((page) => ({
      id: page.slug,
      path: paths.teamPage(team.slug, page.slug),
      label: page.title,
      icon: IconFileText,
    }))

    return [...baseTabs, ...pageTabs]
  }, [
    team.slug,
    team.pages,
    team.enableAds,
    team.enableRoutes,
    team.enableRides,
    team.enableTrips,
    isMember,
    t,
  ])

  return (
    <Stack>
      {/* Team Header */}
      <Group align="flex-start" wrap="wrap">
        <TeamAvatar team={team} size="xl" />
        <Box style={{ flex: 1 }}>
          <Group justify="space-between" align="flex-start" wrap="wrap">
            <Group gap="sm">
              <Title order={1}>{team.name}</Title>
              {team.visibility === 'TEAM' && <VisibilityBadge visibility={team.visibility} />}
            </Group>

            <Group gap="sm">
              {canJoin && (
                <Button onClick={handleJoin} loading={joinMutation.isPending}>
                  {joinMutation.isPending
                    ? t('teams.detail.actions.joining')
                    : t('teams.detail.actions.join')}
                </Button>
              )}

              {canLeave && (
                <Button variant="default" onClick={() => setShowLeaveConfirm(true)}>
                  {t('teams.detail.actions.leave')}
                </Button>
              )}

              {isOrganizer && (
                <Button variant="default" component={Link} to={paths.teamAdmin(team.slug)}>
                  {t('teams.detail.actions.admin')}
                </Button>
              )}
            </Group>
          </Group>
        </Box>
      </Group>

      {/* Team Navigation */}
      <NavButtons items={tabs} currentId={currentTab} />

      {/* Page Content */}
      <div>{children}</div>

      {/* Leave Team Confirmation Dialog */}
      <ConfirmDialog
        isOpen={showLeaveConfirm}
        onClose={() => setShowLeaveConfirm(false)}
        onConfirm={handleLeave}
        title={t('teams.detail.actions.leave')}
        message={t('teams.detail.actions.confirmLeave')}
        confirmText={t('teams.detail.actions.leave')}
        variant="warning"
        isLoading={leaveMutation.isPending}
      />
    </Stack>
  )
}
