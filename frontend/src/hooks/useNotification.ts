import { useNotificationStore, type NotificationType } from '../store/notificationStore'

export function useNotification() {
  const addNotification = useNotificationStore((state) => state.addNotification)

  const notify = (
    type: NotificationType = 'info',
    translatedMessage: string,
    duration: number = 5000
  ) => {
    addNotification({ type, translatedMessage, duration })
  }

  return {
    notify,
  }
}
