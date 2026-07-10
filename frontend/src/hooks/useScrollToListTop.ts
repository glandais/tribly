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

/** The nearest ancestor that actually scrolls, which `scrollIntoView` is about to move. */
function scrollableAncestor(element: HTMLElement): HTMLElement | null {
  for (let node = element.parentElement; node; node = node.parentElement) {
    const { overflowY } = getComputedStyle(node)
    if (overflowY === 'auto' || overflowY === 'scroll') return node
  }
  return null
}

/**
 * Height of the container's own sticky header, which `block: 'start'` would otherwise leave sitting
 * on top of the first row. Measured rather than hard-coded: `Modal.Header` grows when its title
 * wraps onto a second line, which it does on a narrow screen.
 */
function stickyHeaderHeight(container: HTMLElement): number {
  let height = 0
  for (const child of container.children) {
    const style = getComputedStyle(child)
    if (style.position === 'sticky' && parseFloat(style.top) === 0) {
      height = Math.max(height, child.getBoundingClientRect().height)
    }
  }
  return height
}

/**
 * Same, for a list that lives inside a scroll container rather than the document — Mantine's
 * `Modal.content` carries the `overflow-y`, so `useScrollIntoView` would animate the window and do
 * nothing. The native call walks up to the nearest scrollable ancestor, so no one has to know which
 * element that is.
 *
 * `scroll-margin-top` is the only offset the native call honours, and it is what keeps the first row
 * clear of the sticky `Modal.Header` — the container top and the visible top are not the same line.
 */
export function useScrollToListTopWithinContainer() {
  const reduceMotion = useReducedMotion()
  const listTopRef = useRef<HTMLDivElement>(null)

  const scrollToListTop = useCallback(() => {
    const listTop = listTopRef.current
    if (!listTop) return

    const container = scrollableAncestor(listTop)
    listTop.style.scrollMarginTop = container ? `${stickyHeaderHeight(container)}px` : ''

    listTop.scrollIntoView({
      block: 'start',
      behavior: reduceMotion ? 'auto' : 'smooth',
    })
  }, [reduceMotion])

  return { listTopRef, scrollToListTop }
}
