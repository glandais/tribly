import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { Center, Stack, Title, Text, Anchor } from '@mantine/core'

export function NotFoundPage() {
  const { t } = useTranslation()
  return (
    <Center mih="60vh">
      <Stack align="center" gap="md">
        <Title order={1}>{t('notFound.title')}</Title>
        <Text size="lg" c="dimmed">
          {t('notFound.message')}
        </Text>
        <Anchor component={Link} to="/" fw={500}>
          {t('errors.notFound.backHome')}
        </Anchor>
      </Stack>
    </Center>
  )
}
