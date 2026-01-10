import { Avatar, Tooltip } from '@mantine/core'
import type { UserDto } from '@/api/dto'

interface UserAvatarProps {
  user: Pick<UserDto, 'displayName' | 'avatarUrl'>
  size?: 'xs' | 'sm' | 'md' | 'lg' | 'xl'
}

export function UserAvatar({ user, size = 'md' }: UserAvatarProps) {
  const initials = user.displayName
    .split(' ')
    .map((n) => n[0])
    .join('')
    .toUpperCase()
    .slice(0, 2)

  return (
    <Tooltip label={user.displayName} withArrow>
      <Avatar src={user.avatarUrl} alt={user.displayName} size={size} radius="xl" color="indigo">
        {initials}
      </Avatar>
    </Tooltip>
  )
}

interface UserAvatarGroupProps {
  users: Array<Pick<UserDto, 'id' | 'displayName' | 'avatarUrl'>>
  max?: number
  size?: 'xs' | 'sm' | 'md'
}

export function UserAvatarGroup({ users, max = 5, size = 'sm' }: UserAvatarGroupProps) {
  const visibleUsers = users.slice(0, max)
  const remainingCount = users.length - max

  return (
    <Avatar.Group spacing="sm">
      {visibleUsers.map((user) => (
        <UserAvatar key={user.id} user={user} size={size} />
      ))}
      {remainingCount > 0 && (
        <Avatar size={size} radius="xl" color="gray">
          +{remainingCount}
        </Avatar>
      )}
    </Avatar.Group>
  )
}
