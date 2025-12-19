interface CardSkeletonProps {
  count?: number
  hasImage?: boolean
  imageHeight?: string
  statCount?: number
  badgeCount?: number
}

export function CardSkeleton({
  count = 1,
  hasImage = false,
  imageHeight = 'h-32',
  statCount = 2,
  badgeCount = 3,
}: CardSkeletonProps) {
  return (
    <>
      {Array.from({ length: count }).map((_, i) => (
        <div key={i} className="bg-white rounded-lg shadow-xs border border-gray-200 animate-pulse">
          {hasImage && <div className={`w-full ${imageHeight} bg-gray-200 rounded-t-lg`} />}
          <div className="p-4">
            <div className="h-5 bg-gray-200 rounded-sm w-3/4 mb-2" />
            <div className="h-4 bg-gray-200 rounded-sm w-full mb-1" />
            <div className="h-4 bg-gray-200 rounded-sm w-2/3 mb-4" />

            {/* Stats */}
            {statCount > 0 && (
              <div className="flex gap-4 mb-3">
                {Array.from({ length: statCount }).map((_, j) => (
                  <div key={j} className="h-4 bg-gray-200 rounded-sm w-16" />
                ))}
              </div>
            )}

            {/* Badges */}
            {badgeCount > 0 && (
              <div className="flex gap-2">
                {Array.from({ length: badgeCount }).map((_, j) => (
                  <div key={j} className="h-5 bg-gray-200 rounded-full w-16" />
                ))}
              </div>
            )}
          </div>
        </div>
      ))}
    </>
  )
}
