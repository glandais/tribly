import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { Stack, Switch, Text } from '@mantine/core'
import { isErrorReportingEnabled, setErrorReportingEnabled } from '@/lib/feedback/errorReporter'

/** Opt out of the automatic error reports. Stored in this browser only, like the app's own. */
export function ErrorReportingPreference() {
  const { t } = useTranslation()
  const [enabled, setEnabled] = useState(isErrorReportingEnabled)

  return (
    <Stack gap={4}>
      <Switch
        checked={enabled}
        onChange={(event) => {
          const checked = event.currentTarget.checked
          setErrorReportingEnabled(checked)
          setEnabled(checked)
        }}
        label={t('profile.errorReports.label')}
      />
      <Text size="xs" c="dimmed">
        {t('profile.errorReports.description')}
      </Text>
    </Stack>
  )
}
