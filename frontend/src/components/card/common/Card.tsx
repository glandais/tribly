import { Link } from 'react-router-dom'
import { ReactNode } from 'react'
import { Paper, Title, Box, Text } from '@mantine/core'
import { MarkdownDisplay } from '../../common/MarkdownDisplay'
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
  return (
    <Box p="md" style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
      {children}
    </Box>
  )
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
  /**
   * Server-computed plain-text opening of the body. Present whatever the list `view`, so a card
   * reads it first and never has to carry `media.markdown` just to show two lines.
   */
  excerpt?: string
  markdown?: boolean
  maxLength?: number
}

export function CardDescription({
  media,
  excerpt,
  markdown = false,
  maxLength = 150,
}: CardDescriptionProps) {
  if (excerpt && excerpt.trim().length > 0) {
    return (
      <Text size="sm" c="dimmed" lineClamp={2}>
        {excerpt}
      </Text>
    )
  }

  // Fallback for payloads that carry no excerpt (older rows, or an entity whose excerpt is null
  // while the body still holds renderable content).
  if (markdown && media.markdown) {
    return <MarkdownDisplay markdown={media.markdown} preview={true} maxLength={maxLength} />
  }

  return null
}
