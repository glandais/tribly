import type {
  AdDto,
  AdminStatsDto,
  GpxPreviewDto,
  PlaceDetailDto,
  PlaceRequest,
  PostDto,
  RideDto,
  RideTemplateDto,
  RideTemplateRequest,
  RouteDto,
  TeamDetailDto,
  TeamPageDto,
  TripDto,
} from '../../src/api/dto'
import { newAd } from './ads'
import { apiGet, apiPost, expectOk, withApi, type AuthResponse } from './api'
import { addMember, markdownMedia, newTeam, newTeamPage, newUser, roleSession } from './data'
import { newPost } from './posts'
import { newRide } from './rides'
import { gpxOf, newRoute, newTrip, windingTrack } from './routes'

/**
 * The dataset of routes-render.e2e.ts: one real value for every `{param}` of contracts/routes.yaml
 * (see contract.ts), and a session for every role the route table names.
 */

export type RenderRole = 'anonymous' | 'member' | 'organizer' | 'teamAdmin' | 'platformAdmin'

export interface Dataset {
  team: TeamDetailDto
  sessions: Record<Exclude<RenderRole, 'anonymous'>, AuthResponse>
  ride: RideDto
  route: RouteDto
  trip: TripDto
  post: PostDto
  ad: AdDto
  page: TeamPageDto
  template: RideTemplateDto
  place: PlaceDetailDto
  preview: GpxPreviewDto
  /** Every `{param}` of the contract, filled from the entities above. */
  params: Record<string, string>
}

/**
 * A public team — so an anonymous visitor may read its public screens — owned by a user of its own,
 * with a plain member, an organizer and an admin, and one public entity of every kind a route
 * names. The platform admin adds the members (a team is born with addMemberAllowed=false) and is
 * not a member itself.
 */
export async function buildDataset(label: string): Promise<Dataset> {
  const platformAdmin = await roleSession('admin')
  const [owner, member, organizer, teamAdmin] = await Promise.all([
    newUser(`Fondatrice ${label}`),
    newUser(`Membre ${label}`),
    newUser(`Organisatrice ${label}`),
    newUser(`Admin équipe ${label}`),
  ])
  const team = await newTeam(owner, `Écrans ${label}`, { visibility: 'PUBLIC' })
  await addMember(platformAdmin, team.slug, member, 'MEMBER')
  await addMember(platformAdmin, team.slug, organizer, 'ORGANIZER')
  await addMember(platformAdmin, team.slug, teamAdmin, 'ADMIN')

  const slug = team.slug
  const track = windingTrack(120)
  const route = await newRoute(organizer, slug, `Parcours ${label}`, track, {
    visibility: 'PUBLIC',
  })
  const [ride, trip, publication, ad, page, template, place, preview] = await Promise.all([
    newRide(organizer, slug, `Sortie ${label}`, { visibility: 'PUBLIC' }),
    newTrip(
      organizer,
      slug,
      `Voyage ${label}`,
      [{ name: `Étape ${label}`, routeSlug: route.slug }],
      { visibility: 'PUBLIC' }
    ),
    newPost(organizer, slug, `Article ${label}`, {
      media: markdownMedia('Un article pour vérifier les écrans.'),
      visibility: 'PUBLIC',
    }),
    newAd(member, slug, { name: `Annonce ${label}`, body: 'Un vélo à vendre.', price: 100 }),
    newTeamPage(teamAdmin, slug, `Page ${label}`, 'Une page pour vérifier les écrans.', {
      visibility: 'PUBLIC',
    }),
    apiPost<RideTemplateDto>(teamAdmin, `/api/teams/${slug}/ride-templates`, {
      name: `Modèle ${label}`,
      markdown: '',
      visibility: 'TEAM',
      status: 'PUBLISHED',
      groups: [{ name: 'Groupe A' }],
    } satisfies RideTemplateRequest),
    apiPost<PlaceDetailDto>(teamAdmin, `/api/teams/${slug}/places`, {
      name: `Lieu ${label}`,
      address: '1 place des Halles, Chartres',
      startPlace: true,
      endPlace: true,
      geometry: { type: 'Point', coordinates: [1.4875, 48.4469] },
    } satisfies PlaceRequest),
    uploadPreview(member, `Trace ${label}`, gpxOf(`Trace ${label}`, track)),
  ])

  return {
    team,
    sessions: { member, organizer, teamAdmin, platformAdmin },
    ride,
    route,
    trip,
    post: publication,
    ad,
    page,
    template,
    place,
    preview,
    params: {
      teamSlug: slug,
      rideSlug: ride.slug,
      routeSlug: route.slug,
      tripSlug: trip.slug,
      stageSlug: trip.stages[0].slug,
      postSlug: publication.slug,
      adSlug: ad.slug,
      pageSlug: page.slug,
      templateSlug: template.slug,
      previewId: preview.id,
    },
  }
}

/** A GPX preview (the « Outils GPX » upload), owned by `as`. */
const uploadPreview = (as: AuthResponse, name: string, gpx: string) =>
  withApi(as, async (api) =>
    expectOk<GpxPreviewDto>(
      await api.post('/api/gpx-previews', {
        multipart: {
          gpxFile: {
            name: `${name}.gpx`,
            mimeType: 'application/gpx+xml',
            buffer: Buffer.from(gpx),
          },
        },
      })
    )
  )

/** The platform's totals, as the platform admin dashboard shows them. */
export const platformStats = async () =>
  apiGet<AdminStatsDto>(await roleSession('admin'), '/api/admin/domains/stats')
