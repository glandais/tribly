import { useState, useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { useForm } from '@mantine/form'
import { notifications } from '@mantine/notifications'
import {
  Modal,
  Stack,
  TextInput,
  Switch,
  Button,
  Group,
  Table,
  Badge,
  ActionIcon,
  Tooltip,
  Text,
  Paper,
  Select,
  PasswordInput,
  Divider,
  Alert,
  Loader,
  Center,
} from '@mantine/core'
import { IconPlus, IconPencil, IconTrash, IconCheck, IconX } from '@tabler/icons-react'
import type {
  AdminDomainDto,
  AdminGpsCredentialDto,
  CreateGpsCredentialRequest,
  UpdateGpsCredentialRequest,
} from '@/api/dto'
import { GpsServiceType } from '@/api/dto'
import {
  useCreateDomain,
  useUpdateDomain,
  getListDomainsQueryKey,
  useListDomainGpsCredentials,
  useCreateDomainGpsCredential,
  useUpdateDomainGpsCredential,
  useDeleteDomainGpsCredential,
  getListDomainGpsCredentialsQueryKey,
} from '@/api/endpoints/admin-domains/admin-domains'
import { ConfirmDialog } from '@/components/common/ConfirmDialog'

interface DomainFormModalProps {
  isOpen: boolean
  onClose: () => void
  domain?: AdminDomainDto
}

interface DomainFormValues {
  domain: string
  name: string
  baseUrl: string
  singleTeam: boolean
}

interface CredentialFormValues {
  serviceType: GpsServiceType
  clientId: string
  clientSecret: string
  active: boolean
}

export function DomainFormModal({ isOpen, onClose, domain }: DomainFormModalProps) {
  const { t } = useTranslation()
  const queryClient = useQueryClient()
  const isEditMode = !!domain

  // Domain mutations
  const createDomainMutation = useCreateDomain()
  const updateDomainMutation = useUpdateDomain()

  // Domain form
  const domainForm = useForm<DomainFormValues>({
    initialValues: {
      domain: '',
      name: '',
      baseUrl: '',
      singleTeam: false,
    },
  })

  // Reset form when domain changes (edit vs create mode)
  useEffect(() => {
    if (isOpen) {
      domainForm.setValues({
        domain: domain?.domain ?? '',
        name: domain?.name ?? '',
        baseUrl: domain?.baseUrl ?? '',
        singleTeam: domain?.singleTeam ?? false,
      })
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [domain, isOpen])

  const handleDomainSubmit = async (values: DomainFormValues) => {
    if (isEditMode && domain) {
      await updateDomainMutation.mutateAsync(
        {
          domainId: domain.id,
          data: {
            name: values.name,
            baseUrl: values.baseUrl,
            singleTeam: values.singleTeam,
          },
        },
        {
          onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: getListDomainsQueryKey() })
            notifications.show({
              message: t('admin.domains.notifications.updated'),
              color: 'green',
            })
            onClose()
          },
        }
      )
    } else {
      await createDomainMutation.mutateAsync(
        {
          data: {
            domain: values.domain,
            name: values.name,
            baseUrl: values.baseUrl,
            singleTeam: values.singleTeam,
          },
        },
        {
          onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: getListDomainsQueryKey() })
            notifications.show({
              message: t('admin.domains.notifications.created'),
              color: 'green',
            })
            onClose()
          },
        }
      )
    }
  }

  const isPending = createDomainMutation.isPending || updateDomainMutation.isPending

  return (
    <Modal
      opened={isOpen}
      onClose={onClose}
      title={isEditMode ? t('admin.domains.edit.title') : t('admin.domains.create.title')}
      size="lg"
    >
      <form onSubmit={domainForm.onSubmit(handleDomainSubmit)}>
        <Stack>
          <TextInput
            label={t('admin.domains.domain')}
            placeholder="example.com"
            required
            disabled={isEditMode}
            {...domainForm.getInputProps('domain')}
          />
          <TextInput
            label={t('admin.domains.name')}
            placeholder={t('admin.domains.namePlaceholder')}
            required
            {...domainForm.getInputProps('name')}
          />
          <TextInput
            label={t('admin.domains.baseUrl')}
            placeholder="https://example.com"
            required
            {...domainForm.getInputProps('baseUrl')}
          />
          <Switch
            label={t('admin.domains.singleTeam')}
            {...domainForm.getInputProps('singleTeam', { type: 'checkbox' })}
          />

          {isEditMode && domain && (
            <>
              <Divider my="md" />
              <GpsCredentialsSection domainId={domain.id} />
            </>
          )}

          <Group justify="flex-end" mt="md">
            <Button variant="default" onClick={onClose} disabled={isPending}>
              {t('actions.cancelAction')}
            </Button>
            <Button type="submit" loading={isPending}>
              {t('actions.save')}
            </Button>
          </Group>
        </Stack>
      </form>
    </Modal>
  )
}

function GpsCredentialsSection({ domainId }: { domainId: string }) {
  const { t } = useTranslation()
  const queryClient = useQueryClient()
  const [isAddingCredential, setIsAddingCredential] = useState(false)
  const [editingCredentialId, setEditingCredentialId] = useState<string | null>(null)
  const [deleteCredential, setDeleteCredential] = useState<AdminGpsCredentialDto | null>(null)

  // Fetch credentials
  const { data: credentials, isLoading } = useListDomainGpsCredentials(domainId)

  // Mutations
  const createMutation = useCreateDomainGpsCredential()
  const updateMutation = useUpdateDomainGpsCredential()
  const deleteMutation = useDeleteDomainGpsCredential()

  // Add form
  const addForm = useForm<CredentialFormValues>({
    initialValues: {
      serviceType: GpsServiceType.HAMMERHEAD,
      clientId: '',
      clientSecret: '',
      active: true,
    },
  })

  // Edit form
  const editForm = useForm<CredentialFormValues>({
    initialValues: {
      serviceType: GpsServiceType.HAMMERHEAD,
      clientId: '',
      clientSecret: '',
      active: true,
    },
  })

  const handleAddCredential = async (values: CredentialFormValues) => {
    const request: CreateGpsCredentialRequest = {
      serviceType: values.serviceType,
      clientId: values.clientId,
      clientSecret: values.clientSecret || undefined,
      active: values.active,
    }

    await createMutation.mutateAsync(
      { domainId, data: request },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({
            queryKey: getListDomainGpsCredentialsQueryKey(domainId),
          })
          notifications.show({
            message: t('admin.domains.gpsCredentials.notifications.created'),
            color: 'green',
          })
          setIsAddingCredential(false)
          addForm.reset()
        },
      }
    )
  }

  const handleUpdateCredential = async (credentialId: string, values: CredentialFormValues) => {
    const request: UpdateGpsCredentialRequest = {
      clientId: values.clientId,
      clientSecret: values.clientSecret || undefined,
      active: values.active,
    }

    await updateMutation.mutateAsync(
      { domainId, credentialId, data: request },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({
            queryKey: getListDomainGpsCredentialsQueryKey(domainId),
          })
          notifications.show({
            message: t('admin.domains.gpsCredentials.notifications.updated'),
            color: 'green',
          })
          setEditingCredentialId(null)
        },
      }
    )
  }

  const handleDeleteCredential = async () => {
    if (!deleteCredential) return

    await deleteMutation.mutateAsync(
      { domainId, credentialId: deleteCredential.id },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({
            queryKey: getListDomainGpsCredentialsQueryKey(domainId),
          })
          notifications.show({
            message: t('admin.domains.gpsCredentials.notifications.deleted'),
            color: 'green',
          })
          setDeleteCredential(null)
        },
      }
    )
  }

  const startEditCredential = (credential: AdminGpsCredentialDto) => {
    editForm.setValues({
      serviceType: credential.serviceType,
      clientId: credential.clientId,
      clientSecret: '',
      active: credential.active,
    })
    setEditingCredentialId(credential.id)
  }

  const serviceTypeOptions = [
    { value: GpsServiceType.HAMMERHEAD, label: t('gps.services.hammerhead') },
    { value: GpsServiceType.GARMIN, label: t('gps.services.garmin') },
  ]

  // Get already used service types
  const usedServiceTypes = credentials?.map((c) => c.serviceType) ?? []
  const availableServiceTypes = serviceTypeOptions.filter(
    (opt) => !usedServiceTypes.includes(opt.value)
  )

  if (isLoading) {
    return (
      <Center py="md">
        <Loader size="sm" />
      </Center>
    )
  }

  return (
    <Stack>
      <Group justify="space-between">
        <Text fw={500}>{t('admin.domains.gpsCredentials.title')}</Text>
        {availableServiceTypes.length > 0 && !isAddingCredential && (
          <Button
            size="xs"
            variant="light"
            leftSection={<IconPlus size={14} />}
            onClick={() => {
              addForm.setFieldValue('serviceType', availableServiceTypes[0].value)
              setIsAddingCredential(true)
            }}
          >
            {t('admin.domains.gpsCredentials.add')}
          </Button>
        )}
      </Group>

      {isAddingCredential && (
        <Paper withBorder p="sm">
          <Stack gap="sm">
            <Select
              label={t('admin.domains.gpsCredentials.serviceType')}
              data={availableServiceTypes}
              required
              {...addForm.getInputProps('serviceType')}
            />
            <TextInput
              label={t('admin.domains.gpsCredentials.clientId')}
              required
              {...addForm.getInputProps('clientId')}
            />
            <PasswordInput
              label={t('admin.domains.gpsCredentials.clientSecret')}
              {...addForm.getInputProps('clientSecret')}
            />
            <Switch
              label={t('admin.status.active')}
              {...addForm.getInputProps('active', { type: 'checkbox' })}
            />
            <Group justify="flex-end">
              <Button
                size="xs"
                variant="default"
                type="button"
                onClick={() => {
                  setIsAddingCredential(false)
                  addForm.reset()
                }}
              >
                {t('actions.cancelAction')}
              </Button>
              <Button
                size="xs"
                type="button"
                loading={createMutation.isPending}
                onClick={() => addForm.onSubmit(handleAddCredential)()}
              >
                {t('actions.save')}
              </Button>
            </Group>
          </Stack>
        </Paper>
      )}

      {credentials && credentials.length > 0 ? (
        <Table.ScrollContainer minWidth={400}>
          <Table striped highlightOnHover>
            <Table.Thead>
              <Table.Tr>
                <Table.Th>{t('admin.domains.gpsCredentials.serviceType')}</Table.Th>
                <Table.Th>{t('admin.domains.gpsCredentials.clientId')}</Table.Th>
                <Table.Th ta="center">{t('admin.domains.status')}</Table.Th>
                <Table.Th ta="center">{t('admin.domains.actions')}</Table.Th>
              </Table.Tr>
            </Table.Thead>
            <Table.Tbody>
              {credentials.map((credential) =>
                editingCredentialId === credential.id ? (
                  <Table.Tr key={credential.id}>
                    <Table.Td colSpan={4}>
                      <Stack gap="xs">
                        <Group grow>
                          <TextInput
                            label={t('admin.domains.gpsCredentials.clientId')}
                            size="xs"
                            required
                            {...editForm.getInputProps('clientId')}
                          />
                          <PasswordInput
                            label={t('admin.domains.gpsCredentials.clientSecret')}
                            size="xs"
                            placeholder={t('admin.domains.gpsCredentials.clientSecretPlaceholder')}
                            {...editForm.getInputProps('clientSecret')}
                          />
                        </Group>
                        <Group justify="space-between">
                          <Switch
                            label={t('admin.status.active')}
                            size="xs"
                            {...editForm.getInputProps('active', { type: 'checkbox' })}
                          />
                          <Group>
                            <ActionIcon
                              variant="subtle"
                              color="gray"
                              type="button"
                              onClick={() => setEditingCredentialId(null)}
                            >
                              <IconX size={16} />
                            </ActionIcon>
                            <ActionIcon
                              variant="subtle"
                              color="green"
                              type="button"
                              loading={updateMutation.isPending}
                              onClick={() =>
                                editForm.onSubmit((values) =>
                                  handleUpdateCredential(credential.id, values)
                                )()
                              }
                            >
                              <IconCheck size={16} />
                            </ActionIcon>
                          </Group>
                        </Group>
                      </Stack>
                    </Table.Td>
                  </Table.Tr>
                ) : (
                  <Table.Tr key={credential.id}>
                    <Table.Td>
                      {serviceTypeOptions.find((o) => o.value === credential.serviceType)?.label}
                    </Table.Td>
                    <Table.Td>
                      <Text size="sm" c="dimmed">
                        {credential.clientId}
                      </Text>
                    </Table.Td>
                    <Table.Td ta="center">
                      <Badge color={credential.active ? 'green' : 'gray'} size="sm">
                        {credential.active ? t('admin.status.active') : t('admin.status.inactive')}
                      </Badge>
                    </Table.Td>
                    <Table.Td ta="center">
                      <Group gap="xs" justify="center">
                        <Tooltip label={t('actions.edit')}>
                          <ActionIcon
                            variant="subtle"
                            onClick={() => startEditCredential(credential)}
                          >
                            <IconPencil size={16} />
                          </ActionIcon>
                        </Tooltip>
                        <Tooltip label={t('actions.delete')}>
                          <ActionIcon
                            variant="subtle"
                            color="red"
                            onClick={() => setDeleteCredential(credential)}
                          >
                            <IconTrash size={16} />
                          </ActionIcon>
                        </Tooltip>
                      </Group>
                    </Table.Td>
                  </Table.Tr>
                )
              )}
            </Table.Tbody>
          </Table>
        </Table.ScrollContainer>
      ) : (
        !isAddingCredential && (
          <Alert color="gray" variant="light">
            {t('admin.domains.gpsCredentials.empty')}
          </Alert>
        )
      )}

      <ConfirmDialog
        isOpen={!!deleteCredential}
        onClose={() => setDeleteCredential(null)}
        onConfirm={handleDeleteCredential}
        title={t('admin.domains.gpsCredentials.deleteConfirm.title')}
        message={t('admin.domains.gpsCredentials.deleteConfirm.message')}
        variant="danger"
        isLoading={deleteMutation.isPending}
      />
    </Stack>
  )
}
