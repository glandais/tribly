import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import type { TeamDetail } from '../../hooks/useTeam';

interface TeamLayoutProps {
  team: TeamDetail;
  currentTab: 'rides' | 'trips' | 'routes' | 'members';
  children: React.ReactNode;
}

export function TeamLayout({ team, currentTab, children }: TeamLayoutProps) {
  const { t, i18n } = useTranslation('teams');
  const { t: tCommon } = useTranslation('common');

  const isAdmin = team.userRole === 'ADMIN';

  const tabs = [
    { id: 'rides', path: `/teams/${team.slug}/rides`, label: t('detail.tabs.rides') },
    { id: 'trips', path: `/teams/${team.slug}/trips`, label: t('detail.tabs.trips') },
    { id: 'routes', path: `/teams/${team.slug}/routes`, label: t('detail.tabs.routes') },
  ];

  // Only admins can see members tab
  if (isAdmin) {
    tabs.push({ id: 'members', path: `/teams/${team.slug}/members`, label: t('detail.tabs.members') });
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Header */}
      <div className="relative mb-8">
        {team.coverImageUrl ? (
          <img
            src={team.coverImageUrl}
            alt=""
            className="w-full h-48 sm:h-64 object-cover rounded-lg"
          />
        ) : (
          <div className="w-full h-48 sm:h-64 bg-gradient-to-r from-indigo-500 to-purple-600 rounded-lg" />
        )}

        <div className="absolute -bottom-12 left-6 flex items-end gap-4">
          {team.logoUrl ? (
            <img
              src={team.logoUrl}
              alt={team.name}
              className="w-24 h-24 rounded-xl border-4 border-white bg-white shadow-lg"
            />
          ) : (
            <div className="w-24 h-24 rounded-xl border-4 border-white bg-indigo-600 shadow-lg flex items-center justify-center">
              <span className="text-3xl font-bold text-white">{team.name.charAt(0).toUpperCase()}</span>
            </div>
          )}
        </div>
      </div>

      <div className="pt-14 sm:pt-8 sm:pl-32">
        <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-4">
          <div>
            <div className="flex items-center gap-3">
              <h1 className="text-3xl font-bold text-gray-900">{team.name}</h1>
              {!team.isPublic && (
                <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800">
                  <svg className="w-3 h-3 mr-1" fill="currentColor" viewBox="0 0 20 20">
                    <path
                      fillRule="evenodd"
                      d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z"
                      clipRule="evenodd"
                    />
                  </svg>
                  {tCommon('visibility.private')}
                </span>
              )}
            </div>
            {team.description && <p className="mt-2 text-gray-600 max-w-2xl">{team.description}</p>}
            <div className="mt-3 flex items-center gap-4 text-sm text-gray-500">
              <span className="flex items-center">
                <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"
                  />
                </svg>
                {t('detail.info.memberCount', { count: team.memberCount })}
                {team.maxMembers && t('detail.info.maxMembers', { max: team.maxMembers })}
              </span>
              {team.createdAt && (
                <span>
                  {t('detail.info.created', {
                    date: new Date(team.createdAt).toLocaleDateString(i18n.language, {
                      year: 'numeric',
                      month: 'long',
                    }),
                  })}
                </span>
              )}
            </div>
          </div>

          {isAdmin && (
            <div className="flex items-center gap-3">
              <Link
                to={`/teams/${team.slug}/settings`}
                className="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
              >
                <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"
                  />
                </svg>
                {t('detail.actions.edit')}
              </Link>
            </div>
          )}
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
    </div>
  );
}
