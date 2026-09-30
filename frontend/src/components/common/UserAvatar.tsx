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
      <Avatar src={user.avatarUrl} alt={user.displayName} size={size} radius="xl" color="primary">
        {initials}
      </Avatar>
    </Tooltip>
  )
}

interface UserAvatarGroupProps {
  users: Array<Pick<UserDto, 'id' | 'displayName' | 'avatarUrl'>>
  max?: number
  size?: 'xs' | 'sm' | 'md'
  /**
   * Everyone the avatars stand for, when `users` is only a preview of them (a ride group or a trip
   * embeds its first few participants): the « +N » counts from it rather than from `users`.
   */
  total?: number
}

export function UserAvatarGroup({ users, max = 5, size = 'sm', total }: UserAvatarGroupProps) {
  const visibleUsers = users.slice(0, max)
  const remainingCount = (total ?? users.length) - visibleUsers.length

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
