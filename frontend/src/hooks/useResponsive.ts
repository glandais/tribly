import { useMantineTheme, MantineSpacing, StyleProp } from '@mantine/core'
import { useMediaQuery } from '@mantine/hooks'

/**
 * Hook for responsive breakpoint detection.
 * Uses mobile-first approach: defaults to mobile, scales up for larger screens.
 */
export function useResponsive() {
  const theme = useMantineTheme()

  // Mobile-first: default to true for mobile on SSR
  const isMobile = useMediaQuery(`(max-width: ${theme.breakpoints.sm})`, true, {
    getInitialValueInEffect: false,
  })

  const isTablet = useMediaQuery(
    `(min-width: ${theme.breakpoints.sm}) and (max-width: ${theme.breakpoints.md})`,
    false,
    { getInitialValueInEffect: false }
  )

  const isDesktop = useMediaQuery(`(min-width: ${theme.breakpoints.md})`, false, {
    getInitialValueInEffect: false,
  })

  return { isMobile, isTablet, isDesktop }
}

// Type for responsive spacing props
type ResponsiveSpacing = StyleProp<MantineSpacing>

/**
 * Responsive spacing values for consistent mobile-first design.
 * Usage: gap={responsiveSpacing.section}
 */
export const responsiveSpacing: Record<string, ResponsiveSpacing> = {
  /** Page-level padding/margins */
  page: { base: 'sm', sm: 'md', lg: 'lg' },
  /** Section spacing within pages */
  section: { base: 'md', sm: 'lg' },
  /** Card internal padding */
  card: { base: 'sm', sm: 'md' },
  /** Form element spacing */
  form: { base: 'md', sm: 'lg' },
}

/**
 * Responsive map/chart heights.
 * Usage: h={responsiveMapHeight.full}
 */
export const responsiveMapHeight: Record<string, StyleProp<number>> = {
  /** Compact map (e.g., in cards) */
  compact: { base: 200, sm: 280, md: 350 },
  /** Standard map view */
  standard: { base: 280, sm: 380, md: 450 },
  /** Full map (detail pages) */
  full: { base: 300, sm: 400, md: 500 },
}
