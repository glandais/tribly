import { Link, Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { ArrowLeftIcon } from '@heroicons/react/24/outline'
import type { TeamDetailDto } from '../../hooks/useTeam'
import { paths } from '@/config/paths'

export type AdminTab = 'ride-templates' | 'places' | 'pages' | 'members' | 'settings'

interface TeamAdminLayoutProps {
  team: TeamDetailDto
  currentTab: AdminTab
  children: React.ReactNode
}

export function TeamAdminLayout({ team, currentTab, children }: TeamAdminLayoutProps) {
  const { t } = useTranslation('teams')

  const isAdmin = team.role === 'ADMIN'
  const isOrganizer = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  // Only ADMIN or ORGANIZER can access admin section
  if (!isOrganizer) {
    return <Navigate to={paths.team(team.slug)} replace />
  }

  const tabs: { id: AdminTab; path: string; label: string; adminOnly?: boolean }[] = [
    {
      id: 'ride-templates',
      path: paths.rideTemplates(team.slug),
      label: t('admin.tabs.rideTemplates'),
    },
    {
      id: 'places',
      path: paths.teamAdminPlaces(team.slug),
      label: t('admin.tabs.places'),
    },
    {
      id: 'pages',
      path: paths.teamAdminPages(team.slug),
      label: t('admin.tabs.pages'),
      adminOnly: true,
    },
    {
      id: 'members',
      path: paths.teamMembers(team.slug),
      label: t('admin.tabs.members'),
      adminOnly: true,
    },
    {
      id: 'settings',
      path: paths.teamSettings(team.slug),
      label: t('admin.tabs.settings'),
      adminOnly: true,
    },
  ]

  // Filter tabs based on role
  const visibleTabs = tabs.filter((tab) => !tab.adminOnly || isAdmin)

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Header with back link */}
      <div className="mb-6">
        <Link
          to={paths.team(team.slug)}
          className="inline-flex items-center text-sm text-gray-500 hover:text-gray-700"
        >
          <ArrowLeftIcon className="w-4 h-4 mr-1" />
          {t('admin.backToTeam', { teamName: team.name })}
        </Link>
        <h1 className="mt-2 text-2xl font-bold text-gray-900">{t('admin.title')}</h1>
      </div>

      {/* Admin Navigation */}
      <div className="border-b border-gray-200">
        <nav className="-mb-px flex space-x-8">
          {visibleTabs.map((tab) => (
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
    </div>
  )
}
