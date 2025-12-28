import { Link } from 'react-router-dom'
import { ReactNode } from 'react'
import { MarkdownDisplay } from '../MarkdownDisplay'
import { MediaDto } from '@/api'

interface CardProps {
  to: string
  children: ReactNode
  className?: string
}

export function Card({ to, children, className = '' }: CardProps) {
  return (
    <Link
      to={to}
      className={`block bg-white rounded-lg shadow-xs border border-gray-200 hover:shadow-md hover:border-gray-300 transition-all ${className}`}
    >
      {children}
    </Link>
  )
}

interface CardContentProps {
  children: ReactNode
  className?: string
}

export function CardContent({ children, className = '' }: CardContentProps) {
  return <div className={`p-4 ${className}`}>{children}</div>
}

interface CardHeaderProps {
  children: ReactNode
  className?: string
}

export function CardHeader({ children, className = '' }: CardHeaderProps) {
  return <div className={`flex items-start justify-between ${className}`}>{children}</div>
}

interface CardTitleProps {
  children: ReactNode
  truncate?: boolean
  className?: string
}

export function CardTitle({ children, truncate = false, className = '' }: CardTitleProps) {
  return (
    <h3
      className={`text-lg font-semibold text-gray-900 ${truncate ? 'truncate' : ''} ${className}`}
    >
      {children}
    </h3>
  )
}

interface CardDescriptionProps {
  media: MediaDto
  markdown?: boolean
  maxLength?: number
  className?: string
}

export function CardDescription({
  media,
  markdown = false,
  maxLength = 150,
  className = '',
}: CardDescriptionProps) {
  // Markdown preview mode
  if (markdown && media.markdown) {
    return (
      <MarkdownDisplay
        markdown={media.markdown || ''}
        preview={true}
        maxLength={maxLength}
        className={`text-sm text-gray-600 ${className}`}
      />
    )
  }

  return <p></p>
}
