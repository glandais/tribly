import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { UsersIcon, PencilIcon } from '@heroicons/react/24/outline'
import { useLeaveTeam, useJoinTeam } from '../../hooks/useTeam'
import { useAuth } from '../../hooks/useAuth'
import { ConfirmDialog } from '../common/ConfirmDialog'
import { VisibilityBadge } from '../common/card/VisibilityBadge'
import type { TeamDetailDto } from '../../hooks/useTeam'
import { MarkdownDisplay } from '../../components/common/MarkdownDisplay'

interface TeamLayoutProps {
  team: TeamDetailDto
  currentTab: 'publications' | 'rides' | 'posts' | 'routes' | 'members'
  children: React.ReactNode
}

export function TeamLayout({ team, currentTab, children }: TeamLayoutProps) {
  const { t, i18n } = useTranslation('teams')
  const navigate = useNavigate()
  const { isAuthenticated } = useAuth()
  const [showLeaveConfirm, setShowLeaveConfirm] = useState(false)

  const isMember = !!team.role
  const isAdmin = team.role === 'ADMIN'
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
        navigate('/teams')
      },
    })
  }

  const tabs = [
    {
      id: 'publications',
      path: `/teams/${team.slug}/publications`,
      label: t('detail.tabs.publications'),
    },
    { id: 'rides', path: `/teams/${team.slug}/rides`, label: t('detail.tabs.rides') },
    { id: 'posts', path: `/teams/${team.slug}/posts`, label: t('detail.tabs.posts') },
    { id: 'routes', path: `/teams/${team.slug}/routes`, label: t('detail.tabs.routes') },
  ]

  // Only admins can see members tab
  if (isAdmin) {
    tabs.push({
      id: 'members',
      path: `/teams/${team.slug}/members`,
      label: t('detail.tabs.members'),
    })
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="pt-14 sm:pt-8 sm:pl-32">
        <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-4">
          <div>
            <div className="flex items-center gap-3">
              <h1 className="text-3xl font-bold text-gray-900">{team.name}</h1>
              {team.visibility === 'TEAM' && <VisibilityBadge visibility={team.visibility} />}
            </div>
            {team.description && (
              <MarkdownDisplay
                content={team.description}
                className="mt-2 text-gray-600 max-w-2xl"
              />
            )}
            <div className="mt-3 flex items-center gap-4 text-sm text-gray-500">
              <span className="flex items-center">
                <UsersIcon className="w-4 h-4 mr-1" />
                {t('detail.info.memberCount', { count: team.memberCount })}
              </span>
              {team.createdAt && (
                <span>
                  {t('detail.info.created', {
                    date: new Date(team.createdAt).toLocaleDateString(i18n.language, {
                      day: 'numeric',
                      month: 'long',
                      year: 'numeric',
                    }),
                  })}
                </span>
              )}
            </div>
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

            {isAdmin && (
              <Link
                to={`/teams/${team.slug}/edit`}
                className="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-xs text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-hidden focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
              >
                <PencilIcon className="w-4 h-4 mr-2" />
                {t('detail.actions.edit')}
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
