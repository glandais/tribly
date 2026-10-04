/** A team's fallback avatar, when it has no logo: its initials, on a colour drawn from its name. */

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

export function getColorFromName(name: string): string {
  const hash = hashString(name)
  return colors[hash % colors.length]
}

export function getInitials(name: string): string {
  const words = name.trim().split(/\s+/)
  if (words.length === 0) return '?'
  if (words.length === 1) {
    return words[0].charAt(0).toUpperCase()
  }
  // Return first letter of first and second word
  return (words[0].charAt(0) + words[1].charAt(0)).toUpperCase()
}
