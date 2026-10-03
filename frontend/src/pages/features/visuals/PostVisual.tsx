import { useTranslation } from 'react-i18next'
import { Avatar, Box, Group, Paper, Stack, Text, Title } from '@mantine/core'
import { FALLBACK_GRADIENTS, PUBLICATION_TYPE_COLORS } from '@/lib/badgeColors.generated'
import { TypeBadge } from '@/components/card/common'
import { VisualFrame } from './VisualFrame'

/** A publication and two comments under it. */
export function PostVisual() {
  const { t } = useTranslation()
  const comments = [
    {
      initials: 'JL',
      author: t('features.demo.post.comment1Author'),
      when: t('features.demo.post.comment1When'),
      body: t('features.demo.post.comment1Body'),
    },
    {
      initials: 'CM',
      author: t('features.demo.post.comment2Author'),
      when: t('features.demo.post.comment2When'),
      body: t('features.demo.post.comment2Body'),
    },
  ]

  return (
    <VisualFrame color={PUBLICATION_TYPE_COLORS.POST}>
      <Paper withBorder radius="lg" style={{ overflow: 'hidden' }}>
        <Box h={72} style={{ background: FALLBACK_GRADIENTS.POST }} />
        <Stack gap="sm" p="md">
          <Group justify="space-between" align="flex-start" wrap="nowrap" gap="sm">
            <Title order={3} size="h4">
              {t('features.demo.post.title')}
            </Title>
            <Box style={{ flex: 'none' }}>
              <TypeBadge type="POST">{t('publicationType.post')}</TypeBadge>
            </Box>
          </Group>
          <Text size="sm" c="dimmed">
            {t('features.demo.post.excerpt')}
          </Text>
          <Stack
            gap="sm"
            pt="sm"
            style={{ borderTop: '1px solid var(--mantine-color-default-border)' }}
          >
            {comments.map((comment) => (
              <Group key={comment.initials} gap="sm" align="flex-start" wrap="nowrap">
                <Avatar size="sm" radius="xl" color="initials" name={comment.author}>
                  {comment.initials}
                </Avatar>
                <Stack gap={0} style={{ minWidth: 0 }}>
                  <Text size="xs" c="dimmed">
                    {comment.author} · {comment.when}
                  </Text>
                  <Text size="sm">{comment.body}</Text>
                </Stack>
              </Group>
            ))}
          </Stack>
        </Stack>
      </Paper>
    </VisualFrame>
  )
}
