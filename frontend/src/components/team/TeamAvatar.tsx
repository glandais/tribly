import { Avatar, Tooltip } from '@mantine/core'
import { MediaDto } from '@/api/dto'
import { getColorFromName, getInitials } from './teamInitials'

export interface TeamAvatarProps {
  team: {
    name: string
    about: MediaDto
  }
  size?: 'xs' | 'sm' | 'md' | 'lg' | 'xl'
}

/**
 * TeamAvatar component - displays team logo or initials fallback.
 * Uses consistent hash-based colors for teams without logos.
 */
export function TeamAvatar({ team, size = 'md' }: TeamAvatarProps) {
  const logo = team.about.assets.logo
  const initials = getInitials(team.name)
  const color = getColorFromName(team.name)

  return (
    <Tooltip label={team.name} withArrow>
      <Avatar
        src={logo?.imageUrl?.replace('{size}', String(128))}
        alt={team.name}
        size={size}
        radius="xl"
        color={color}
      >
        {initials}
      </Avatar>
    </Tooltip>
  )
}
