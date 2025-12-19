import { Link } from 'react-router-dom'
import { ReactNode } from 'react'

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
  children: ReactNode
  className?: string
}

export function CardDescription({ children, className = '' }: CardDescriptionProps) {
  return <p className={`text-sm text-gray-600 line-clamp-2 ${className}`}>{children}</p>
}
