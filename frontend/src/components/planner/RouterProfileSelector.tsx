import { useState, useCallback } from 'react'
import { useTranslation } from 'react-i18next'
import {
  IconBike,
  IconBolt,
  IconRoad,
  IconMountain,
  IconWalk,
  IconMotorbike,
  IconX,
} from '@tabler/icons-react'
import { Box, Paper, Group, Text, Stack, ActionIcon, UnstyledButton } from '@mantine/core'
import { RouterProfile } from '@/api/dto'
import { MapControlButton, MapControlGroup } from '@/components/map/MapControl'

interface RouterProfileSelectorProps {
  currentProfile: RouterProfile
  onProfileChange: (profile: RouterProfile) => void
}

const PROFILE_ICONS: Record<RouterProfile, React.ComponentType<{ size?: number }>> = {
  [RouterProfile.BIKE]: IconBike,
  [RouterProfile.FASTBIKE]: IconBolt,
  [RouterProfile.GRAVEL]: IconRoad,
  [RouterProfile.MTB]: IconMountain,
  [RouterProfile.RUN_HIKE]: IconWalk,
  [RouterProfile.MOTORCYCLE]: IconMotorbike,
}

const PROFILES: RouterProfile[] = [
  RouterProfile.BIKE,
  RouterProfile.FASTBIKE,
  RouterProfile.GRAVEL,
  RouterProfile.MTB,
  RouterProfile.RUN_HIKE,
  RouterProfile.MOTORCYCLE,
]

export function RouterProfileSelector({
  currentProfile,
  onProfileChange,
}: RouterProfileSelectorProps) {
  const { t } = useTranslation()
  const [isExpanded, setIsExpanded] = useState(false)

  const toggleExpanded = useCallback(() => {
    setIsExpanded((prev) => !prev)
  }, [])

  const handleProfileSelect = useCallback(
    (profile: RouterProfile) => {
      onProfileChange(profile)
      setIsExpanded(false)
    },
    [onProfileChange]
  )

  const CurrentIcon = PROFILE_ICONS[currentProfile]

  return (
    <>
      <MapControlGroup>
        <MapControlButton
          label={t('planner.profile.label')}
          icon={<CurrentIcon size={20} />}
          onClick={toggleExpanded}
          active={isExpanded}
        />
      </MapControlGroup>

      {isExpanded && (
        <Box pos="absolute" top={10} left={48} style={{ zIndex: 1001 }}>
          <Paper shadow="lg" p="sm" radius="md" maw={200}>
            <Group
              justify="space-between"
              align="center"
              mb="sm"
              pb="sm"
              style={{ borderBottom: '1px solid var(--mantine-color-default-border)' }}
            >
              <Text size="sm" fw={500} c="dimmed">
                {t('planner.profile.label')}
              </Text>
              <ActionIcon
                variant="subtle"
                color="gray"
                size="sm"
                onClick={toggleExpanded}
                aria-label={t('actions.cancelAction')}
              >
                <IconX size={16} />
              </ActionIcon>
            </Group>
            <Stack gap={4}>
              {PROFILES.map((profile) => {
                const Icon = PROFILE_ICONS[profile]
                const isSelected = profile === currentProfile
                return (
                  <UnstyledButton
                    key={profile}
                    onClick={() => handleProfileSelect(profile)}
                    px="sm"
                    py="xs"
                    style={{
                      borderRadius: 'var(--mantine-radius-sm)',
                      backgroundColor: isSelected
                        ? 'var(--mantine-primary-color-light)'
                        : undefined,
                      transition: 'background-color 150ms',
                    }}
                  >
                    <Group gap="xs">
                      <Icon size={18} />
                      <Text
                        size="sm"
                        c={isSelected ? 'var(--mantine-primary-color-filled)' : undefined}
                        fw={isSelected ? 500 : 400}
                      >
                        {t(
                          `planner.profile.${profile satisfies 'BIKE' | 'FASTBIKE' | 'GRAVEL' | 'MTB' | 'RUN_HIKE' | 'MOTORCYCLE'}`
                        )}
                      </Text>
                    </Group>
                  </UnstyledButton>
                )
              })}
            </Stack>
          </Paper>
        </Box>
      )}
    </>
  )
}
