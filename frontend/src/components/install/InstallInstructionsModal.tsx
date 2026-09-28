import { useTranslation } from 'react-i18next'
import { Group, Modal, Stack, Text, ThemeIcon } from '@mantine/core'
import { IconSquareArrowUp, IconSquarePlus, IconCircleCheck } from '@tabler/icons-react'
import { closeInstallInstructions, useInstallStore } from '@/lib/install/installStore'
import { useAppName } from '@/hooks/useAppName'

/**
 * How to add the site to an iOS home screen — there is no install prompt there, only the Share
 * menu. Mounted once, in `Layout`; opened by every install offer through the install store.
 */
export function InstallInstructionsModal() {
  const { t } = useTranslation()
  const appName = useAppName()
  const opened = useInstallStore((state) => state.instructionsOpen)

  const steps = [
    { icon: <IconSquareArrowUp size={18} />, text: t('install.instructions.share') },
    { icon: <IconSquarePlus size={18} />, text: t('install.instructions.addToHomeScreen') },
    { icon: <IconCircleCheck size={18} />, text: t('install.instructions.confirm') },
  ]

  return (
    <Modal
      opened={opened}
      onClose={closeInstallInstructions}
      title={t('install.instructions.title', { appName })}
      centered
    >
      <Stack>
        <Text size="sm" c="dimmed">
          {t('install.instructions.intro')}
        </Text>
        {steps.map((step, index) => (
          <Group key={index} wrap="nowrap" align="flex-start">
            <ThemeIcon variant="light" radius="xl" size="lg">
              {step.icon}
            </ThemeIcon>
            <Text size="sm" pt={6}>
              {index + 1}. {step.text}
            </Text>
          </Group>
        ))}
        <Text size="xs" c="dimmed">
          {t('install.instructions.notifications')}
        </Text>
      </Stack>
    </Modal>
  )
}
