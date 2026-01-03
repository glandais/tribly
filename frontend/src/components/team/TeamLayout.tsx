import { useMemo, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useLeaveTeam, useJoinTeam } from '../../hooks/useTeam'
import { useAuth } from '../../hooks/useAuth'
import { useFavicon } from '../../hooks/useFavicon'
import { ConfirmDialog } from '../common/ConfirmDialog'
import { VisibilityBadge } from '../common/card/VisibilityBadge'
import { TeamAvatar } from './TeamAvatar'
import type { TeamDetailDto } from '../../hooks/useTeam'
import { paths } from '@/config/paths'

interface TeamLayoutProps {
  team: TeamDetailDto
  /** Tab identifier: 'publications', 'routes', 'about', or a page slug for dynamic pages */
  currentTab: string
  children: React.ReactNode
}

export function TeamLayout({ team, currentTab, children }: TeamLayoutProps) {
  const { t } = useTranslation('teams')
  const navigate = useNavigate()
  const { isAuthenticated } = useAuth()
  const [showLeaveConfirm, setShowLeaveConfirm] = useState(false)

  // Set favicon to team logo
  useFavicon(team.about?.assets?.logo?.url)

  const isMember = !!team.role
  const isAdmin = team.role === 'ADMIN'
  const isOrganizer = team.role === 'ADMIN' || team.role === 'ORGANIZER'
  const canJoin = isAuthenticated && !isMember && team.visibility === 'PUBLIC'
  const canLeave = isMember && !isAdmin

  const joinMutation = useJoinTeam(team.slug)
  const leaveMutation = useLeaveTeam(team.slug)

  const handleJoin = () => {
    joinMutation.mutate()
  }

  const handleLeave = () => {
    leaveMutation.mutate(undefined, {
      onSuccess: () => {
        navigate(paths.teams())
      },
    })
  }

  // Build tabs list with dynamic pages
  const tabs = useMemo(() => {
    const baseTabs = [
      {
        id: 'publications',
        path: paths.team(team.slug),
        label: t('detail.tabs.publications'),
      },
      { id: 'routes', path: paths.routes(team.slug), label: t('detail.tabs.routes') },
      { id: 'about', path: paths.teamAbout(team.slug), label: t('detail.tabs.about') },
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
  }, [team.slug, team.pages, isMember, t])

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="relative pt-14 sm:pt-8 sm:pl-32">
        {/* Team Avatar - absolute on larger screens, centered on mobile */}
        <div className="absolute left-1/2 -translate-x-1/2 top-0 sm:left-0 sm:translate-x-0 sm:top-0">
          <TeamAvatar team={team} size="xl" className="ring-4 ring-white shadow-lg" />
        </div>

        <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-4">
          <div className="flex items-center gap-3">
            <h1 className="text-3xl font-bold text-gray-900">{team.name}</h1>
            {team.visibility === 'TEAM' && <VisibilityBadge visibility={team.visibility} />}
          </div>

          <div className="flex items-center gap-3">
            {canJoin && (
              <button
                onClick={handleJoin}
                disabled={joinMutation.isPending}
                className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-hidden focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
              >
                {joinMutation.isPending ? t('detail.actions.joining') : t('detail.actions.join')}
              </button>
            )}

            {canLeave && (
              <button
                onClick={() => setShowLeaveConfirm(true)}
                className="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-xs text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-hidden focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
              >
                {t('detail.actions.leave')}
              </button>
            )}

            {isOrganizer && (
              <Link
                to={paths.teamAdmin(team.slug)}
                className="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-xs text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-hidden focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
              >
                {t('detail.actions.admin')}
              </Link>
            )}
          </div>
        </div>
      </div>

      {/* Team Navigation */}
      <div className="mt-8 border-b border-gray-200">
        <nav className="-mb-px flex space-x-8">
          {tabs.map((tab) => (
            <Link
              key={tab.id}
              to={tab.path}
              className={
                currentTab === tab.id
                  ? 'border-indigo-500 text-indigo-600 py-4 px-1 border-b-2 font-medium text-sm'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300 py-4 px-1 border-b-2 font-medium text-sm'
              }
            >
              {tab.label}
            </Link>
          ))}
        </nav>
      </div>

      {/* Page Content */}
      <div>{children}</div>

      {/* Leave Team Confirmation Dialog */}
      <ConfirmDialog
        isOpen={showLeaveConfirm}
        onClose={() => setShowLeaveConfirm(false)}
        onConfirm={handleLeave}
        title={t('detail.actions.leave')}
        message={t('detail.actions.confirmLeave')}
        confirmText={t('detail.actions.leave')}
        variant="warning"
        isLoading={leaveMutation.isPending}
      />
    </div>
  )
}
