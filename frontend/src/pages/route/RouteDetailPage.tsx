import { useState } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import i18next from 'i18next'
import { paths } from '../../config/paths'
import { MapIcon, ArrowUpIcon, ArrowDownIcon, ArrowDownTrayIcon } from '@heroicons/react/24/outline'
import { useGetRoute, useDeleteRoute, getListRoutesQueryKey } from '@/api/endpoints/routes/routes'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { RouteMapView } from '../../components/route/RouteMapView'
import { ConfirmDialog } from '../../components/common/ConfirmDialog'
import { MediaDisplay } from '../../components/common/MediaDisplay'
import { EntityLogo } from '../../components/common/EntityLogo'
import { CommentSection } from '../../components/comment'

export function RouteDetailPage() {
  const { teamSlug, routeSlug } = useParams<{ teamSlug: string; routeSlug: string }>()
  const { t } = useTranslation('routes')
  const { t: tCommon } = useTranslation('common')
  const { t: tErrors } = useTranslation('errors')
  const navigate = useNavigate()
  const queryClient = useQueryClient()

  const { data: team } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
  const { data: route, isLoading: routeLoading } = useGetRoute(teamSlug!, routeSlug!, {
    query: { enabled: !!teamSlug && !!routeSlug },
  })
  const deleteRouteMutation = useDeleteRoute()

  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false)

  const isMember = !!team?.role
  const canEdit = team && (team.role === 'ADMIN' || team.role === 'ORGANIZER')

  const handleDelete = async () => {
    if (routeSlug && teamSlug) {
      await deleteRouteMutation.mutateAsync(
        { slug: teamSlug, routeSlug },
        {
          onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: getListRoutesQueryKey(teamSlug) })
            toast.success(i18next.t('routes:notifications.deleted'))
            navigate(paths.routes(teamSlug))
          },
        }
      )
    }
  }

  const getClimbCategoryColor = (category: string) => {
    switch (category) {
      case 'HC':
        return 'bg-purple-100 text-purple-800 border-purple-300'
      case 'CAT1':
        return 'bg-red-100 text-red-800 border-red-300'
      case 'CAT2':
        return 'bg-orange-100 text-orange-800 border-orange-300'
      case 'CAT3':
        return 'bg-yellow-100 text-yellow-800 border-yellow-300'
      case 'CAT4':
        return 'bg-green-100 text-green-800 border-green-300'
      default:
        return 'bg-gray-100 text-gray-800 border-gray-300'
    }
  }

  if (routeLoading) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="animate-pulse">
          <div className="h-8 bg-gray-200 rounded-sm w-1/4 mb-4" />
          <div className="h-96 bg-gray-200 rounded-sm mb-8" />
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
            {[...Array(3)].map((_, i) => (
              <div key={i} className="h-24 bg-gray-200 rounded-sm" />
            ))}
          </div>
        </div>
      </div>
    )
  }

  if (!route) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="text-center">
          <p className="text-gray-500">{tErrors('api.notFound')}</p>
        </div>
      </div>
    )
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Header */}
      <div className="mb-6">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
          <div>
            <div className="flex items-center gap-3">
              <EntityLogo logo={route.media.assets.logo} alt={route.name} size="lg" />
              <h1 className="text-3xl font-bold text-gray-900">{route.name}</h1>
            </div>
            <MediaDisplay media={route.media} className="mt-2 text-gray-600" />
          </div>
          {canEdit && (
            <div className="mt-4 sm:mt-0 flex gap-3">
              <Link
                to={paths.routeEdit(teamSlug!, routeSlug!)}
                className="px-4 py-2 border border-gray-300 rounded-md shadow-xs text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
              >
                {tCommon('actions.edit')}
              </Link>
              <button
                onClick={() => setShowDeleteConfirm(true)}
                className="px-4 py-2 border border-red-300 rounded-md shadow-xs text-sm font-medium text-red-700 bg-white hover:bg-red-50"
              >
                {tCommon('buttons.delete')}
              </button>
            </div>
          )}
        </div>
      </div>

      <ConfirmDialog
        isOpen={showDeleteConfirm}
        onClose={() => setShowDeleteConfirm(false)}
        onConfirm={handleDelete}
        title={t('detail.deleteConfirm.title')}
        message={t('detail.deleteConfirm.message')}
        confirmText={tCommon('buttons.delete')}
        variant="danger"
        isLoading={deleteRouteMutation.isPending}
      />

      {/* Download Section */}
      <div className="bg-white rounded-lg shadow-sm p-6  mb-8">
        <div className="flex flex-wrap gap-3">
          <a
            href={route.media.assets.gpx?.url}
            className="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-xs text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
          >
            <ArrowDownTrayIcon className="w-5 h-5 mr-2 -ml-1" />
            {t('detail.download.gpx')}
          </a>
          <a
            href={route.media.assets.fit?.url}
            className="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-xs text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
          >
            <ArrowDownTrayIcon className="w-5 h-5 mr-2 -ml-1" />
            {t('detail.download.fit')}
          </a>
        </div>
      </div>

      {/* Interactive Map with Elevation Chart */}
      <div className="mb-8">
        <RouteMapView route={route} />
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div className="bg-white rounded-lg shadow-sm p-6">
          <div className="flex items-center">
            <MapIcon className="h-8 w-8 text-indigo-600 mr-3" />
            <div>
              <p className="text-sm text-gray-500">{t('detail.stats.distance')}</p>
              <p className="text-2xl font-bold text-gray-900">
                {(route.distance / 1000).toFixed(1)} {tCommon('units.km')}
              </p>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-sm p-6">
          <div className="flex items-center">
            <ArrowUpIcon className="h-8 w-8 text-green-600 mr-3" />
            <div>
              <p className="text-sm text-gray-500">{t('detail.stats.elevationGain')}</p>
              <p className="text-2xl font-bold text-gray-900">
                {route.elevationGain}
                {tCommon('units.m')}
              </p>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-sm p-6">
          <div className="flex items-center">
            <ArrowDownIcon className="h-8 w-8 text-red-600 mr-3" />
            <div>
              <p className="text-sm text-gray-500">{t('detail.stats.elevationLoss')}</p>
              <p className="text-2xl font-bold text-gray-900">
                {route.elevationLoss}
                {tCommon('units.m')}
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Route Info */}
      <div className="bg-white rounded-lg shadow-sm p-6 mb-8">
        <h2 className="text-xl font-bold text-gray-900 mb-4">{t('detail.info.title')}</h2>
        <dl className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          {route.surfaceType && (
            <>
              <dt className="text-sm font-medium text-gray-500">{t('detail.info.surfaceType')}</dt>
              <dd>
                <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                  {t(
                    `surfaceType.${route.surfaceType satisfies 'ROAD' | 'GRAVEL' | 'MTB' | 'MIXED'}`
                  )}
                </span>
              </dd>
            </>
          )}
          <dt className="text-sm font-medium text-gray-500">{t('detail.info.visibility')}</dt>
          <dd>
            <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800">
              {tCommon(`visibility.${route.visibility.toLowerCase() as 'public' | 'team'}`)}
            </span>
          </dd>
          <dt className="text-sm font-medium text-gray-500">{t('detail.info.createdAt')}</dt>
          <dd className="text-sm text-gray-900">
            {new Date(route.createdAt).toLocaleDateString()}
          </dd>
        </dl>
      </div>

      {/* Climbs Section */}
      {(() => {
        const allClimbs = route.tracks?.flatMap((track) => track.climbs) || []
        return (
          allClimbs.length > 0 && (
            <div className="bg-white rounded-lg shadow-sm p-6 mb-8">
              <h2 className="text-xl font-bold text-gray-900 mb-4">
                {t('detail.climbs.title')} ({allClimbs.length})
              </h2>
              <div className="space-y-4">
                {allClimbs.map((climb, index) => (
                  <div
                    key={index}
                    className="border rounded-lg p-4 hover:bg-gray-50 transition-colors"
                  >
                    <div className="flex items-start justify-between mb-2">
                      <div>
                        <h3 className="font-semibold text-gray-900">
                          {t('detail.climbs.unnamed', { number: index + 1 })}
                        </h3>
                        <p className="text-sm text-gray-600">
                          {t('detail.climbs.distance', {
                            start: (climb.startDistance / 1000).toFixed(1),
                            end: (climb.endDistance / 1000).toFixed(1),
                          })}
                        </p>
                      </div>
                      {climb.category && (
                        <span
                          className={`inline-flex items-center px-3 py-1 rounded-full text-sm font-medium border ${getClimbCategoryColor(climb.category)}`}
                        >
                          {t(
                            `climbCategory.${climb.category satisfies 'HC' | 'CAT1' | 'CAT2' | 'CAT3' | 'CAT4'}`
                          )}
                        </span>
                      )}
                    </div>
                    <div className="grid grid-cols-3 gap-4 text-sm">
                      <div>
                        <span className="text-gray-500">{t('detail.climbs.gain')}: </span>
                        <span className="font-medium text-gray-900">
                          {climb.elevationGain}
                          {tCommon('units.m')}
                        </span>
                      </div>
                      <div>
                        <span className="text-gray-500">{t('detail.climbs.avgGradient')}: </span>
                        <span className="font-medium text-gray-900">
                          {climb.averageGradient.toFixed(1)}%
                        </span>
                      </div>
                      <div>
                        <span className="text-gray-500">{t('detail.climbs.maxGradient')}: </span>
                        <span className="font-medium text-gray-900">
                          {climb.maxGradient.toFixed(1)}%
                        </span>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )
        )
      })()}

      {/* Comments Section - only visible to team members */}
      {isMember && (
        <CommentSection
          teamSlug={teamSlug!}
          entityType="routes"
          entitySlug={routeSlug!}
          isOrganizer={!!canEdit}
        />
      )}
    </div>
  )
}
