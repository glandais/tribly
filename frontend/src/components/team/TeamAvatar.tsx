import { Avatar, Tooltip } from '@mantine/core'
import { MediaDto } from '@/api/dto'

// Generate a consistent color based on team name hash
const colors = [
  'red',
  'orange',
  'yellow',
  'lime',
  'green',
  'teal',
  'cyan',
  'blue',
  'indigo',
  'violet',
  'grape',
  'pink',
]

function hashString(str: string): number {
  let hash = 0
  for (let i = 0; i < str.length; i++) {
    const char = str.charCodeAt(i)
    hash = (hash << 5) - hash + char
    hash = hash & hash // Convert to 32-bit integer
  }
  return Math.abs(hash)
}

function getColorFromName(name: string): string {
  const hash = hashString(name)
  return colors[hash % colors.length]
}

function getInitials(name: string): string {
  const words = name.trim().split(/\s+/)
  if (words.length === 0) return '?'
  if (words.length === 1) {
    return words[0].charAt(0).toUpperCase()
  }
  // Return first letter of first and second word
  return (words[0].charAt(0) + words[1].charAt(0)).toUpperCase()
}

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
      <Avatar src={logo?.url} alt={team.name} size={size} radius="xl" color={color}>
        {initials}
      </Avatar>
    </Tooltip>
  )
}
