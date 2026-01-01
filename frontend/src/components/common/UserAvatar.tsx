import type { UserDto } from '../../api/api'

interface UserAvatarProps {
  user: Pick<UserDto, 'displayName' | 'avatarUrl'>
  size?: 'xs' | 'sm' | 'md' | 'lg' | 'xl'
  className?: string
}

const sizeClasses = {
  xs: 'h-6 w-6 text-xs',
  sm: 'h-8 w-8 text-sm',
  md: 'h-10 w-10 text-base',
  lg: 'h-12 w-12 text-lg',
  xl: 'h-16 w-16 text-xl',
}

export function UserAvatar({ user, size = 'md', className = '' }: UserAvatarProps) {
  const initials = user.displayName
    .split(' ')
    .map((n) => n[0])
    .join('')
    .toUpperCase()
    .slice(0, 2)

  if (user.avatarUrl) {
    return (
      <img
        src={user.avatarUrl}
        alt={user.displayName}
        className={`${sizeClasses[size]} rounded-full object-cover ${className}`}
      />
    )
  }

  return (
    <div
      className={`${sizeClasses[size]} rounded-full bg-indigo-500 flex items-center justify-center text-white font-medium ${className}`}
    >
      {initials}
    </div>
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
    <div className="flex -space-x-2">
      {visibleUsers.map((user) => (
        <div key={user.id} className="relative">
          <UserAvatar user={user} size={size} className="ring-2 ring-white" />
        </div>
      ))}
      {remainingCount > 0 && (
        <div
          className={`${sizeClasses[size]} rounded-full bg-gray-200 flex items-center justify-center text-gray-600 font-medium ring-2 ring-white`}
        >
          +{remainingCount}
        </div>
      )}
    </div>
  )
}
