import { useTranslation } from 'react-i18next'
import { Badge, Box, Group, Tooltip, type MantineSize, type MantineSpacing } from '@mantine/core'
import type { TagDto } from '@/api/dto'
import { tagFamily } from './tagFamily'

/** A tag colour's dot alone — in a select's options, where the label is the option's own text. */
export function TagDot({ color, size = 8 }: { color: string; size?: number }) {
  return (
    <Box
      w={size}
      h={size}
      style={{
        borderRadius: '50%',
        flexShrink: 0,
        backgroundColor: `var(--mantine-color-${tagFamily(color)}-6)`,
      }}
    />
  )
}

interface TagChipProps {
  tag: Pick<TagDto, 'label' | 'color'>
  size?: MantineSize
}

/**
 * One team tag: a coloured dot and a label in neutral text, in its original case.
 *
 * Deliberately not the business badges' look (light fill, coloured upper-case text — `TypeBadge`,
 * `StatusBadge`…): a green tag must not read « Publié » (ledger `WEB-40`, plan D10). The colour is
 * the tag's family, rendered by Mantine's own `dot` variant.
 */
export function TagChip({ tag, size = 'sm' }: TagChipProps) {
  return (
    <Badge variant="dot" color={tagFamily(tag.color)} size={size} tt="none" fw={500}>
      {tag.label}
    </Badge>
  )
}

interface TagListProps {
  /** As the API gives them: already sorted by label. */
  tags: TagDto[] | undefined
  /**
   * Shows the first `max` tags and a « +n » chip for the rest — a card's mode. Left out, every tag
   * is shown — a detail page's.
   */
  max?: number
  size?: MantineSize
  /** Spacing around the list — none is drawn, margins included, when there is no tag. */
  mt?: MantineSpacing
  mb?: MantineSpacing
}

/** A content's tags, or nothing at all when it has none. */
export function TagList({ tags, max, size = 'sm', mt, mb }: TagListProps) {
  const { t } = useTranslation()
  if (!tags || tags.length === 0) {
    return null
  }
  // Truncating to hide a single tag would spend a chip to save none — the same rule as the mobile
  // app (`PdlTagRow`), so a content has the same card on both.
  const truncated = max !== undefined && tags.length > max + 1
  const shown = truncated ? tags.slice(0, max) : tags
  const hidden = truncated ? tags.slice(max) : []

  return (
    <Group gap={6} wrap="wrap" mt={mt} mb={mb} role="group" aria-label={t('tags.listLabel')}>
      {shown.map((tag) => (
        <TagChip key={tag.id} tag={tag} size={size} />
      ))}
      {hidden.length > 0 && (
        <Tooltip label={hidden.map((tag) => tag.label).join(', ')} withArrow multiline maw={260}>
          <Badge
            variant="default"
            size={size}
            tt="none"
            fw={500}
            aria-label={t('tags.more', { count: hidden.length })}
          >
            {t('tags.moreShort', { hidden: hidden.length })}
          </Badge>
        </Tooltip>
      )}
    </Group>
  )
}
