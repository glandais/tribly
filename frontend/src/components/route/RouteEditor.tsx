import { useState, useCallback } from 'react'
import { useTranslation } from 'react-i18next'
import { RouteRequest, SurfaceType } from '@/api/dto'
import type { TeamDetailDto, GeoPoint } from '@/api/dto'
import { LoadingSpinner } from '../common/LoadingSpinner'
import { MediaEditor } from '../common/MediaEditor'
import { SlugEditor } from '../common/SlugEditor'
import { EmbeddedRoutePlanner } from '../planner/EmbeddedRoutePlanner'
import { createRouteBody } from '@/api/zod/routes/routes.zod'
import { zodResolver } from '@hookform/resolvers/zod'
import { useForm, useWatch } from 'react-hook-form'

import { Button } from '@/components/ui/button'
import { Label } from '@/components/ui/label'
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group'
import { Input } from '@/components/ui/input'

import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form'

import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'

export type RouteSourceMode = 'gpx' | 'planner'

const routeSchema = createRouteBody.shape.route.unwrap()

interface RouteEditorProps {
  // Context
  team: TeamDetailDto
  teamSlug: string

  // Initial values (REQUIRED - each page prepares these)
  initialValues: RouteRequest

  // Submission
  onSubmit: (data: RouteRequest, gpxFile?: File) => void | Promise<void>
  onCancel: () => void

  // State
  isPending: boolean
  error?: Error | null

  // UI customization
  isCreateMode?: boolean // true for create (requires source), false for edit
  initialTrack?: number[][] // [lng, lat, ele, dist][] for edit mode with planner
  submitButtonText?: string
  cancelButtonText?: string
  showCancelButton?: boolean

  // Slug editing (only for edit mode)
  currentSlug?: string
  onSlugChange?: (newSlug: string) => Promise<void>
  canEditSlug?: boolean
}

export function RouteEditor({
  team,
  teamSlug,
  initialValues,
  onSubmit,
  onCancel,
  isPending,
  error: _error,
  isCreateMode = false,
  initialTrack,
  submitButtonText,
  cancelButtonText,
  showCancelButton = true,
  currentSlug,
  onSlugChange,
  canEditSlug = false,
}: RouteEditorProps) {
  const { t } = useTranslation()

  const form = useForm<RouteRequest>({
    resolver: zodResolver(routeSchema),
    mode: 'onChange',
    defaultValues: initialValues,
  })

  // Can use planner if creating or if editing with a single-track route
  const canUsePlanner = isCreateMode || !!initialTrack

  // Route source mode (GPX upload or Planner)
  // Default to planner if editing with initialTrack
  const [sourceMode, setSourceMode] = useState<RouteSourceMode>(
    !isCreateMode && initialTrack ? 'planner' : 'gpx'
  )

  const name = useWatch({ control: form.control, name: 'name' })

  const [gpxFile, setGpxFile] = useState<File | null>(null)
  const [plannerPoints, setPlannerPoints] = useState<GeoPoint[]>([])
  const [error, setError] = useState<string | null>(null)

  const handleFileChange = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const file = e.target.files?.[0]
      if (!name && file?.name) {
        const defaultName = file.name.replace(/\.gpx$/i, '')
        form.setValue('name', defaultName)
      }
      if (file) {
        // Validate file type
        if (!file.name.endsWith('.gpx')) {
          setError(t('routes.create.validation.invalidFileType'))
          setGpxFile(null)
          return
        }
        setError(null)
        setGpxFile(file)
      }
    },
    [t, form, name]
  )

  const handleSubmit = async (values: RouteRequest) => {
    // Validate source in create mode
    if (isCreateMode) {
      if (sourceMode === 'gpx' && !gpxFile) {
        setError(t('routes.create.validation.fileRequired'))
        return
      }
      if (sourceMode === 'planner' && plannerPoints.length < 2) {
        setError(t('routes.create.validation.pointsRequired'))
        return
      }
    }

    // Clear local errors
    setError(null)

    onSubmit(
      {
        ...values,
        points: sourceMode === 'planner' ? plannerPoints : undefined,
      },
      sourceMode === 'gpx' ? gpxFile || undefined : undefined
    )
  }

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-6">
        {error && (
          <div className="p-4 bg-red-50 border border-red-200 rounded-lg">
            <p className="text-red-700">{error}</p>
          </div>
        )}

        {/* Route Source - Mode selector (create mode or edit with single-track) */}
        {canUsePlanner && (
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              {t('routes.create.form.sourceMode')} {isCreateMode && '*'}
            </label>
            <div className="flex gap-2">
              <button
                type="button"
                onClick={() => setSourceMode('gpx')}
                className={`flex-1 py-2 px-4 text-sm font-medium rounded-lg border transition-colors ${
                  sourceMode === 'gpx'
                    ? 'bg-indigo-600 text-white border-indigo-600'
                    : 'bg-white text-gray-700 border-gray-300 hover:bg-gray-50'
                }`}
              >
                {t('routes.create.form.sourceModeGpx')}
              </button>
              <button
                type="button"
                onClick={() => setSourceMode('planner')}
                className={`flex-1 py-2 px-4 text-sm font-medium rounded-lg border transition-colors ${
                  sourceMode === 'planner'
                    ? 'bg-indigo-600 text-white border-indigo-600'
                    : 'bg-white text-gray-700 border-gray-300 hover:bg-gray-50'
                }`}
              >
                {t('routes.create.form.sourceModePlanner')}
              </button>
            </div>
          </div>
        )}

        {/* GPX File Upload (GPX mode or edit mode without planner) */}
        {(sourceMode === 'gpx' || !canUsePlanner) && (
          <div>
            <label htmlFor="gpxFile" className="block text-sm font-medium text-gray-700">
              {t('routes.create.form.gpxFile')} {isCreateMode && sourceMode === 'gpx' && '*'}
            </label>
            <div className="mt-1">
              <input
                id="gpxFile"
                name="gpxFile"
                type="file"
                accept=".gpx"
                onChange={handleFileChange}
                className="block w-full text-sm text-gray-500
                file:mr-4 file:py-2 file:px-4
                file:rounded-md file:border-0
                file:text-sm file:font-semibold
                file:bg-indigo-50 file:text-indigo-700
                hover:file:bg-indigo-100"
              />
            </div>
            <p className="mt-2 text-sm text-gray-500">{t('routes.create.form.gpxFileHint')}</p>
          </div>
        )}

        {/* Route Planner - full viewport width */}
        {canUsePlanner && sourceMode === 'planner' && (
          <div className="relative left-1/2 right-1/2 -ml-[50vw] -mr-[50vw] w-screen">
            <label className="block text-sm font-medium text-gray-700 mb-2 px-4 sm:px-6 lg:px-8">
              {t('routes.create.form.plannerLabel')} {isCreateMode && '*'}
            </label>
            <div className="h-[70vh] border-y border-gray-300 overflow-hidden">
              <EmbeddedRoutePlanner onPointsChange={setPlannerPoints} initialTrack={initialTrack} />
            </div>
            {plannerPoints.length > 0 && (
              <p className="mt-2 text-sm text-gray-500 px-4 sm:px-6 lg:px-8">
                {t('routes.create.form.pointCount', { count: plannerPoints.length })}
              </p>
            )}
          </div>
        )}

        {/* Name */}
        <FormField
          control={form.control}
          name="name"
          render={({ field }) => (
            <FormItem>
              <FormLabel>
                {t('routes.create.form.name')} <span className="text-destructive">*</span>
              </FormLabel>
              <FormControl>
                <Input placeholder={t('routes.create.form.name')} {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        {/* Slug Editor (only in edit mode) */}
        {currentSlug && onSlugChange && (
          <SlugEditor
            currentSlug={currentSlug}
            baseUrl={`/teams/${teamSlug}/routes/`}
            onSlugChange={onSlugChange}
            disabled={!canEditSlug}
          />
        )}

        {/* Description */}
        <FormField
          control={form.control}
          name="media"
          render={({ field }) => (
            <FormItem>
              <FormLabel>{t('form.description')}</FormLabel>
              <FormControl>
                <MediaEditor
                  value={field.value}
                  onChange={field.onChange}
                  placeholder={t('form.description')}
                  minHeight="150px"
                  maxHeight="300px"
                  disabled={isPending}
                  ariaLabel={t('form.description')}
                  teamSlug={teamSlug}
                />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        {/* Surface Type */}
        <FormField
          control={form.control}
          name="surfaceType"
          render={({ field }) => (
            <FormItem>
              <FormLabel>{t('routes.create.form.surfaceType')}</FormLabel>
              <Select value={field.value} onValueChange={field.onChange}>
                <FormControl>
                  <SelectTrigger className="w-full">
                    <SelectValue />
                  </SelectTrigger>
                </FormControl>
                <SelectContent>
                  <SelectItem value={SurfaceType.ROAD}>{t('routes.surfaceType.ROAD')}</SelectItem>
                  <SelectItem value={SurfaceType.GRAVEL}>
                    {t('routes.surfaceType.GRAVEL')}
                  </SelectItem>
                  <SelectItem value={SurfaceType.MTB}>{t('routes.surfaceType.MTB')}</SelectItem>
                  <SelectItem value={SurfaceType.MIXED}>{t('routes.surfaceType.MIXED')}</SelectItem>
                </SelectContent>
              </Select>
              <FormMessage />
            </FormItem>
          )}
        />

        {/* Visibility */}
        {team.visibility !== 'TEAM' && (
          <FormField
            control={form.control}
            name="visibility"
            render={({ field }) => (
              <FormItem>
                <FormLabel>{t('visibility.label')}</FormLabel>
                <FormControl>
                  <RadioGroup
                    value={field.value}
                    onValueChange={field.onChange}
                    className="space-y-2"
                  >
                    <div className="flex items-center space-x-2">
                      <RadioGroupItem value="TEAM" id="visibility-team" />
                      <Label htmlFor="visibility-team">{t('visibility.team')}</Label>
                    </div>
                    <div className="flex items-center space-x-2">
                      <RadioGroupItem value="PUBLIC" id="visibility-public" />
                      <Label htmlFor="visibility-public">{t('visibility.public')}</Label>
                    </div>
                  </RadioGroup>
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        )}

        {/* Submit Buttons */}
        <div className="flex justify-end gap-3">
          {showCancelButton && (
            <Button type="button" variant="outline" onClick={onCancel} disabled={isPending}>
              {cancelButtonText || t('actions.cancelAction')}
            </Button>
          )}
          <Button
            type="submit"
            disabled={
              isPending ||
              !form.formState.isValid ||
              (isCreateMode && sourceMode === 'gpx' && !gpxFile) ||
              (isCreateMode && sourceMode === 'planner' && plannerPoints.length < 2)
            }
          >
            {isPending ? (
              <>
                <LoadingSpinner size="sm" />
                {t('status.creating')}
              </>
            ) : (
              submitButtonText || t('routes.create.submit')
            )}
          </Button>
        </div>
      </form>
    </Form>
  )
}
