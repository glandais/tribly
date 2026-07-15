import { useEffect, useState } from 'react'

/**
 * Toggles a "hidden" boolean each time the user presses the "h" key, letting map
 * views hide the GPX trace and its markers to reveal the underlying basemap.
 *
 * Ignores the key when a modifier is held or when focus is in an editable field
 * (input/textarea/select/contenteditable), so it never clashes with typing or
 * browser/OS shortcuts.
 */
export function useHideTrackKey(): boolean {
  const [hidden, setHidden] = useState(false)

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key !== 'h' && e.key !== 'H') return
      if (e.ctrlKey || e.metaKey || e.altKey) return
      const target = e.target as HTMLElement | null
      if (
        target &&
        (target.isContentEditable ||
          target.tagName === 'INPUT' ||
          target.tagName === 'TEXTAREA' ||
          target.tagName === 'SELECT')
      ) {
        return
      }
      setHidden((prev) => !prev)
    }
    window.addEventListener('keydown', handleKeyDown)
    return () => window.removeEventListener('keydown', handleKeyDown)
  }, [])

  return hidden
}
