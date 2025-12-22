import { useState } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import {
  MapIcon,
  ArrowUpIcon,
  ArrowDownIcon,
  ArrowDownTrayIcon,
  ChevronLeftIcon,
} from '@heroicons/react/24/outline'
import { MapContainer, TileLayer, Polyline } from 'react-leaflet'
import { useRoute, useRouteClimbs, useGpxTrack, useDeleteRoute } from '../../hooks/useRoute'
import { useTeam } from '../../hooks/useTeam'
import { ConfirmDialog } from '../../components/common/ConfirmDialog'
import 'leaflet/dist/leaflet.css'

export function RouteDetailPage() {
  const { teamSlug, routeId } = useParams<{ teamSlug: string; routeId: string }>()
  const { t } = useTranslation('routes')
  const { t: tCommon } = useTranslation('common')
  const { t: tErrors } = useTranslation('errors')
  const navigate = useNavigate()

  const { data: team } = useTeam(teamSlug)
  const { data: route, isLoading: routeLoading } = useRoute(teamSlug, routeId)
  const { data: climbsData, isLoading: climbsLoading } = useRouteClimbs(teamSlug, routeId)
  const { data: gpxTrack, isLoading: trackLoading } = useGpxTrack(teamSlug, routeId)
  const deleteRoute = useDeleteRoute(teamSlug!)

  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false)

  const canEdit = team && (team.role === 'ADMIN' || team.role === 'ORGANIZER')

  const handleDelete = async () => {
    if (routeId) {
      await deleteRoute.mutateAsync(routeId)
      navigate(`/teams/${teamSlug}/routes`)
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

  if (routeLoading || climbsLoading || trackLoading) {
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

  if (!route || !gpxTrack) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="text-center">
          <p className="text-gray-500">{tErrors('api.notFound')}</p>
          <Link
            to={`/teams/${teamSlug}/routes`}
            className="mt-4 inline-flex items-center text-sm text-gray-500 hover:text-gray-700"
          >
            <ChevronLeftIcon className="w-4 h-4 mr-1" />
            {t('detail.backToList')}
          </Link>
        </div>
      </div>
    )
  }

  const trackPoints = gpxTrack.trackPoints.map((p) => [p.lat, p.lng] as [number, number])
  const bounds =
    trackPoints.length > 0
      ? (trackPoints.reduce(
          (acc, [lat, lng]) => [
            [Math.min(acc[0][0], lat), Math.min(acc[0][1], lng)],
            [Math.max(acc[1][0], lat), Math.max(acc[1][1], lng)],
          ],
          [
            [trackPoints[0][0], trackPoints[0][1]],
            [trackPoints[0][0], trackPoints[0][1]],
          ]
        ) as [[number, number], [number, number]])
      : undefined

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Header */}
      <div className="mb-6">
        <Link
          to={`/teams/${teamSlug}/routes`}
          className="inline-flex items-center text-sm text-gray-500 hover:text-gray-700"
        >
          <ChevronLeftIcon className="w-4 h-4 mr-1" />
          {t('detail.backToList')}
        </Link>
        <div className="mt-4 flex flex-col sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h1 className="text-3xl font-bold text-gray-900">{route.name}</h1>
            {route.description && <p className="mt-2 text-gray-600">{route.description}</p>}
          </div>
          {canEdit && (
            <div className="mt-4 sm:mt-0 flex gap-3">
              <Link
                to={`/teams/${teamSlug}/routes/${routeId}/edit`}
                className="px-4 py-2 border border-gray-300 rounded-md shadow-xs text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
              >
                {tCommon('buttons.edit')}
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
        isLoading={deleteRoute.isPending}
      />

      {/* Map */}
      <div className="mb-8 rounded-lg overflow-hidden shadow-lg">
        <MapContainer
          bounds={bounds}
          style={{ height: '500px', width: '100%' }}
          scrollWheelZoom={false}
        >
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />
          <Polyline
            positions={trackPoints}
            pathOptions={{ color: '#4f46e5', weight: 4, opacity: 0.8 }}
          />
        </MapContainer>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div className="bg-white rounded-lg shadow-sm p-6">
          <div className="flex items-center">
            <MapIcon className="h-8 w-8 text-indigo-600 mr-3" />
            <div>
              <p className="text-sm text-gray-500">{t('detail.stats.distance')}</p>
              <p className="text-2xl font-bold text-gray-900">
                {(route.distance / 1000).toFixed(1)} km
              </p>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-sm p-6">
          <div className="flex items-center">
            <ArrowUpIcon className="h-8 w-8 text-green-600 mr-3" />
            <div>
              <p className="text-sm text-gray-500">{t('detail.stats.elevationGain')}</p>
              <p className="text-2xl font-bold text-gray-900">{route.elevationGain}m</p>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-sm p-6">
          <div className="flex items-center">
            <ArrowDownIcon className="h-8 w-8 text-red-600 mr-3" />
            <div>
              <p className="text-sm text-gray-500">{t('detail.stats.elevationLoss')}</p>
              <p className="text-2xl font-bold text-gray-900">{route.elevationLoss}m</p>
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
                  {t(`surfaceType.${route.surfaceType}`)}
                </span>
              </dd>
            </>
          )}
          <dt className="text-sm font-medium text-gray-500">{t('detail.info.visibility')}</dt>
          <dd>
            <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800">
              {tCommon(`visibility.${route.visibility.toLowerCase()}`)}
            </span>
          </dd>
          <dt className="text-sm font-medium text-gray-500">{t('detail.info.createdAt')}</dt>
          <dd className="text-sm text-gray-900">
            {new Date(route.createdAt).toLocaleDateString()}
          </dd>
        </dl>
      </div>

      {/* Climbs Section */}
      {climbsData && climbsData.climbs.length > 0 && (
        <div className="bg-white rounded-lg shadow-sm p-6 mb-8">
          <h2 className="text-xl font-bold text-gray-900 mb-4">
            {t('detail.climbs.title')} ({climbsData.climbs.length})
          </h2>
          <div className="space-y-4">
            {climbsData.climbs.map((climb, index) => (
              <div
                key={climb.id}
                className="border rounded-lg p-4 hover:bg-gray-50 transition-colors"
              >
                <div className="flex items-start justify-between mb-2">
                  <div>
                    <h3 className="font-semibold text-gray-900">
                      {climb.name || t('detail.climbs.unnamed', { number: index + 1 })}
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
                      {t(`climbCategory.${climb.category}`)}
                    </span>
                  )}
                </div>
                <div className="grid grid-cols-3 gap-4 text-sm">
                  <div>
                    <span className="text-gray-500">{t('detail.climbs.gain')}: </span>
                    <span className="font-medium text-gray-900">{climb.elevationGain}m</span>
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
      )}

      {/* Download Section */}
      <div className="bg-white rounded-lg shadow-sm p-6">
        <h2 className="text-xl font-bold text-gray-900 mb-4">{t('detail.download.title')}</h2>
        <div className="flex flex-wrap gap-3">
          <a
            href={`/api/download/${route.visibility.toLowerCase()}/teams/${teamSlug}/routes/${routeId}/gpx`}
            className="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-xs text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
          >
            <ArrowDownTrayIcon className="w-5 h-5 mr-2 -ml-1" />
            {t('detail.download.gpx')}
          </a>
          <a
            href={`/api/download/${route.visibility.toLowerCase()}/teams/${teamSlug}/routes/${routeId}/fit`}
            className="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-xs text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
          >
            <ArrowDownTrayIcon className="w-5 h-5 mr-2 -ml-1" />
            {t('detail.download.fit')}
          </a>
        </div>
      </div>
    </div>
  )
}
