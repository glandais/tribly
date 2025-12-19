import { useNotificationStore, type NotificationType } from '../store/notificationStore'

export function useNotification() {
  const addNotification = useNotificationStore((state) => state.addNotification)

  const notify = (message: string, type: NotificationType = 'info', duration: number = 5000) => {
    addNotification({ message, type, duration })
  }

  const notifyError = (message: string) => {
    addNotification({ message, type: 'error', duration: 7000 })
  }

  const notifySuccess = (message: string, duration: number = 4000) => {
    addNotification({ message, type: 'success', duration })
  }

  const notifyWarning = (message: string) => {
    addNotification({ message, type: 'warning', duration: 6000 })
  }

  const notifyWithTranslation = (
    translationKey: string,
    type: NotificationType = 'info',
    translationParams?: Record<string, unknown>,
    duration: number = 5000
  ) => {
    addNotification({ message: '', type, translationKey, translationParams, duration })
  }

  return {
    notify,
    notifyError,
    notifySuccess,
    notifyWarning,
    notifyWithTranslation,
  }
}
