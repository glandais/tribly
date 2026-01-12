import { useTranslation } from 'react-i18next'
import { SegmentedControl, Stack, Text } from '@mantine/core'
import { usePreferencesStore } from '../../store/preferencesStore'
import { useAuth } from '../../hooks/useAuth'
import { UnitSystem } from '@/api/dto'

export function UnitSystemSwitcher() {
  const { t } = useTranslation()
  const unitSystem = usePreferencesStore((state) => state.unitSystem)
  const setUnitSystem = usePreferencesStore((state) => state.setUnitSystem)
  const { updateProfile, isUpdatingProfile } = useAuth()

  const handleChange = (value: string) => {
    const newUnitSystem = value as UnitSystem
    setUnitSystem(newUnitSystem)
    // Sync to server if authenticated
    updateProfile({ unitSystem: newUnitSystem })
  }

  return (
    <Stack gap="xs">
      <Text size="sm" fw={500}>
        {t('profile.preferences.unitSystem.label')}
      </Text>
      <SegmentedControl
        value={unitSystem}
        onChange={handleChange}
        disabled={isUpdatingProfile}
        data={[
          { value: 'METRIC', label: t('profile.preferences.unitSystem.metric') },
          { value: 'IMPERIAL', label: t('profile.preferences.unitSystem.imperial') },
        ]}
      />
    </Stack>
  )
}
