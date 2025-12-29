import { useTranslation } from 'react-i18next'
import { CalendarIcon } from '@heroicons/react/24/outline'
import { Status } from '../../hooks/usePost'
import type { PostDto } from '../../hooks/usePost'
import { Card, CardContent, CardTitle, CardDescription } from '../common/card'
import { Badge, VisibilityBadge, Stat, StatGroup, CardSkeleton } from '../common/card'
import { EntityLogo } from '../common/EntityLogo'
import { useFormattedDate } from '../../utils/dateFormat'

interface PostCardProps {
  post: PostDto
  teamSlug: string
  showTypeBadge?: boolean
}

const statusVariants: Record<Status, 'gray' | 'green' | 'red' | 'blue'> = {
  [Status.Draft]: 'gray',
  [Status.Published]: 'green',
  [Status.Cancelled]: 'red',
}

export function PostCard({ post, teamSlug, showTypeBadge = false }: PostCardProps) {
  const { t } = useTranslation('posts')
  const { t: tCommon } = useTranslation('common')
  const { formatDateTime } = useFormattedDate()
  const formattedDate = formatDateTime(post.dateTime)

  const calendarIcon = <CalendarIcon />

  return (
    <Card to={`/teams/${teamSlug}/posts/${post.slug}`}>
      <CardContent>
        <div className="flex items-start justify-between mb-4">
          <div className="flex items-start gap-3 flex-1 min-w-0">
            <EntityLogo
              logo={post.media.assets.logo}
              alt={post.name}
              size="md"
              className="shrink-0"
            />
            <div className="flex-1 min-w-0">
              <CardTitle>{post.name}</CardTitle>
              <CardDescription markdown={true} media={post.media} />
            </div>
          </div>
          <div className="ml-3 flex flex-col items-end gap-1">
            {showTypeBadge && <Badge variant="purple">{tCommon('publicationType.post')}</Badge>}
            <Badge variant={statusVariants[post.status]}>{t(`status.${post.status}`)}</Badge>
            <VisibilityBadge visibility={post.visibility} />
          </div>
        </div>

        <StatGroup>
          <Stat icon={calendarIcon}>{formattedDate}</Stat>
        </StatGroup>
      </CardContent>
    </Card>
  )
}

export function PostCardSkeleton() {
  return <CardSkeleton count={1} statCount={1} badgeCount={2} />
}
