import { useNavigate } from 'react-router-dom'
import { Text, UnstyledButton } from '@mantine/core'
import { IconUsers, IconChevronRight } from '@tabler/icons-react'
import { paths } from '@/config/paths'

interface CardTeamLinkProps {
  teamSlug: string
  teamName: string
}

export function CardTeamLink({ teamSlug, teamName }: CardTeamLinkProps) {
  const navigate = useNavigate()

  return (
    <UnstyledButton
      onClick={(e) => {
        e.preventDefault()
        e.stopPropagation()
        navigate(paths.team(teamSlug))
      }}
      mb="sm"
      styles={{
        root: {
          display: 'flex',
          alignItems: 'center',
          gap: 'var(--mantine-spacing-xs)',
          '&:hover': {
            textDecoration: 'underline',
          },
        },
      }}
    >
      <IconUsers size={16} color="var(--mantine-color-dimmed)" />
      <Text size="sm" c="dimmed" fw={500}>
        {teamName}
      </Text>
      <IconChevronRight size={12} color="var(--mantine-color-dimmed)" />
    </UnstyledButton>
  )
}
