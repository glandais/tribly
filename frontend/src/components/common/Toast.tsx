import { useTranslation } from 'react-i18next'
import { useNotificationStore, type Notification } from '../../store/notificationStore'

const ICON_MAP = {
  success: '✓',
  error: '✕',
  warning: '⚠',
  info: 'ℹ',
}

const STYLE_MAP = {
  success: 'bg-green-50 text-green-800 border-green-200',
  error: 'bg-red-50 text-red-800 border-red-200',
  warning: 'bg-yellow-50 text-yellow-800 border-yellow-200',
  info: 'bg-blue-50 text-blue-800 border-blue-200',
}

interface ToastItemProps {
  notification: Notification
}

function ToastItem({ notification }: ToastItemProps) {
  const { t } = useTranslation('errors')
  const removeNotification = useNotificationStore((state) => state.removeNotification)

  const message = notification.translationKey
    ? t(notification.translationKey, notification.translationParams)
    : notification.message

  return (
    <div
      className={`flex items-center gap-3 px-4 py-3 rounded-lg border shadow-lg ${STYLE_MAP[notification.type]}`}
      role="alert"
    >
      <span className="text-lg font-bold">{ICON_MAP[notification.type]}</span>
      <p className="flex-1 text-sm font-medium">{message}</p>
      <button
        onClick={() => removeNotification(notification.id)}
        className="text-current opacity-70 hover:opacity-100"
        aria-label="Dismiss"
      >
        ✕
      </button>
    </div>
  )
}

export function ToastContainer() {
  const notifications = useNotificationStore((state) => state.notifications)

  if (notifications.length === 0) return null

  return (
    <div
      className="fixed top-4 right-4 z-50 flex flex-col gap-2 max-w-md"
      aria-live="polite"
      aria-atomic="true"
    >
      {notifications.map((notification) => (
        <ToastItem key={notification.id} notification={notification} />
      ))}
    </div>
  )
}
