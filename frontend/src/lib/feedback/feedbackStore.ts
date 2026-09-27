import { create } from 'zustand'
import type { ClientErrorDto } from '@/api/dto'

interface FeedbackState {
  opened: boolean
  /** The error the report is about, when it was opened from an error screen. */
  error?: ClientErrorDto
  open: (error?: ClientErrorDto) => void
  close: () => void
}

/**
 * One feedback modal for the whole app, mounted outside the router and the root error boundary
 * (see AppFrame), so an error screen can still open it.
 */
export const useFeedbackStore = create<FeedbackState>()((set) => ({
  opened: false,
  error: undefined,
  open: (error) => set({ opened: true, error }),
  close: () => set({ opened: false, error: undefined }),
}))

export const openFeedback = (error?: ClientErrorDto) => useFeedbackStore.getState().open(error)
