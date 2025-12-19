import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import type { TeamDto, TeamWithRoleDto } from '../../hooks/useTeam'

interface TeamCardProps {
  team: TeamDto | TeamWithRoleDto
  showRole?: boolean
}

function isTeamWithRole(team: TeamDto | TeamWithRoleDto): team is TeamWithRoleDto {
  return 'role' in team
}

export function TeamCard({ team, showRole = false }: TeamCardProps) {
  const { t } = useTranslation('common')

  const roleBadgeColors = {
    ADMIN: 'bg-purple-100 text-purple-800',
    ORGANIZER: 'bg-blue-100 text-blue-800',
    MEMBER: 'bg-gray-100 text-gray-800',
  }

  return (
    <Link
      to={`/teams/${team.slug}`}
      className="block bg-white rounded-lg shadow-xs border border-gray-200 hover:shadow-md hover:border-gray-300 transition-all"
    >
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

      <div className={`p-4 ${team.logoUrl ? 'pt-8' : ''}`}>
        <div className="flex items-start justify-between">
          <div className="flex-1 min-w-0">
            <h3 className="text-lg font-semibold text-gray-900 truncate">{team.name}</h3>
            {team.description && (
              <p className="mt-1 text-sm text-gray-600 line-clamp-2">{team.description}</p>
            )}
          </div>
        </div>

        <div className="mt-4 flex items-center justify-between">
          <div className="flex items-center text-sm text-gray-500">
            <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"
              />
            </svg>
            {t('memberCount', { count: team.memberCount })}
          </div>

          <div className="flex items-center gap-2">
            {!team.isPublic && (
              <span className="inline-flex items-center px-2 py-0.5 rounded-sm text-xs font-medium bg-yellow-100 text-yellow-800">
                <svg className="w-3 h-3 mr-1" fill="currentColor" viewBox="0 0 20 20">
                  <path
                    fillRule="evenodd"
                    d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z"
                    clipRule="evenodd"
                  />
                </svg>
                {t('visibility.private')}
              </span>
            )}
            {showRole && isTeamWithRole(team) && (
              <span
                className={`inline-flex items-center px-2 py-0.5 rounded-sm text-xs font-medium ${roleBadgeColors[team.role]}`}
              >
                {t(`roles.${team.role}`)}
              </span>
            )}
          </div>
        </div>
      </div>
    </Link>
  )
}

interface TeamCardSkeletonProps {
  count?: number
}

export function TeamCardSkeleton({ count = 1 }: TeamCardSkeletonProps) {
  return (
    <>
      {Array.from({ length: count }).map((_, i) => (
        <div key={i} className="bg-white rounded-lg shadow-xs border border-gray-200 animate-pulse">
          <div className="w-full h-32 bg-gray-200 rounded-t-lg" />
          <div className="p-4">
            <div className="h-5 bg-gray-200 rounded-sm w-3/4 mb-2" />
            <div className="h-4 bg-gray-200 rounded-sm w-full mb-1" />
            <div className="h-4 bg-gray-200 rounded-sm w-2/3 mb-4" />
            <div className="h-4 bg-gray-200 rounded-sm w-1/4" />
          </div>
        </div>
      ))}
    </>
  )
}
