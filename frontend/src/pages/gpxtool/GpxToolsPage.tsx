import { useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useNavigate } from 'react-router-dom'
import { IconListDetails, IconMapSearch } from '@tabler/icons-react'
import { Button, Card, Group, SimpleGrid, Stack, Text, Title } from '@mantine/core'
import { createPreview } from '@/api/endpoints/gpx-previews/gpx-previews'
import { paths } from '@/config/paths'

/**
 * Hub for the GPX tools. Only "view a file" for now; the grid is where merging, URL import and
 * the empty map will land.
 */
export function GpxToolsPage() {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const fileInputRef = useRef<HTMLInputElement>(null)
  const [isUploading, setIsUploading] = useState(false)

  const handleFileSelected = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0]
    // Reset so re-picking the same file fires another change event.
    event.target.value = ''
    if (!file) return

    setIsUploading(true)
    try {
      const preview = await createPreview({ gpxFile: file })
      navigate(paths.gpxToolsView(preview.id))
    } finally {
      setIsUploading(false)
    }
  }

  return (
    <Stack>
      <Stack gap="xs">
        <Title order={1}>{t('gpxTools.title')}</Title>
        <Text c="dimmed">{t('gpxTools.subtitle')}</Text>
      </Stack>

      <SimpleGrid cols={{ base: 1, sm: 2, lg: 3 }}>
        <Card withBorder padding="lg">
          <Stack justify="space-between" h="100%">
            <Stack gap="xs">
              <Group gap="xs">
                <IconMapSearch size={20} />
                <Text fw={500}>{t('gpxTools.viewFile.title')}</Text>
              </Group>
              <Text size="sm" c="dimmed">
                {t('gpxTools.viewFile.description')}
              </Text>
            </Stack>

            <input
              ref={fileInputRef}
              type="file"
              accept=".gpx"
              hidden
              onChange={handleFileSelected}
            />
            <Button onClick={() => fileInputRef.current?.click()} loading={isUploading}>
              {t('gpxTools.viewFile.submit')}
            </Button>
          </Stack>
        </Card>

        <Card withBorder padding="lg">
          <Stack justify="space-between" h="100%">
            <Stack gap="xs">
              <Group gap="xs">
                <IconListDetails size={20} />
                <Text fw={500}>{t('gpxTools.listFiles.title')}</Text>
              </Group>
              <Text size="sm" c="dimmed">
                {t('gpxTools.listFiles.description')}
              </Text>
            </Stack>

            <Button variant="default" onClick={() => navigate(paths.gpxToolsList())}>
              {t('gpxTools.listFiles.submit')}
            </Button>
          </Stack>
        </Card>
      </SimpleGrid>

      <Text size="sm" c="dimmed">
        {t('gpxTools.retentionNotice')}
      </Text>
    </Stack>
  )
}
