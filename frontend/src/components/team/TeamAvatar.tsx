import { MediaDto } from '@/api/dto'

// Size configurations for the avatar
const sizeClasses = {
  xs: 'h-6 w-6 text-xs',
  sm: 'h-8 w-8 text-sm',
  md: 'h-10 w-10 text-base',
  lg: 'h-12 w-12 text-lg',
  xl: 'h-16 w-16 text-xl',
} as const

// Generate a consistent color based on team name hash
const colors = [
  'bg-red-500',
  'bg-orange-500',
  'bg-amber-500',
  'bg-yellow-500',
  'bg-lime-500',
  'bg-green-500',
  'bg-emerald-500',
  'bg-teal-500',
  'bg-cyan-500',
  'bg-sky-500',
  'bg-blue-500',
  'bg-indigo-500',
  'bg-violet-500',
  'bg-purple-500',
  'bg-fuchsia-500',
  'bg-pink-500',
  'bg-rose-500',
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
  size?: keyof typeof sizeClasses
  className?: string
}

/**
 * TeamAvatar component - displays team logo or initials fallback.
 * Uses consistent hash-based colors for teams without logos.
 */
export function TeamAvatar({ team, size = 'md', className = '' }: TeamAvatarProps) {
  const logo = team.about.assets.logo
  const sizeClass = sizeClasses[size]

  if (logo?.url) {
    return (
      <img
        src={logo.url}
        alt={team.name}
        className={`${sizeClass} object-cover rounded-full ${className}`}
      />
    )
  }

  // Fallback: initials in colored circle
  const initials = getInitials(team.name)
  const colorClass = getColorFromName(team.name)

  return (
    <div
      className={`${sizeClass} ${colorClass} rounded-full flex items-center justify-center text-white font-semibold ${className}`}
      title={team.name}
    >
      {initials}
    </div>
  )
}
