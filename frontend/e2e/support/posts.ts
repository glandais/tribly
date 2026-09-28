import type {
  CommentDto,
  CommentListResponse,
  CommentRequest,
  PostDto,
  PostRequest,
} from '../../src/api/dto'
import { apiGet, apiGetOrNull, apiPost, type AuthResponse } from './api'
import { markdownMedia } from './data'

/** Publications (posts) seeded and read back through the REST API. */

/** A published, members-only post dated now, unless `overrides` say otherwise. */
export const newPost = (
  who: AuthResponse,
  teamSlug: string,
  name: string,
  overrides: Partial<PostRequest> = {}
) =>
  apiPost<PostDto>(who, `/api/teams/${teamSlug}/posts`, {
    name,
    media: markdownMedia(),
    dateTime: new Date().toISOString(),
    visibility: 'TEAM',
    status: 'PUBLISHED',
    ...overrides,
  } satisfies PostRequest)

const postApiPath = (teamSlug: string, postSlug: string) =>
  `/api/teams/${teamSlug}/posts/${postSlug}`

/** The post as `who` reads it through the API. */
export const fetchPost = (who: AuthResponse, teamSlug: string, postSlug: string) =>
  apiGet<PostDto>(who, postApiPath(teamSlug, postSlug))

/** The post as `who` reads it, or null when the API answers 404 (deleted, or never there). */
export const findPost = (who: AuthResponse, teamSlug: string, postSlug: string) =>
  apiGetOrNull<PostDto>(who, postApiPath(teamSlug, postSlug))

/** The post's page. */
export const postPath = (teamSlug: string, postSlug: string) =>
  `/equipes/${teamSlug}/articles/${postSlug}`

/** A comment on a post, as `who` — a reply when `parentId` is given. */
export const commentOnPost = (
  who: AuthResponse,
  teamSlug: string,
  postSlug: string,
  content: string,
  parentId?: string
) =>
  apiPost<CommentDto>(who, `${postApiPath(teamSlug, postSlug)}/comments`, {
    content,
    parentId,
  } satisfies CommentRequest)

/** The post's comments as `who` reads them (a blocker does not get the blocked author's). */
export const readPostComments = (who: AuthResponse, teamSlug: string, postSlug: string) =>
  apiGet<CommentListResponse>(who, `${postApiPath(teamSlug, postSlug)}/comments`)
