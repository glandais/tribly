import { useTranslation } from 'react-i18next'
import { Stack, Paper, Group, Text, Anchor } from '@mantine/core'
import { IconPaperclip, IconDownload } from '@tabler/icons-react'
import { MediaDto, AssetDto } from '@/api/dto'
import { MarkdownDisplay } from './MarkdownDisplay'

export interface MediaDisplayProps {
  media: MediaDto
}

export function MediaDisplay({ media }: MediaDisplayProps) {
  const { t } = useTranslation()
  const attachments = media.assets.attachments
  const images = media.assets.images

  return (
    <Stack gap="md">
      <MarkdownDisplay markdown={media.markdown} preview={false} images={images} />

      {attachments.length > 0 && (
        <Paper withBorder p="sm" bg="var(--mantine-color-body)">
          <Group gap="xs" mb="xs">
            <IconPaperclip size={16} />
            <Text size="sm" fw={500}>
              {t('attachments.title')}
            </Text>
          </Group>
          <Stack gap={4}>
            {attachments.map((attachment: AssetDto) => (
              <Anchor
                key={attachment.id}
                href={attachment.url}
                target="_blank"
                rel="noopener noreferrer"
                size="sm"
              >
                <Group gap="xs">
                  <IconPaperclip size={14} color="var(--mantine-color-dimmed)" />
                  <Text truncate style={{ flex: 1 }}>
                    {attachment.fileName}
                  </Text>
                  <IconDownload size={14} />
                </Group>
              </Anchor>
            ))}
          </Stack>
        </Paper>
      )}
    </Stack>
  )
}
