import { ArrowPathIcon } from '@heroicons/react/24/outline'

interface LoadingSpinnerProps {
  size?: 'sm' | 'md' | 'lg'
  color?: 'primary' | 'white' | 'gray'
  className?: string
}

const sizeClasses = {
  sm: 'h-4 w-4',
  md: 'h-8 w-8',
  lg: 'h-12 w-12',
}

const colorClasses = {
  primary: 'text-indigo-600',
  white: 'text-white',
  gray: 'text-gray-400',
}

export function LoadingSpinner({
  size = 'md',
  color = 'primary',
  className = '',
}: LoadingSpinnerProps) {
  return (
    <ArrowPathIcon
      className={`animate-spin ${sizeClasses[size]} ${colorClasses[color]} ${className}`}
      aria-hidden="true"
    />
  )
}

interface LoadingPageProps {
  message: string
}

export function LoadingPage({ message }: LoadingPageProps) {
  return (
    <div className="flex flex-col items-center justify-center min-h-[60vh]">
      <LoadingSpinner size="lg" />
      <p className="mt-4 text-gray-600">{message}</p>
    </div>
  )
}

interface LoadingOverlayProps {
  isLoading: boolean
  children: React.ReactNode
}

export function LoadingOverlay({ isLoading, children }: LoadingOverlayProps) {
  return (
    <div className="relative">
      {children}
      {isLoading && (
        <div className="absolute inset-0 bg-white bg-opacity-75 flex items-center justify-center z-10">
          <LoadingSpinner size="lg" />
        </div>
      )}
    </div>
  )
}
