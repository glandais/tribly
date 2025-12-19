import { ReactNode } from 'react'

interface StatProps {
  icon: ReactNode
  children: ReactNode
  className?: string
}

export function Stat({ icon, children, className = '' }: StatProps) {
  return (
    <span className={`flex items-center text-sm text-gray-500 ${className}`}>
      <span className="w-4 h-4 mr-1">{icon}</span>
      {children}
    </span>
  )
}

interface StatGroupProps {
  children: ReactNode
  className?: string
}

export function StatGroup({ children, className = '' }: StatGroupProps) {
  return <div className={`flex items-center gap-4 ${className}`}>{children}</div>
}
