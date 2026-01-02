import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { PaperAirplaneIcon } from '@heroicons/react/24/solid'
import { LoadingSpinner } from '../common/LoadingSpinner'

interface CommentFormProps {
  onSubmit: (content: string) => void
  onCancel?: () => void
  isLoading: boolean
  placeholder?: string
  autoFocus?: boolean
}

export function CommentForm({
  onSubmit,
  onCancel,
  isLoading,
  placeholder,
  autoFocus = false,
}: CommentFormProps) {
  const { t } = useTranslation('comments')
  const [content, setContent] = useState('')

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    if (content.trim()) {
      onSubmit(content.trim())
      setContent('')
    }
  }

  return (
    <form onSubmit={handleSubmit} className="flex gap-2">
      <textarea
        value={content}
        onChange={(e) => setContent(e.target.value)}
        placeholder={placeholder || t('form.placeholder')}
        autoFocus={autoFocus}
        rows={2}
        className="flex-1 rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 text-sm resize-none"
        disabled={isLoading}
      />
      <div className="flex flex-col gap-1">
        <button
          type="submit"
          disabled={isLoading || !content.trim()}
          className="inline-flex items-center px-3 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {isLoading ? <LoadingSpinner size="sm" /> : <PaperAirplaneIcon className="w-4 h-4" />}
        </button>
        {onCancel && (
          <button
            type="button"
            onClick={onCancel}
            className="text-xs text-gray-500 hover:text-gray-700"
          >
            {t('form.cancel')}
          </button>
        )}
      </div>
    </form>
  )
}
