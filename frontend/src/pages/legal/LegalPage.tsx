import { Container, Title, Stack } from '@mantine/core'
import { useTranslation } from 'react-i18next'
import { MarkdownDisplay } from '@/components/common/MarkdownDisplay'

import privacyFr from '../../../privacy/privacy-policy.fr.md?raw'
import privacyEn from '../../../privacy/privacy-policy.en.md?raw'
import termsFr from '../../../privacy/terms-of-service.fr.md?raw'
import termsEn from '../../../privacy/terms-of-service.en.md?raw'

const content = {
  privacy: { fr: privacyFr, en: privacyEn },
  terms: { fr: termsFr, en: termsEn },
} as const

interface LegalPageProps {
  type: 'privacy' | 'terms'
}

export function LegalPage({ type }: LegalPageProps) {
  const { t, i18n } = useTranslation()
  const lang = i18n.language.startsWith('fr') ? 'fr' : 'en'
  const markdown = content[type][lang]
  const titleKey = type === 'privacy' ? 'legal.privacy.title' : 'legal.terms.title'

  return (
    <Container size="md" py="xl">
      <Stack gap="md">
        <Title order={1}>{t(titleKey)}</Title>
        <MarkdownDisplay markdown={markdown} />
      </Stack>
    </Container>
  )
}
