import { useTranslation } from 'react-i18next'
import { Stack, Paper, Group, Text, Anchor, Image } from '@mantine/core'
import { IconPaperclip, IconDownload } from '@tabler/icons-react'
import { MediaDto, AssetDto } from '@/api/dto'
import { MarkdownDisplay } from './MarkdownDisplay'
import { getImageSizeWidth } from '@/lib/assetMarkdown'

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
          <Stack gap="xs">
            {attachments.map((attachment: AssetDto) => {
              const thumbnailUrl = attachment.imageUrl?.replace(
                '{size}',
                String(getImageSizeWidth('thumbnail'))
              )
              return (
                <Anchor
                  key={attachment.id}
                  href={attachment.url}
                  target="_blank"
                  rel="noopener noreferrer"
                  size="sm"
                >
                  <Group gap="xs">
                    {thumbnailUrl ? (
                      <Image
                        src={thumbnailUrl}
                        alt={attachment.fileName}
                        w={getImageSizeWidth('thumbnail')}
                        h={getImageSizeWidth('thumbnail')}
                        fit="cover"
                        radius="sm"
                      />
                    ) : (
                      <IconPaperclip size={14} color="var(--mantine-color-dimmed)" />
                    )}
                    <Text truncate style={{ flex: 1 }}>
                      {attachment.fileName}
                    </Text>
                    <IconDownload size={14} />
                  </Group>
                </Anchor>
              )
            })}
          </Stack>
        </Paper>
      )}
    </Stack>
  )
}
