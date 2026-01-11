import { Link } from 'react-router-dom'
import { ReactNode } from 'react'
import { Paper, Title, Box } from '@mantine/core'
import { MarkdownDisplay } from '../MarkdownDisplay'
import { MediaDto } from '@/api/dto'

interface CardProps {
  to: string
  children: ReactNode
}

export function Card({ to, children }: CardProps) {
  return (
    <Paper
      component={Link}
      to={to}
      withBorder
      radius="md"
      style={{
        display: 'block',
        textDecoration: 'none',
        color: 'inherit',
        transition: 'box-shadow 0.2s, border-color 0.2s',
      }}
      styles={{
        root: {
          '&:hover': {
            boxShadow: 'var(--mantine-shadow-md)',
            borderColor: 'var(--mantine-color-default-border)',
          },
        },
      }}
    >
      {children}
    </Paper>
  )
}

interface CardContentProps {
  children: ReactNode
}

export function CardContent({ children }: CardContentProps) {
  return <Box p="md">{children}</Box>
}

interface CardHeaderProps {
  children: ReactNode
}

export function CardHeader({ children }: CardHeaderProps) {
  return (
    <Box style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between' }}>
      {children}
    </Box>
  )
}

interface CardTitleProps {
  children: ReactNode
  truncate?: boolean
}

export function CardTitle({ children, truncate = false }: CardTitleProps) {
  return (
    <Title order={4} lineClamp={truncate ? 1 : undefined}>
      {children}
    </Title>
  )
}

interface CardDescriptionProps {
  media: MediaDto
  markdown?: boolean
  maxLength?: number
}

export function CardDescription({
  media,
  markdown = false,
  maxLength = 150,
}: CardDescriptionProps) {
  if (markdown && media.markdown) {
    return <MarkdownDisplay markdown={media.markdown} preview={true} maxLength={maxLength} />
  }

  return null
}
