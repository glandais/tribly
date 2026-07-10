import { useCallback, useRef } from 'react'
import { useReducedMotion, useScrollIntoView } from '@mantine/hooks'

/** Clears the sticky AppShell header (56–60px) with a little breathing room. */
const HEADER_OFFSET = 72

/** Mantine defaults to 1250ms, which feels like the page is stuck. */
const DURATION = 300

/**
 * Brings the first item of a paginated list back into view when the page changes.
 *
 * Call `scrollToListTop()` from the page-change handler, never from an effect keyed on the page
 * number: such an effect also fires on mount, and on a back-navigation it would run right after
 * `useScrollRestoration` restored the position, throwing it away.
 */
export function useScrollToListTop() {
  const { targetRef, scrollIntoView } = useScrollIntoView<HTMLDivElement>({
    offset: HEADER_OFFSET,
    duration: DURATION,
    cancelable: true,
  })

  const scrollToListTop = useCallback(
    () => scrollIntoView({ alignment: 'start' }),
    [scrollIntoView]
  )

  return { listTopRef: targetRef, scrollToListTop }
}

/**
 * Same, for a list that lives inside a scroll container rather than the document — Mantine's
 * `Modal.content` carries the `overflow-y`, so `useScrollIntoView` would animate the window and do
 * nothing. The native call walks up to the nearest scrollable ancestor, so no one has to know which
 * element that is.
 */
export function useScrollToListTopWithinContainer() {
  const reduceMotion = useReducedMotion()
  const listTopRef = useRef<HTMLDivElement>(null)

  const scrollToListTop = useCallback(() => {
    listTopRef.current?.scrollIntoView({
      block: 'start',
      behavior: reduceMotion ? 'auto' : 'smooth',
    })
  }, [reduceMotion])

  return { listTopRef, scrollToListTop }
}
