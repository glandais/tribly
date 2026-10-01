import { useMemo } from 'react'
import { useTranslation } from 'react-i18next'
import { Group, MultiSelect, Text, type MultiSelectProps } from '@mantine/core'
import { IconTags } from '@tabler/icons-react'
import { useListTeamTags } from '@/api/endpoints/tags/tags'
import type { TagTarget } from '@/api/dto'
import { TagDot } from './TagList'

interface TagFilterProps {
  teamSlug: string
  /** The kind of content listed: each kind has its own set of tags (plan D3). */
  type: TagTarget
  /** Tag ids, as `?tags=` carries them. */
  value: string[] | undefined
  /** Receives `undefined` once nothing is selected, so the key leaves the URL. */
  onChange: (tagIds: string[] | undefined) => void
  style?: MultiSelectProps['style']
  w?: MultiSelectProps['w']
  mt?: MultiSelectProps['mt']
}

/**
 * Multi-selection over a team's tags of one kind, in OR (plan D6). Renders nothing while the
 * vocabulary loads and when the team has no tag of this kind: a filter with nothing to pick from
 * is noise.
 *
 * Its data comes from `useListTeamTags(teamSlug, { type })` — the same key a list page's `prefetch`
 * primes with `prefetchTeamTags`, so the control is in the server render.
 */
export function TagFilter({ teamSlug, type, value, onChange, style, w, mt }: TagFilterProps) {
  const { t } = useTranslation()
  // An optional control: if the vocabulary fails to load, the list still works without it — no
  // toast for a filter the reader never asked for.
  const { data: tags } = useListTeamTags(teamSlug, { type }, { request: { skipErrorToast: true } })

  const colorById = useMemo(
    () => new Map<string, string>((tags ?? []).map((tag) => [tag.id, tag.color])),
    [tags]
  )
  if (!tags || tags.length === 0) {
    return null
  }
  // An id the team no longer has (a deleted tag, a stale link) is ignored by the API (plan D18);
  // shown, it would be a pill with no label.
  const known = (value ?? []).filter((id) => colorById.has(id))

  return (
    <MultiSelect
      aria-label={t('tags.filter.label')}
      placeholder={known.length === 0 ? t('tags.filter.placeholder') : undefined}
      leftSection={<IconTags size={16} />}
      data={tags.map((tag) => ({ value: tag.id, label: tag.label }))}
      value={known}
      onChange={(ids) => onChange(ids.length > 0 ? ids : undefined)}
      renderOption={({ option }) => (
        <Group gap="xs" wrap="nowrap">
          <TagDot color={colorById.get(option.value) ?? 'gray'} />
          <Text size="sm">{option.label}</Text>
        </Group>
      )}
      searchable
      clearable
      hidePickedOptions
      nothingFoundMessage={t('tags.filter.nothingFound')}
      style={style}
      w={w}
      mt={mt}
    />
  )
}
