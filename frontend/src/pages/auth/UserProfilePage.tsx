import { useRef, useState } from 'react'
import { useForm } from '@mantine/form'
import { zod4Resolver } from 'mantine-form-zod-resolver'
import { useTranslation } from 'react-i18next'
import { IconCamera, IconX } from '@tabler/icons-react'
import {
  Button,
  TextInput,
  Stack,
  Group,
  Paper,
  Title,
  Text,
  Box,
  ActionIcon,
  Divider,
  Center,
} from '@mantine/core'
import { useAuth } from '../../hooks/useAuth'
import { LoadingSpinner } from '../../components/common/LoadingSpinner'
import { ConfirmDialog } from '../../components/common/ConfirmDialog'
import { UserAvatar } from '../../components/common/UserAvatar'
import { updateMeBody } from '@/api/zod/users/users.zod'
import { UpdateUserRequest } from '@/api/dto'

const profileSchema = updateMeBody

export function UserProfilePage() {
  const { t } = useTranslation()
  const {
    user,
    isLoading,
    updateProfile,
    isUpdatingProfile,
    deleteAccount,
    isDeletingAccount,
    uploadAvatar,
    isUploadingAvatar,
    deleteAvatar,
    isDeletingAvatar,
    logout,
  } = useAuth()

  const avatarInputRef = useRef<HTMLInputElement>(null)
  const [isEditing, setIsEditing] = useState(false)
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false)

  const form = useForm<UpdateUserRequest>({
    validate: zod4Resolver(profileSchema) as any,
    validateInputOnChange: true,
    initialValues: user || { displayName: '' },
  })

  if (isLoading || !user) {
    return (
      <Center py="xl">
        <LoadingSpinner size="lg" />
      </Center>
    )
  }

  const handleStartEditing = () => {
    form.setValues(user)
    form.validate()
    setIsEditing(true)
  }

  const handleSubmit = (values: UpdateUserRequest) => {
    updateProfile(values, {
      onSuccess: () => {
        setIsEditing(false)
      },
    })
  }

  const handleDelete = () => {
    deleteAccount()
  }

  const handleAvatarSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (file) {
      uploadAvatar(file)
    }
    if (avatarInputRef.current) {
      avatarInputRef.current.value = ''
    }
  }

  return (
    <Box maw={672} mx="auto">
      <Paper shadow="sm" radius="md">
        <Box px="lg" py="md" style={{ borderBottom: '1px solid var(--mantine-color-gray-2)' }}>
          <Title order={2} size="h4">
            {t('profile.title')}
          </Title>
        </Box>

        <Stack gap="lg" p="lg">
          <Group gap="lg">
            <Box pos="relative">
              <UserAvatar user={user} size="xl" />
              <Group gap={4} pos="absolute" bottom={-4} right={-4}>
                <input
                  ref={avatarInputRef}
                  type="file"
                  accept="image/*"
                  onChange={handleAvatarSelect}
                  style={{ display: 'none' }}
                />
                <ActionIcon
                  variant="filled"
                  color="indigo"
                  size="sm"
                  radius="xl"
                  onClick={() => avatarInputRef.current?.click()}
                  disabled={isUploadingAvatar}
                  title={t('profile.avatar.upload')}
                >
                  {isUploadingAvatar ? <LoadingSpinner size="sm" /> : <IconCamera size={14} />}
                </ActionIcon>
                {user.avatarUrl && (
                  <ActionIcon
                    variant="filled"
                    color="red"
                    size="sm"
                    radius="xl"
                    onClick={() => deleteAvatar()}
                    disabled={isDeletingAvatar}
                    title={t('profile.avatar.remove')}
                  >
                    {isDeletingAvatar ? <LoadingSpinner size="sm" /> : <IconX size={14} />}
                  </ActionIcon>
                )}
              </Group>
            </Box>
            <Box>
              <Text fw={500} size="lg">
                {user.displayName}
              </Text>
              <Text c="dimmed">{user.email}</Text>
            </Box>
          </Group>

          <Divider />

          {isEditing ? (
            <form onSubmit={form.onSubmit(handleSubmit)}>
              <Stack gap="md">
                <TextInput
                  label={t('profile.form.displayName.label')}
                  {...form.getInputProps('displayName')}
                />

                <Group gap="sm" pt="md">
                  <Button
                    type="submit"
                    disabled={isUpdatingProfile || !form.isValid()}
                    loading={isUpdatingProfile}
                  >
                    {t('actions.save')}
                  </Button>
                  <Button type="button" variant="default" onClick={() => setIsEditing(false)}>
                    {t('actions.cancelAction')}
                  </Button>
                </Group>
              </Stack>
            </form>
          ) : (
            <Stack gap="md">
              <Box>
                <Text size="sm" fw={500} c="dimmed">
                  {t('profile.form.displayName.label')}
                </Text>
                <Text size="sm" mt={4}>
                  {user.displayName}
                </Text>
              </Box>
              <Box>
                <Text size="sm" fw={500} c="dimmed">
                  {t('profile.form.email.label')}
                </Text>
                <Text size="sm" mt={4}>
                  {user.email}
                </Text>
              </Box>

              <Box pt="md">
                <Button onClick={handleStartEditing}>{t('profile.actions.editProfile')}</Button>
              </Box>
            </Stack>
          )}

          <Divider />

          <Stack gap="md">
            <Title order={3} size="h5">
              {t('profile.account.title')}
            </Title>

            <Button variant="default" onClick={logout}>
              {t('profile.account.signOut')}
            </Button>

            <Box pt="md" style={{ borderTop: '1px solid var(--mantine-color-gray-2)' }}>
              <Text size="sm" fw={500} c="red">
                {t('profile.account.dangerZone.title')}
              </Text>
              <Text size="sm" c="dimmed" mt={4}>
                {t('profile.account.dangerZone.deleteDescription')}
              </Text>

              <Button
                variant="outline"
                color="red"
                mt="md"
                onClick={() => setShowDeleteConfirm(true)}
              >
                {t('profile.account.dangerZone.deleteButton')}
              </Button>
            </Box>
          </Stack>
        </Stack>
      </Paper>

      <ConfirmDialog
        isOpen={showDeleteConfirm}
        onClose={() => setShowDeleteConfirm(false)}
        onConfirm={handleDelete}
        title={t('profile.account.dangerZone.title')}
        message={t('profile.account.dangerZone.confirmMessage')}
        confirmText={t('profile.account.dangerZone.confirmButton')}
        variant="danger"
        isLoading={isDeletingAccount}
      />
    </Box>
  )
}
