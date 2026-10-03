import type { ComponentType } from 'react'
import { useTranslation } from 'react-i18next'
import { Anchor, Box, Group, Paper, SimpleGrid, Stack, Text, ThemeIcon, Title } from '@mantine/core'
import {
  IconArrowRight,
  IconArticle,
  IconBike,
  IconMap2,
  IconRoute,
  type IconProps,
} from '@tabler/icons-react'
import { PrefetchLink } from '@/components/common/PrefetchLink'
import { paths } from '@/config/paths'
import { PUBLICATION_TYPE_COLORS } from '@/lib/badgeColors.generated'

type TileKey = 'rides' | 'routes' | 'trips' | 'posts'

const TILES: { key: TileKey; icon: ComponentType<IconProps>; color: string }[] = [
  { key: 'rides', icon: IconBike, color: PUBLICATION_TYPE_COLORS.RIDE },
  { key: 'routes', icon: IconRoute, color: 'primary' },
  { key: 'trips', icon: IconMap2, color: PUBLICATION_TYPE_COLORS.TRIP },
  { key: 'posts', icon: IconArticle, color: PUBLICATION_TYPE_COLORS.POST },
]

/** Four tiles of what the product does, each leading to the features page. */
export function FeatureTeaser() {
  const { t } = useTranslation()

  return (
    <Stack component="section" aria-labelledby="home-features-title">
      <Group justify="space-between" align="flex-end" gap="sm">
        <Box>
          <Title id="home-features-title" order={2}>
            {t('home.visitor.features.title')}
          </Title>
          <Text c="dimmed" mt={4}>
            {t('home.visitor.features.subtitle')}
          </Text>
        </Box>
        <Anchor component={PrefetchLink} to={paths.features()} fw={500}>
          <Group gap={4} wrap="nowrap">
            {t('home.visitor.features.all')}
            <IconArrowRight size={16} />
          </Group>
        </Anchor>
      </Group>

      <SimpleGrid cols={{ base: 2, md: 4 }} spacing={{ base: 'sm', sm: 'md' }}>
        {TILES.map(({ key, icon: Icon, color }) => (
          <Paper
            key={key}
            withBorder
            radius="md"
            p="md"
            component={PrefetchLink}
            to={paths.features()}
            style={{ color: 'inherit', textDecoration: 'none' }}
          >
            <Stack gap="xs">
              <ThemeIcon variant="light" color={color} size={40} radius="md">
                <Icon size={22} />
              </ThemeIcon>
              <Text fw={600}>{t(`home.visitor.features.${key}.title`)}</Text>
              <Text size="sm" c="dimmed">
                {t(`home.visitor.features.${key}.text`)}
              </Text>
            </Stack>
          </Paper>
        ))}
      </SimpleGrid>
    </Stack>
  )
}
