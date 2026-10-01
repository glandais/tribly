import * as zod from 'zod'

/**
 * The team's tags, sorted by label, each with its usage count. Open to whoever can see the team.
 * @summary List team tags
 */
export const ListTeamTagsParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
})

export const ListTeamTagsQueryParams = zod.object({
  type: zod
    .enum(['RIDE', 'POST', 'TRIP', 'ROUTE', 'AD'])
    .optional()
    .describe('Only the tags of this kind of content; all kinds when absent'),
})

export const ListTeamTagsResponseItem = zod
  .object({
    id: zod.string().describe('Tag ID (TSID)'),
    label: zod.string().describe('Label, at most 32 characters'),
    color: zod
      .enum(['INDIGO', 'BLUE', 'GREEN', 'RED', 'YELLOW', 'ORANGE', 'GRAPE', 'TEAL', 'GRAY'])
      .describe('Colour family'),
    type: zod
      .enum(['RIDE', 'POST', 'TRIP', 'ROUTE', 'AD'])
      .describe('Kind of content the tag applies to'),
    usageCount: zod
      .int()
      .describe(
        'Contents carrying the tag: rides, posts, trips, routes and ads out of the trash, plus ride templates — what a deletion would detach it from'
      ),
  })
  .describe('A team tag with its usage count')
export const ListTeamTagsResponse = zod.array(ListTeamTagsResponseItem)

/**
 * Requires team admin permissions.
 * @summary Create a team tag
 */
export const CreateTeamTagParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
})

export const createTeamTagBodyLabelMax = 255

export const CreateTeamTagBody = zod
  .object({
    type: zod
      .enum(['RIDE', 'POST', 'TRIP', 'ROUTE', 'AD'])
      .describe('Kind of content the tag applies to'),
    label: zod
      .string()
      .max(createTeamTagBodyLabelMax)
      .describe(
        'Label, trimmed; unique in the team and kind whatever the case, at most 32 characters once trimmed (TAG_LABEL_INVALID otherwise)'
      ),
    color: zod
      .enum(['INDIGO', 'BLUE', 'GREEN', 'RED', 'YELLOW', 'ORANGE', 'GRAPE', 'TEAL', 'GRAY'])
      .describe('Colour family'),
  })
  .describe('Tag creation request')

export const CreateTeamTagResponse = zod
  .object({
    id: zod.string().describe('Tag ID (TSID)'),
    label: zod.string().describe('Label, at most 32 characters'),
    color: zod
      .enum(['INDIGO', 'BLUE', 'GREEN', 'RED', 'YELLOW', 'ORANGE', 'GRAPE', 'TEAL', 'GRAY'])
      .describe('Colour family'),
    type: zod
      .enum(['RIDE', 'POST', 'TRIP', 'ROUTE', 'AD'])
      .describe('Kind of content the tag applies to'),
    usageCount: zod
      .int()
      .describe(
        'Contents carrying the tag: rides, posts, trips, routes and ads out of the trash, plus ride templates — what a deletion would detach it from'
      ),
  })
  .describe('A team tag with its usage count')

/**
 * Absent fields are unchanged; the kind never changes. Requires team admin.
 * @summary Rename or recolour a team tag
 */
export const UpdateTeamTagParams = zod.object({
  tagId: zod.string().describe('Tag ID (TSID)'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const updateTeamTagBodyLabelMax = 255

export const UpdateTeamTagBody = zod
  .object({
    label: zod
      .string()
      .max(updateTeamTagBodyLabelMax)
      .optional()
      .describe(
        'New label, trimmed, at most 32 characters once trimmed (TAG_LABEL_INVALID otherwise)'
      ),
    color: zod
      .enum(['INDIGO', 'BLUE', 'GREEN', 'RED', 'YELLOW', 'ORANGE', 'GRAPE', 'TEAL', 'GRAY'])
      .optional()
      .describe('New colour family'),
  })
  .describe('Tag update request — absent fields are unchanged')

export const UpdateTeamTagResponse = zod
  .object({
    id: zod.string().describe('Tag ID (TSID)'),
    label: zod.string().describe('Label, at most 32 characters'),
    color: zod
      .enum(['INDIGO', 'BLUE', 'GREEN', 'RED', 'YELLOW', 'ORANGE', 'GRAPE', 'TEAL', 'GRAY'])
      .describe('Colour family'),
    type: zod
      .enum(['RIDE', 'POST', 'TRIP', 'ROUTE', 'AD'])
      .describe('Kind of content the tag applies to'),
    usageCount: zod
      .int()
      .describe(
        'Contents carrying the tag: rides, posts, trips, routes and ads out of the trash, plus ride templates — what a deletion would detach it from'
      ),
  })
  .describe('A team tag with its usage count')

/**
 * Detaches the tag from every content and deletes it for good. Requires team admin.
 * @summary Delete a team tag
 */
export const DeleteTeamTagParams = zod.object({
  tagId: zod.string().describe('Tag ID (TSID)'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const DeleteTeamTagResponse = zod
  .object({
    detachedCount: zod
      .int()
      .describe(
        'Contents the tag was detached from, counted like usageCount (trashed contents lose it too, uncounted)'
      ),
  })
  .describe('Result of a tag deletion')
