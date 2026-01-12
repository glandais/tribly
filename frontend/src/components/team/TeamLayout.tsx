import { useMemo, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { Stack, Group, Title, Button, Tabs, Box } from '@mantine/core'
import { getGetTeamQueryKey } from '@/api/endpoints/teams/teams'
import {
  useLeaveTeam,
  useJoinTeam,
  getGetMembersQueryKey,
} from '@/api/endpoints/team-members/team-members'
import { useAuth } from '../../hooks/useAuth'
import { useFavicon } from '../../hooks/useFavicon'
import { ConfirmDialog } from '../common/ConfirmDialog'
import { VisibilityBadge } from '../common/card/VisibilityBadge'
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
    const baseTabs = [
      {
        id: 'publications',
        path: paths.team(team.slug),
        label: t('teams.publications.list.title'),
      },
      {
        id: 'calendar',
        path: paths.teamCalendar(team.slug),
        label: t('teams.detail.tabs.calendar'),
      },
      { id: 'routes', path: paths.routes(team.slug), label: t('teams.detail.tabs.routes') },
      ...(team.enableAds ? [{ id: 'ads', path: paths.ads(team.slug), label: t('ads.title') }] : []),
      { id: 'about', path: paths.teamAbout(team.slug), label: t('teams.detail.tabs.about') },
    ]

    // Add dynamic pages - filter by visibility (PUBLIC pages or member can see TEAM pages)
    const visiblePages = (team.pages ?? []).filter(
      (page) => page.visibility === 'PUBLIC' || isMember
    )

    const pageTabs = visiblePages.map((page) => ({
      id: page.slug,
      path: paths.teamPage(team.slug, page.slug),
      label: page.title,
    }))

    return [...baseTabs, ...pageTabs]
  }, [team.slug, team.pages, team.enableAds, isMember, t])

  return (
    <Stack gap="lg">
      {/* Team Header */}
      <Group align="flex-start" gap="lg" wrap="wrap">
        <TeamAvatar team={team} size="xl" />
        <Box style={{ flex: 1 }}>
          <Group justify="space-between" align="flex-start" wrap="wrap" gap="md">
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
      <Tabs value={currentTab}>
        <Tabs.List>
          {tabs.map((tab) => (
            <Tabs.Tab
              key={tab.id}
              value={tab.id}
              renderRoot={(props) => <Link to={tab.path} {...props} />}
            >
              {tab.label}
            </Tabs.Tab>
          ))}
        </Tabs.List>
      </Tabs>

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
