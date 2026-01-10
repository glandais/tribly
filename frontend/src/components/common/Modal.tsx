import { type ReactNode } from 'react'
import { Modal as MantineModal, Group, Title, ScrollArea } from '@mantine/core'

type ModalSize = 'sm' | 'md' | 'lg' | 'xl' | '2xl' | '4xl' | 'full'

interface ModalProps {
  isOpen: boolean
  onClose: () => void
  title: string
  children: ReactNode
  size?: ModalSize
  footer?: ReactNode
  subheader?: ReactNode
  noPadding?: boolean
}

const sizeMap: Record<ModalSize, string | number> = {
  sm: 'sm',
  md: 'md',
  lg: 'lg',
  xl: 'xl',
  '2xl': '42rem',
  '4xl': '56rem',
  full: '95vw',
}

export function Modal({
  isOpen,
  onClose,
  title,
  children,
  size = 'md',
  footer,
  subheader,
  noPadding = false,
}: ModalProps) {
  return (
    <MantineModal
      opened={isOpen}
      onClose={onClose}
      title={<Title order={3}>{title}</Title>}
      size={sizeMap[size]}
      padding={0}
      styles={{
        body: { display: 'flex', flexDirection: 'column', maxHeight: 'calc(90vh - 60px)' },
        header: {
          padding: 'var(--mantine-spacing-md)',
          borderBottom: '1px solid var(--mantine-color-default-border)',
        },
      }}
    >
      {subheader && (
        <div
          style={{
            padding: 'var(--mantine-spacing-md)',
            borderBottom: '1px solid var(--mantine-color-default-border)',
          }}
        >
          {subheader}
        </div>
      )}
      <ScrollArea style={{ flex: 1 }} p={noPadding ? 0 : 'md'}>
        {children}
      </ScrollArea>
      {footer && (
        <Group
          justify="flex-end"
          p="md"
          style={{ borderTop: '1px solid var(--mantine-color-default-border)' }}
        >
          {footer}
        </Group>
      )}
    </MantineModal>
  )
}
