import { useState, useCallback } from 'react'
import { useTranslation } from 'react-i18next'
import { PencilIcon, CheckIcon, XMarkIcon } from '@heroicons/react/24/outline'
import { toast } from 'sonner'

interface SlugEditorProps {
  currentSlug: string
  baseUrl: string
  onSlugChange: (newSlug: string) => Promise<void>
  disabled?: boolean
}

const SLUG_PATTERN = /^[a-z0-9]+(-[a-z0-9]+)*$/

export function SlugEditor({
  currentSlug,
  baseUrl,
  onSlugChange,
  disabled = false,
}: SlugEditorProps) {
  const { t } = useTranslation()
  const [isEditing, setIsEditing] = useState(false)
  const [newSlug, setNewSlug] = useState(currentSlug)
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const validateSlug = useCallback(
    (slug: string): string | null => {
      if (!slug || slug.length === 0) {
        return t('slug.error.empty')
      }
      if (slug.length > 200) {
        return t('slug.error.tooLong')
      }
      if (!SLUG_PATTERN.test(slug)) {
        return t('slug.error.invalid')
      }
      return null
    },
    [t]
  )

  const handleStartEdit = () => {
    setNewSlug(currentSlug)
    setError(null)
    setIsEditing(true)
  }

  const handleCancel = () => {
    setNewSlug(currentSlug)
    setError(null)
    setIsEditing(false)
  }

  const handleSave = async () => {
    const validationError = validateSlug(newSlug)
    if (validationError) {
      setError(validationError)
      return
    }

    if (newSlug === currentSlug) {
      setIsEditing(false)
      return
    }

    setIsLoading(true)
    setError(null)

    try {
      await onSlugChange(newSlug)
      toast.success(t('slug.success'))
      setIsEditing(false)
    } catch (err) {
      // Error is handled by the parent component or apiClient
      // Check for conflict error
      if (err && typeof err === 'object' && 'status' in err && err.status === 409) {
        setError(t('slug.error.taken'))
      }
    } finally {
      setIsLoading(false)
    }
  }

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value.toLowerCase().replace(/[^a-z0-9-]/g, '')
    setNewSlug(value)
    setError(null)
  }

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      e.preventDefault()
      handleSave()
    } else if (e.key === 'Escape') {
      handleCancel()
    }
  }

  if (!isEditing) {
    return (
      <div className="flex items-center gap-2 text-sm text-gray-600">
        <span className="font-medium">{t('slug.label')}:</span>
        <span className="font-mono text-gray-800">{baseUrl + currentSlug}</span>
        {!disabled && (
          <button
            type="button"
            onClick={handleStartEdit}
            className="rounded p-1 text-gray-400 hover:bg-gray-100 hover:text-gray-600"
            title={t('slug.edit')}
          >
            <PencilIcon className="h-4 w-4" />
          </button>
        )}
      </div>
    )
  }

  return (
    <div className="space-y-2">
      <div className="flex items-center gap-2">
        <span className="text-sm font-medium text-gray-600">{t('slug.label')}:</span>
        <div className="flex flex-1 items-center gap-1">
          <span className="text-sm text-gray-500">{baseUrl}</span>
          <input
            type="text"
            value={newSlug}
            onChange={handleInputChange}
            onKeyDown={handleKeyDown}
            disabled={isLoading}
            className="w-48 rounded border border-gray-300 px-2 py-1 font-mono text-sm focus:border-blue-500 focus:ring-1 focus:ring-blue-500 disabled:bg-gray-100"
            autoFocus
          />
          <button
            type="button"
            onClick={handleSave}
            disabled={isLoading}
            className="rounded p-1 text-green-600 hover:bg-green-50 disabled:opacity-50"
            title={t('actions.save')}
          >
            <CheckIcon className="h-4 w-4" />
          </button>
          <button
            type="button"
            onClick={handleCancel}
            disabled={isLoading}
            className="rounded p-1 text-gray-400 hover:bg-gray-100 hover:text-gray-600 disabled:opacity-50"
            title={t('actions.cancelAction')}
          >
            <XMarkIcon className="h-4 w-4" />
          </button>
        </div>
      </div>
      {error && <p className="text-sm text-red-600">{error}</p>}
      <p className="text-xs text-gray-500">{t('slug.hint')}</p>
    </div>
  )
}
