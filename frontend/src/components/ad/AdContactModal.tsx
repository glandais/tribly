import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useForm } from '@mantine/form'
import { Alert, Button, Group, Modal, Stack, Text, Textarea } from '@mantine/core'
import { notifications } from '@mantine/notifications'
import { IconInfoCircle } from '@tabler/icons-react'
import {
  contactAdAuthorBodyMessageMax,
  contactAdAuthorBodyMessageMin,
  contactAdAuthorBodyMessageRegExp,
} from '@/api/zod/ads/ads.zod'
import { useContactAdAuthor } from '@/api/endpoints/ads/ads'
import { ApiClientError } from '@/lib/apiError'

interface AdContactModalProps {
  opened: boolean
  onClose: () => void
  teamSlug: string
  adSlug: string
}

interface Failure {
  color: 'orange' | 'red'
  message: string
}

/**
 * Writes to an ad's author through the server-side email relay.
 *
 * No address is involved on either side: the server sends the mail and sets `Reply-To` to the
 * sender, so the author can answer directly while neither address ever reaches the API or the
 * DOM. There is deliberately nothing here to display as a contact address.
 */
export function AdContactModal({ opened, onClose, teamSlug, adSlug }: AdContactModalProps) {
  const { t } = useTranslation()
  const [failure, setFailure] = useState<Failure | null>(null)
  const mutation = useContactAdAuthor()

  const form = useForm({
    initialValues: { message: '' },
    validate: {
      message: (value) => {
        if (value.trim().length === 0 || !contactAdAuthorBodyMessageRegExp.test(value)) {
          return t('ads.contact.error.blank')
        }
        if (value.length < contactAdAuthorBodyMessageMin) return t('ads.contact.error.tooShort')
        if (value.length > contactAdAuthorBodyMessageMax) return t('ads.contact.error.tooLong')
        return null
      },
    },
  })

  const length = form.values.message.length
  const outOfBounds =
    length < contactAdAuthorBodyMessageMin || length > contactAdAuthorBodyMessageMax

  // `AD_CONTACT_OPTED_OUT` is the one failure that cannot be retried by trying again.
  const optedOut = failure !== null && failure.message === t('ads.contact.error.optedOut')

  const describe = (error: unknown): Failure => {
    if (!(error instanceof ApiClientError)) {
      return { color: 'red', message: t('ads.contact.error.generic') }
    }
    switch (error.error.code) {
      case 'AD_CONTACT_OPTED_OUT':
        return { color: 'orange', message: t('ads.contact.error.optedOut') }
      case 'AD_CONTACT_RATE_LIMITED':
        return {
          color: 'orange',
          message: error.retryAfterSeconds
            ? t('ads.contact.error.rateLimitedIn', {
                minutes: Math.max(1, Math.ceil(error.retryAfterSeconds / 60)),
              })
            : t('ads.contact.error.rateLimited'),
        }
      case 'AD_CONTACT_DELIVERY_FAILED':
        return { color: 'red', message: t('ads.contact.error.deliveryFailed') }
      default:
        return { color: 'red', message: t('ads.contact.error.generic') }
    }
  }

  const handleSubmit = form.onSubmit((values) => {
    setFailure(null)
    mutation.mutate(
      { teamSlug, slug: adSlug, data: { message: values.message } },
      {
        onSuccess: () => {
          notifications.show({ message: t('ads.contact.success'), color: 'green' })
          form.reset()
          onClose()
        },
        // The draft is deliberately kept on every failure: a quota or a delivery outage is
        // not the sender's fault, and losing what they wrote would be.
        onError: (error) => setFailure(describe(error)),
      }
    )
  })

  const handleClose = () => {
    setFailure(null)
    onClose()
  }

  return (
    <Modal opened={opened} onClose={handleClose} title={t('ads.contact.title')}>
      <form onSubmit={handleSubmit}>
        <Stack>
          <Alert variant="light" icon={<IconInfoCircle size={16} />}>
            {t('ads.contact.disclosure')}
          </Alert>

          {failure && <Alert color={failure.color}>{failure.message}</Alert>}

          <Stack gap={4}>
            <Textarea
              autosize
              minRows={4}
              maxRows={12}
              label={t('ads.contact.messageLabel')}
              placeholder={t('ads.contact.placeholder')}
              {...form.getInputProps('message')}
            />
            <Text size="xs" c="dimmed" ta="right">
              {t('ads.contact.counter', { count: length, max: contactAdAuthorBodyMessageMax })}
            </Text>
          </Stack>

          <Group justify="flex-end">
            <Button variant="default" onClick={handleClose}>
              {t('actions.cancel')}
            </Button>
            <Button type="submit" loading={mutation.isPending} disabled={outOfBounds || optedOut}>
              {t('ads.contact.send')}
            </Button>
          </Group>
        </Stack>
      </form>
    </Modal>
  )
}
