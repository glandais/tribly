import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useForm } from '@mantine/form'
import {
  Container,
  Stack,
  Title,
  Text,
  SimpleGrid,
  Paper,
  Badge,
  Button,
  TextInput,
  Center,
  ThemeIcon,
} from '@mantine/core'
import {
  IconDeviceWatch,
  IconDeviceMobile,
  IconMountain,
  IconExternalLink,
  IconMail,
} from '@tabler/icons-react'
import { signUpForBeta } from '@/api/endpoints/beta-signups/beta-signups'

const KAROO_RELEASES_URL = 'https://github.com/glandais/tribly/releases?q=karoo'

type AppStatus = 'available' | 'comingSoon'

function AppCard({
  icon,
  title,
  description,
  status,
  action,
}: {
  icon: React.ReactNode
  title: string
  description: string
  status: AppStatus
  action?: React.ReactNode
}) {
  const { t } = useTranslation()
  return (
    <Paper shadow="sm" radius="lg" p="lg" withBorder>
      <Stack gap="sm">
        <ThemeIcon size={48} radius="lg" variant="light">
          {icon}
        </ThemeIcon>
        <Title order={3}>{title}</Title>
        <Badge color={status === 'available' ? 'green' : 'gray'} w="fit-content">
          {t(status === 'available' ? 'apps.status.available' : 'apps.status.comingSoon')}
        </Badge>
        <Text c="dimmed" size="sm">
          {description}
        </Text>
        {action}
      </Stack>
    </Paper>
  )
}

export function AppsPage() {
  const { t } = useTranslation()
  const [isLoading, setIsLoading] = useState(false)
  const [sent, setSent] = useState(false)

  const form = useForm({
    initialValues: { email: '' },
    validate: {
      email: (v) =>
        !v || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v) ? t('apps.signup.validation.email') : null,
    },
  })

  const handleSubmit = async (values: { email: string }) => {
    setIsLoading(true)
    try {
      await signUpForBeta(values)
      setSent(true)
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <Container size="md" py="xl">
      <Stack gap="xl">
        <Stack gap="xs" ta="center">
          <Title order={1}>{t('apps.title')}</Title>
          <Text c="dimmed">{t('apps.subtitle')}</Text>
        </Stack>

        <SimpleGrid cols={{ base: 1, sm: 3 }}>
          <AppCard
            icon={<IconMountain size={28} />}
            title={t('apps.karoo.title')}
            description={t('apps.karoo.description')}
            status="available"
            action={
              <Button
                component="a"
                href={KAROO_RELEASES_URL}
                target="_blank"
                rel="noopener noreferrer"
                variant="light"
                rightSection={<IconExternalLink size={16} />}
              >
                {t('apps.karoo.download')}
              </Button>
            }
          />
          <AppCard
            icon={<IconDeviceMobile size={28} />}
            title={t('apps.mobile.title')}
            description={t('apps.mobile.description')}
            status="comingSoon"
          />
          <AppCard
            icon={<IconDeviceWatch size={28} />}
            title={t('apps.garmin.title')}
            description={t('apps.garmin.description')}
            status="comingSoon"
          />
        </SimpleGrid>

        <Paper shadow="sm" radius="lg" p="xl" withBorder>
          {sent ? (
            <Center>
              <Stack ta="center" gap="xs">
                <ThemeIcon size={48} radius="xl" color="green" variant="light">
                  <IconMail size={24} />
                </ThemeIcon>
                <Title order={3}>{t('apps.signup.sent.title')}</Title>
                <Text c="dimmed">{t('apps.signup.sent.message')}</Text>
              </Stack>
            </Center>
          ) : (
            <Stack>
              <Stack gap={4}>
                <Title order={3}>{t('apps.signup.title')}</Title>
                <Text c="dimmed" size="sm">
                  {t('apps.signup.subtitle')}
                </Text>
              </Stack>
              <form onSubmit={form.onSubmit(handleSubmit)}>
                <Stack>
                  <TextInput
                    label={t('apps.signup.emailLabel')}
                    placeholder="email@example.com"
                    autoComplete="email"
                    {...form.getInputProps('email')}
                  />
                  <Button type="submit" loading={isLoading}>
                    {t('apps.signup.submit')}
                  </Button>
                </Stack>
              </form>
            </Stack>
          )}
        </Paper>
      </Stack>
    </Container>
  )
}
