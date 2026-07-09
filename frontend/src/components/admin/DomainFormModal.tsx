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
  Textarea,
  Divider,
  Alert,
  Loader,
  Center,
} from '@mantine/core'
import {
  IconPlus,
  IconPencil,
  IconTrash,
  IconCheck,
  IconX,
  IconToggleLeft,
  IconToggleRight,
} from '@tabler/icons-react'
import type {
  AdminDomainDto,
  AdminDomainAliasDto,
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
  useListDomainAliases,
  useCreateDomainAlias,
  useUpdateDomainAlias,
  useDeleteDomainAlias,
  useToggleDomainAliasActive,
  getListDomainAliasesQueryKey,
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
  androidFingerprints: string
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
      androidFingerprints: '',
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
        androidFingerprints: domain?.androidFingerprints ?? '',
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
            androidFingerprints: values.androidFingerprints || undefined,
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
            androidFingerprints: values.androidFingerprints || undefined,
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
          <Textarea
            label={t('admin.domains.androidFingerprints')}
            description={t('admin.domains.androidFingerprintsDescription')}
            placeholder={t('admin.domains.androidFingerprintsPlaceholder')}
            autosize
            minRows={2}
            maxRows={4}
            {...domainForm.getInputProps('androidFingerprints')}
          />

          {isEditMode && domain && (
            <>
              <Divider my="md" />
              <GpsCredentialsSection domainId={domain.id} />
              <Divider my="md" />
              <DomainAliasesSection domainId={domain.id} />
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

interface AliasFormValues {
  hostname: string
  teamSlug: string
  name: string
  baseUrl: string
  androidFingerprints: string
}

function DomainAliasesSection({ domainId }: { domainId: string }) {
  const { t } = useTranslation()
  const queryClient = useQueryClient()
  const [isAdding, setIsAdding] = useState(false)
  const [editingAliasId, setEditingAliasId] = useState<string | null>(null)
  const [deleteAlias, setDeleteAlias] = useState<AdminDomainAliasDto | null>(null)

  const { data: aliases, isLoading } = useListDomainAliases(domainId)

  const createMutation = useCreateDomainAlias()
  const updateMutation = useUpdateDomainAlias()
  const deleteMutation = useDeleteDomainAlias()
  const toggleMutation = useToggleDomainAliasActive()

  const emptyValues: AliasFormValues = {
    hostname: '',
    teamSlug: '',
    name: '',
    baseUrl: '',
    androidFingerprints: '',
  }
  const addForm = useForm<AliasFormValues>({ initialValues: emptyValues })
  const editForm = useForm<AliasFormValues>({ initialValues: emptyValues })

  const invalidate = () =>
    queryClient.invalidateQueries({ queryKey: getListDomainAliasesQueryKey(domainId) })

  const handleAdd = async (values: AliasFormValues) => {
    await createMutation.mutateAsync(
      {
        domainId,
        data: {
          hostname: values.hostname,
          teamSlug: values.teamSlug,
          name: values.name,
          baseUrl: values.baseUrl,
          androidFingerprints: values.androidFingerprints || undefined,
        },
      },
      {
        onSuccess: () => {
          invalidate()
          notifications.show({
            message: t('admin.domains.aliases.notifications.created'),
            color: 'green',
          })
          setIsAdding(false)
          addForm.reset()
        },
      }
    )
  }

  const handleUpdate = async (aliasId: string, values: AliasFormValues) => {
    await updateMutation.mutateAsync(
      {
        domainId,
        aliasId,
        data: {
          teamSlug: values.teamSlug,
          name: values.name,
          baseUrl: values.baseUrl,
          androidFingerprints: values.androidFingerprints || undefined,
        },
      },
      {
        onSuccess: () => {
          invalidate()
          notifications.show({
            message: t('admin.domains.aliases.notifications.updated'),
            color: 'green',
          })
          setEditingAliasId(null)
        },
      }
    )
  }

  const handleDelete = async () => {
    if (!deleteAlias) return
    await deleteMutation.mutateAsync(
      { domainId, aliasId: deleteAlias.id },
      {
        onSuccess: () => {
          invalidate()
          notifications.show({
            message: t('admin.domains.aliases.notifications.deleted'),
            color: 'green',
          })
          setDeleteAlias(null)
        },
      }
    )
  }

  const handleToggle = (aliasId: string) => {
    toggleMutation.mutate({ domainId, aliasId }, { onSuccess: invalidate })
  }

  const startEdit = (alias: AdminDomainAliasDto) => {
    editForm.setValues({
      hostname: alias.hostname,
      teamSlug: alias.pinnedTeamSlug,
      name: alias.name,
      baseUrl: alias.baseUrl,
      androidFingerprints: alias.androidFingerprints ?? '',
    })
    setEditingAliasId(alias.id)
  }

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
        <Text fw={500}>{t('admin.domains.aliases.title')}</Text>
        {!isAdding && (
          <Button
            size="xs"
            variant="light"
            leftSection={<IconPlus size={14} />}
            onClick={() => setIsAdding(true)}
          >
            {t('admin.domains.aliases.add')}
          </Button>
        )}
      </Group>

      <Text size="xs" c="dimmed">
        {t('admin.domains.aliases.description')}
      </Text>

      {isAdding && (
        <Paper withBorder p="sm">
          <Stack gap="sm">
            <TextInput
              label={t('admin.domains.aliases.hostname')}
              placeholder="myteam.fr"
              required
              {...addForm.getInputProps('hostname')}
            />
            <TextInput
              label={t('admin.domains.aliases.teamSlug')}
              required
              {...addForm.getInputProps('teamSlug')}
            />
            <TextInput
              label={t('admin.domains.aliases.name')}
              required
              {...addForm.getInputProps('name')}
            />
            <TextInput
              label={t('admin.domains.aliases.baseUrl')}
              placeholder="https://myteam.fr"
              required
              {...addForm.getInputProps('baseUrl')}
            />
            <Textarea
              label={t('admin.domains.androidFingerprints')}
              description={t('admin.domains.androidFingerprintsDescription')}
              placeholder={t('admin.domains.androidFingerprintsPlaceholder')}
              autosize
              minRows={2}
              maxRows={4}
              {...addForm.getInputProps('androidFingerprints')}
            />
            <Group justify="flex-end">
              <Button
                size="xs"
                variant="default"
                type="button"
                onClick={() => {
                  setIsAdding(false)
                  addForm.reset()
                }}
              >
                {t('actions.cancelAction')}
              </Button>
              <Button
                size="xs"
                type="button"
                loading={createMutation.isPending}
                onClick={() => addForm.onSubmit(handleAdd)()}
              >
                {t('actions.save')}
              </Button>
            </Group>
          </Stack>
        </Paper>
      )}

      {aliases && aliases.length > 0 ? (
        <Table.ScrollContainer minWidth={500}>
          <Table striped highlightOnHover>
            <Table.Thead>
              <Table.Tr>
                <Table.Th>{t('admin.domains.aliases.hostname')}</Table.Th>
                <Table.Th>{t('admin.domains.aliases.teamSlug')}</Table.Th>
                <Table.Th ta="center">{t('admin.domains.status')}</Table.Th>
                <Table.Th ta="center">{t('admin.domains.actions')}</Table.Th>
              </Table.Tr>
            </Table.Thead>
            <Table.Tbody>
              {aliases.map((alias) =>
                editingAliasId === alias.id ? (
                  <Table.Tr key={alias.id}>
                    <Table.Td colSpan={4}>
                      <Stack gap="xs">
                        <Text size="sm" fw={500}>
                          {alias.hostname}
                        </Text>
                        <Group grow>
                          <TextInput
                            label={t('admin.domains.aliases.teamSlug')}
                            size="xs"
                            required
                            {...editForm.getInputProps('teamSlug')}
                          />
                          <TextInput
                            label={t('admin.domains.aliases.name')}
                            size="xs"
                            required
                            {...editForm.getInputProps('name')}
                          />
                        </Group>
                        <TextInput
                          label={t('admin.domains.aliases.baseUrl')}
                          size="xs"
                          required
                          {...editForm.getInputProps('baseUrl')}
                        />
                        <Textarea
                          label={t('admin.domains.androidFingerprints')}
                          size="xs"
                          autosize
                          minRows={2}
                          maxRows={4}
                          {...editForm.getInputProps('androidFingerprints')}
                        />
                        <Group justify="flex-end">
                          <ActionIcon
                            variant="subtle"
                            color="gray"
                            type="button"
                            onClick={() => setEditingAliasId(null)}
                          >
                            <IconX size={16} />
                          </ActionIcon>
                          <ActionIcon
                            variant="subtle"
                            color="green"
                            type="button"
                            loading={updateMutation.isPending}
                            onClick={() =>
                              editForm.onSubmit((values) => handleUpdate(alias.id, values))()
                            }
                          >
                            <IconCheck size={16} />
                          </ActionIcon>
                        </Group>
                      </Stack>
                    </Table.Td>
                  </Table.Tr>
                ) : (
                  <Table.Tr key={alias.id}>
                    <Table.Td>
                      <Text size="sm">{alias.hostname}</Text>
                    </Table.Td>
                    <Table.Td>
                      <Text size="sm" c="dimmed">
                        {alias.pinnedTeamSlug}
                      </Text>
                    </Table.Td>
                    <Table.Td ta="center">
                      <Badge color={alias.active ? 'green' : 'gray'} size="sm">
                        {alias.active ? t('admin.status.active') : t('admin.status.inactive')}
                      </Badge>
                    </Table.Td>
                    <Table.Td ta="center">
                      <Group gap="xs" justify="center">
                        <Tooltip label={t('actions.edit')}>
                          <ActionIcon variant="subtle" onClick={() => startEdit(alias)}>
                            <IconPencil size={16} />
                          </ActionIcon>
                        </Tooltip>
                        <Tooltip
                          label={
                            alias.active
                              ? t('admin.domains.deactivate')
                              : t('admin.domains.activate')
                          }
                        >
                          <ActionIcon
                            variant="subtle"
                            loading={toggleMutation.isPending}
                            onClick={() => handleToggle(alias.id)}
                          >
                            {alias.active ? (
                              <IconToggleRight size={18} />
                            ) : (
                              <IconToggleLeft size={18} />
                            )}
                          </ActionIcon>
                        </Tooltip>
                        <Tooltip label={t('actions.delete')}>
                          <ActionIcon
                            variant="subtle"
                            color="red"
                            onClick={() => setDeleteAlias(alias)}
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
        !isAdding && (
          <Alert color="gray" variant="light">
            {t('admin.domains.aliases.empty')}
          </Alert>
        )
      )}

      <ConfirmDialog
        isOpen={!!deleteAlias}
        onClose={() => setDeleteAlias(null)}
        onConfirm={handleDelete}
        title={t('admin.domains.aliases.deleteConfirm.title')}
        message={t('admin.domains.aliases.deleteConfirm.message')}
        variant="danger"
        isLoading={deleteMutation.isPending}
      />
    </Stack>
  )
}
