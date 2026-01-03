import { useState, useRef } from 'react'
import { useTranslation } from 'react-i18next'
import { PaperClipIcon, XMarkIcon, PlusIcon, PhotoIcon } from '@heroicons/react/24/outline'
import { MediaDto, AssetDto } from '../../api/api'
import { MarkdownEditor } from './MarkdownEditor'
import { assetsApi, unwrapResponse } from '../../lib/apiClient'
import { LoadingSpinner } from './LoadingSpinner'

export interface MediaEditorProps {
  value: MediaDto
  onChange: (value: MediaDto) => void
  placeholder?: string
  minHeight?: string
  maxHeight?: string
  disabled?: boolean
  ariaLabel?: string
  teamSlug?: string // Required for file uploads - when missing, attachments are disabled
}

/**
 * MediaEditor component - editable media content component.
 * Handles MediaDto and extracts/updates markdown for MarkdownEditor.
 * Supports logo and file attachments when teamSlug is provided.
 */
export function MediaEditor({
  value,
  onChange,
  placeholder,
  minHeight,
  maxHeight,
  disabled,
  ariaLabel,
  teamSlug,
}: MediaEditorProps) {
  const { t } = useTranslation('common')
  const { t: tCommon } = useTranslation('common')
  const fileInputRef = useRef<HTMLInputElement>(null)
  const logoInputRef = useRef<HTMLInputElement>(null)
  const [uploading, setUploading] = useState(false)
  const [uploadError, setUploadError] = useState<string | null>(null)
  const [logoUploading, setLogoUploading] = useState(false)
  const [logoError, setLogoError] = useState<string | null>(null)
  const [imageUploading, setImageUploading] = useState(false)
  const [imageError, setImageError] = useState<string | null>(null)

  const handleMarkdownChange = (markdown: string) => {
    onChange({
      ...value,
      markdown,
    })
  }

  // Handle image upload from markdown editor toolbar
  const handleImageUpload = async (
    file: File
  ): Promise<{ id: string; fileName: string } | null> => {
    if (!teamSlug) return null

    setImageUploading(true)
    setImageError(null)

    try {
      const asset = await unwrapResponse(assetsApi.uploadAsset(teamSlug, file))

      // Add to images array
      onChange({
        ...value,
        assets: {
          ...value.assets,
          images: [...value.assets.images, asset],
        },
      })

      return { id: asset.id, fileName: asset.fileName }
    } catch {
      setImageError(t('error.loading'))
      return null
    } finally {
      setImageUploading(false)
    }
  }

  const handleLogoSelect = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (!file || !teamSlug) return

    setLogoUploading(true)
    setLogoError(null)

    try {
      const asset = await unwrapResponse(assetsApi.uploadAsset(teamSlug, file))

      onChange({
        ...value,
        assets: {
          ...value.assets,
          logo: asset,
        },
      })
    } catch {
      setLogoError(t('error.loading'))
    } finally {
      setLogoUploading(false)
      if (logoInputRef.current) {
        logoInputRef.current.value = ''
      }
    }
  }

  const handleRemoveLogo = () => {
    onChange({
      ...value,
      assets: {
        ...value.assets,
        logo: undefined,
      },
    })
  }

  const handleFileSelect = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (!file || !teamSlug) return

    setUploading(true)
    setUploadError(null)

    try {
      const asset = await unwrapResponse(assetsApi.uploadAsset(teamSlug, file))

      // Add the new attachment to the assets
      onChange({
        ...value,
        assets: {
          ...value.assets,
          attachments: [...value.assets.attachments, asset],
        },
      })
    } catch {
      setUploadError(t('error.loading'))
    } finally {
      setUploading(false)
      // Reset the file input so the same file can be selected again
      if (fileInputRef.current) {
        fileInputRef.current.value = ''
      }
    }
  }

  const handleRemoveAttachment = (attachmentId: string) => {
    onChange({
      ...value,
      assets: {
        ...value.assets,
        attachments: value.assets.attachments.filter((a: AssetDto) => a.id !== attachmentId),
      },
    })
  }

  const attachments = value.assets.attachments
  const logo = value.assets.logo
  const canUpload = !!teamSlug && !disabled

  return (
    <div className="space-y-3">
      {/* Logo section - only show when teamSlug is available */}
      {teamSlug && (
        <div className="border border-gray-200 rounded-lg p-3 bg-gray-50">
          <div className="flex items-center justify-between mb-2">
            <h4 className="text-sm font-medium text-gray-700 flex items-center gap-1">
              <PhotoIcon className="h-4 w-4" />
              {t('logo.title')}
            </h4>
          </div>

          <div className="flex items-center gap-4">
            {/* Logo preview */}
            {logo?.url ? (
              <div className="relative">
                <img
                  src={logo.url}
                  alt={t('logo.title')}
                  className="h-16 w-16 object-cover rounded-lg border border-gray-200"
                />
              </div>
            ) : (
              <div className="h-16 w-16 rounded-lg border-2 border-dashed border-gray-300 flex items-center justify-center bg-white">
                <PhotoIcon className="h-8 w-8 text-gray-300" />
              </div>
            )}

            {/* Logo actions */}
            <div className="flex items-center gap-2">
              <input
                ref={logoInputRef}
                type="file"
                accept="image/*"
                onChange={handleLogoSelect}
                disabled={!canUpload || logoUploading}
                className="hidden"
              />
              <button
                type="button"
                onClick={() => logoInputRef.current?.click()}
                disabled={!canUpload || logoUploading}
                className="inline-flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {logoUploading ? (
                  <>
                    <LoadingSpinner size="sm" />
                    {tCommon('loading')}
                  </>
                ) : logo ? (
                  t('logo.change')
                ) : (
                  <>
                    <PlusIcon className="h-4 w-4" />
                    {t('logo.upload')}
                  </>
                )}
              </button>
              {logo && (
                <button
                  type="button"
                  onClick={handleRemoveLogo}
                  disabled={disabled}
                  className="inline-flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-red-600 bg-white border border-gray-300 rounded-md hover:bg-red-50 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  <XMarkIcon className="h-4 w-4" />
                  {t('logo.remove')}
                </button>
              )}
            </div>
          </div>

          {/* Logo upload error */}
          {logoError && <p className="mt-2 text-sm text-red-600">{logoError}</p>}
        </div>
      )}

      <MarkdownEditor
        value={value.markdown || ''}
        onChange={handleMarkdownChange}
        placeholder={placeholder}
        minHeight={minHeight}
        maxHeight={maxHeight}
        disabled={disabled}
        ariaLabel={ariaLabel}
        onImageUpload={teamSlug ? handleImageUpload : undefined}
        isUploadingImage={imageUploading}
        images={value.assets.images}
      />
      {imageError && <p className="text-sm text-red-600">{imageError}</p>}

      {/* Attachments section - only show when teamSlug is available */}
      {teamSlug && (
        <div className="border border-gray-200 rounded-lg p-3 bg-gray-50">
          <div className="flex items-center justify-between mb-2">
            <h4 className="text-sm font-medium text-gray-700 flex items-center gap-1">
              <PaperClipIcon className="h-4 w-4" />
              {t('attachments.title')}
            </h4>
          </div>

          {/* Existing attachments */}
          {attachments.length > 0 && (
            <ul className="space-y-1 mb-3">
              {attachments.map((attachment: AssetDto) => (
                <li
                  key={attachment.id}
                  className="flex items-center justify-between bg-white rounded px-2 py-1 text-sm"
                >
                  <span className="flex items-center gap-2 text-gray-700 truncate">
                    <PaperClipIcon className="h-4 w-4 text-gray-400 shrink-0" />
                    <span className="truncate">{attachment.fileName}</span>
                  </span>
                  <button
                    type="button"
                    onClick={() => handleRemoveAttachment(attachment.id)}
                    disabled={disabled}
                    className="text-gray-400 hover:text-red-500 disabled:opacity-50 disabled:cursor-not-allowed p-1"
                    title={t('attachments.remove')}
                  >
                    <XMarkIcon className="h-4 w-4" />
                  </button>
                </li>
              ))}
            </ul>
          )}

          {/* Upload button */}
          <div className="flex items-center gap-2">
            <input
              ref={fileInputRef}
              type="file"
              onChange={handleFileSelect}
              disabled={!canUpload || uploading}
              className="hidden"
            />
            <button
              type="button"
              onClick={() => fileInputRef.current?.click()}
              disabled={!canUpload || uploading}
              className="inline-flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {uploading ? (
                <>
                  <LoadingSpinner size="sm" />
                  {tCommon('loading')}
                </>
              ) : (
                <>
                  <PlusIcon className="h-4 w-4" />
                  {t('attachments.add')}
                </>
              )}
            </button>
          </div>

          {/* Upload error */}
          {uploadError && <p className="mt-2 text-sm text-red-600">{uploadError}</p>}
        </div>
      )}
    </div>
  )
}
