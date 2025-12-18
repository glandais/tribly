import { useTranslation } from 'react-i18next'

interface ConfirmDialogProps {
  isOpen: boolean
  onClose: () => void
  onConfirm: () => void
  title: string
  message: string
  confirmText?: string
  cancelText?: string
  variant?: 'danger' | 'warning' | 'info'
  isLoading?: boolean
}

export function ConfirmDialog({
  isOpen,
  onClose,
  onConfirm,
  title,
  message,
  confirmText,
  cancelText,
  variant = 'warning',
  isLoading = false,
}: ConfirmDialogProps) {
  const { t } = useTranslation('common')

  if (!isOpen) return null

  const variantStyles = {
    danger: {
      bg: 'bg-red-50',
      border: 'border-red-200',
      text: 'text-red-900',
      messageText: 'text-red-700',
      button: 'bg-red-600 hover:bg-red-700 focus:ring-red-500 text-white disabled:bg-red-300',
    },
    warning: {
      bg: 'bg-yellow-50',
      border: 'border-yellow-200',
      text: 'text-yellow-900',
      messageText: 'text-yellow-700',
      button:
        'bg-yellow-600 hover:bg-yellow-700 focus:ring-yellow-500 text-white disabled:bg-yellow-300',
    },
    info: {
      bg: 'bg-blue-50',
      border: 'border-blue-200',
      text: 'text-blue-900',
      messageText: 'text-blue-700',
      button: 'bg-blue-600 hover:bg-blue-700 focus:ring-blue-500 text-white disabled:bg-blue-300',
    },
  }

  const styles = variantStyles[variant]

  return (
    <div className="fixed inset-0 z-[9999] overflow-y-auto">
      <div className="flex min-h-screen items-center justify-center p-4">
        {/* Backdrop */}
        <div
          className="fixed inset-0 bg-black bg-opacity-30 transition-opacity"
          onClick={onClose}
        />

        {/* Dialog */}
        <div
          className={`relative w-full max-w-md transform rounded-lg ${styles.bg} ${styles.border} border p-6 shadow-xl transition-all z-[10000]`}
        >
          <h3 className={`text-lg font-semibold ${styles.text} mb-3`}>{title}</h3>
          <p className={`text-sm ${styles.messageText} mb-6`}>{message}</p>

          <div className="flex items-center justify-end gap-3">
            <button
              onClick={onClose}
              disabled={isLoading}
              className="px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-500 disabled:opacity-50"
            >
              {cancelText || t('buttons.cancel')}
            </button>
            <button
              onClick={onConfirm}
              disabled={isLoading}
              className={`px-4 py-2 rounded-md shadow-sm text-sm font-medium focus:outline-none focus:ring-2 focus:ring-offset-2 ${styles.button}`}
            >
              {isLoading ? t('buttons.loading') : confirmText || t('buttons.confirm')}
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}
