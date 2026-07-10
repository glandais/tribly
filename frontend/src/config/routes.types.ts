import type { ComponentType, LazyExoticComponent } from 'react'
import type { Locale } from './paths'

/**
 * Authentication requirement for a route
 */
export type AuthRequirement = 'public' | 'authenticated' | 'unauthenticated'

/**
 * Parameter definitions for dynamic segments
 */
export interface RouteParams {
  teamSlug?: string
  rideSlug?: string
  postSlug?: string
  tripSlug?: string
  stageSlug?: string
  routeSlug?: string
  templateSlug?: string
  adSlug?: string
  pageSlug?: string
}

/**
 * Entity types that can provide dynamic breadcrumb labels
 */
export type EntityType =
  'team' | 'ride' | 'post' | 'trip' | 'stage' | 'route' | 'rideTemplate' | 'ad' | 'teamPage'

/**
 * Breadcrumb label configuration
 * - Static: i18n key string (e.g., "common:teams")
 * - Dynamic: object with entity type to fetch name from
 */
export type BreadcrumbLabel =
  { type: 'static'; i18nKey: string } | { type: 'dynamic'; entity: EntityType }

/**
 * A shared navigation group whose (runtime-gated) items feed a breadcrumb crumb's dropdown.
 * - 'home': the top-level sections (feed, teams, calendar, routes)
 * - 'team': the team sections (publications, calendar, routes, ads, about, dynamic pages)
 */
export type NavGroup = 'home' | 'team'

/**
 * Single route configuration entry
 */
export interface RouteConfig {
  /** Unique identifier for this route */
  id: string

  /** URL path patterns per locale (react-router format). */
  paths: Record<Locale, string>

  /** Page component to render */
  component: ComponentType | LazyExoticComponent<ComponentType>

  /** Authentication requirement */
  auth: AuthRequirement

  /**
   * Parent route ID for breadcrumb hierarchy
   * Explicit parent allows custom breadcrumb chains that don't follow URL structure
   * null = no parent (root level)
   */
  parentId: string | null

  /**
   * Which shared navigation group populates this crumb's dropdown. The dropdown then mirrors the
   * actual tab bar for that level — honoring auth, team feature flags, membership and dynamic pages
   * — instead of a static hand-authored list. Omit for crumbs with no sibling-level switcher.
   */
  navGroup?: NavGroup

  /**
   * Breadcrumb label for this route
   * undefined = route not shown in breadcrumbs
   */
  breadcrumb?: BreadcrumbLabel

  /**
   * Whether this route is an index route (renders at parent path)
   */
  index?: boolean

  /**
   * Whether to show a prominent back link to parent in breadcrumb
   * Use for create/edit pages that need clear navigation back
   */
  showBackLink?: boolean

  /**
   * Hide/redirect this route on a single-team site (domain flagged single-team, or a pinned alias
   * host). Team browsing and team creation are meaningless there. Used both to redirect the route
   * to home and to drop its breadcrumb crumb.
   */
  hideWhenSingleTeam?: boolean
}

/**
 * Complete route configuration
 */
export type RoutesConfig = RouteConfig[]
