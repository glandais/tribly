import { useTranslation } from 'react-i18next'
import { IconUsers, IconChevronRight, IconShieldCheck } from '@tabler/icons-react'
import { Stack, Group, Text, Box, ThemeIcon, Modal } from '@mantine/core'
import { UserAvatar } from '../common/UserAvatar'

interface Participant {
  id: string
  displayName: string
  avatarUrl?: string
  isOrganizer?: boolean
}

interface ParticipantListModalProps {
  isOpen: boolean
  onClose: () => void
  participants: Participant[]
  groupName: string
}

export function ParticipantListModal({
  isOpen,
  onClose,
  participants,
  groupName,
}: ParticipantListModalProps) {
  const { t } = useTranslation()

  const participantCount = participants.length

  return (
    <Modal opened={isOpen} onClose={onClose} title={groupName} size="xl">
      <Stack>
        <Text size="sm" fw={600} c="primary">
          {t('rides.detail.groups.participantsNoMax', { current: participantCount })}
        </Text>
        {participants.length === 0 ? (
          <Stack align="center" py="xl">
            <IconUsers size={48} color="var(--mantine-color-dimmed)" />
            <Text size="sm" fw={500} c="dimmed">
              {t('rides.detail.groups.empty')}
            </Text>
          </Stack>
        ) : (
          <Stack gap="xs" component="ul" style={{ listStyle: 'none', padding: 0, margin: 0 }}>
            {participants.map((participant) => (
              <Box
                key={participant.id}
                component="li"
                p="sm"
                style={{
                  borderRadius: 'var(--mantine-radius-md)',
                  transition: 'all 200ms ease',
                  cursor: 'default',
                }}
                mod={{ hover: true }}
                __vars={{
                  '--hover-bg':
                    'linear-gradient(to right, var(--mantine-primary-color-light), var(--mantine-primary-color-light))',
                }}
              >
                <Group gap="sm" wrap="nowrap">
                  {/* Avatar with organizer badge */}
                  <Box pos="relative" style={{ flexShrink: 0 }}>
                    <UserAvatar user={participant} size="md" />
                    {participant.isOrganizer && (
                      <ThemeIcon
                        size={20}
                        radius="xl"
                        color="primary"
                        pos="absolute"
                        bottom={-4}
                        right={-4}
                        style={{ border: '2px solid white' }}
                        title={t('roles.ORGANIZER')}
                      >
                        <IconShieldCheck size={12} />
                      </ThemeIcon>
                    )}
                  </Box>

                  {/* Participant info */}
                  <Box style={{ flex: 1, minWidth: 0 }}>
                    <Text size="sm" fw={600} truncate>
                      {participant.displayName}
                    </Text>
                    {participant.isOrganizer && (
                      <Text size="xs" c="dimmed" mt={2}>
                        {t('groups.groupOrganizer')}
                      </Text>
                    )}
                  </Box>

                  {/* Subtle hover indicator */}
                  <Box style={{ flexShrink: 0, opacity: 0.5 }}>
                    <IconChevronRight size={16} color="var(--mantine-primary-color-light-color)" />
                  </Box>
                </Group>
              </Box>
            ))}
          </Stack>
        )}
      </Stack>
    </Modal>
  )
}
