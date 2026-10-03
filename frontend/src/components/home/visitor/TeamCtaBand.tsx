import { useTranslation } from 'react-i18next'
import { Box, Button, Flex, Group, Paper, Text, Title } from '@mantine/core'
import { PrefetchLink } from '@/components/common/PrefetchLink'
import { paths } from '@/config/paths'
import { useAppName } from '@/hooks/useAppName'

interface TeamCtaBandProps {
  /** Opens the hero form on its account creation step. */
  onCreateAccount: () => void
}

/**
 * « Votre équipe n'est pas encore sur … ? » — for a team to sign up. Not shown on a single-team
 * site, where team creation is irrelevant.
 */
export function TeamCtaBand({ onCreateAccount }: TeamCtaBandProps) {
  const { t } = useTranslation()
  const appName = useAppName()

  return (
    <Paper
      radius="lg"
      p={{ base: 'md', sm: 'xl' }}
      bg="var(--mantine-primary-color-filled)"
      c="white"
      component="section"
      aria-labelledby="home-cta-title"
    >
      <Flex direction={{ base: 'column', md: 'row' }} gap="lg" align={{ md: 'center' }}>
        <Box style={{ flex: 1 }}>
          <Title id="home-cta-title" order={2} c="white">
            {t('home.visitor.cta.title', { appName })}
          </Title>
          <Text mt={4} c="white" opacity={0.9}>
            {t('home.visitor.cta.text')}
          </Text>
        </Box>
        <Group gap="sm">
          <Button variant="white" onClick={onCreateAccount}>
            {t('auth.login.methods.register')}
          </Button>
          <Button component={PrefetchLink} to={paths.features()} variant="outline" color="white">
            {t('home.visitor.discover')}
          </Button>
        </Group>
      </Flex>
    </Paper>
  )
}
