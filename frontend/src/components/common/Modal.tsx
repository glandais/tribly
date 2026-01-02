import { type ReactNode } from 'react'
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog'
import { cn } from '@/lib/utils'

type ModalSize = 'sm' | 'md' | 'lg' | 'xl' | '2xl' | '4xl' | 'full'

interface ModalProps {
  isOpen: boolean
  onClose: () => void
  title: string
  children: ReactNode
  size?: ModalSize
  footer?: ReactNode
  /** Content that appears below the header but doesn't scroll */
  subheader?: ReactNode
  /** Remove padding from content area */
  noPadding?: boolean
}

const sizeClasses: Record<ModalSize, string> = {
  sm: 'sm:max-w-sm',
  md: 'sm:max-w-md',
  lg: 'sm:max-w-lg',
  xl: 'sm:max-w-xl',
  '2xl': 'sm:max-w-2xl',
  '4xl': 'sm:max-w-4xl',
  full: 'sm:max-w-[95vw]',
}

export function Modal({
  isOpen,
  onClose,
  title,
  children,
  size = 'md',
  footer,
  subheader,
  noPadding = false,
}: ModalProps) {
  return (
    <Dialog open={isOpen} onOpenChange={(open) => !open && onClose()}>
      <DialogContent className={cn('flex max-h-[90vh] flex-col gap-0 p-0', sizeClasses[size])}>
        {/* Header */}
        <DialogHeader className="shrink-0 border-b p-6">
          <DialogTitle>{title}</DialogTitle>
        </DialogHeader>

        {/* Subheader */}
        {subheader && <div className="shrink-0 border-b p-6">{subheader}</div>}

        {/* Content */}
        <div className={cn('flex-1 overflow-y-auto', !noPadding && 'p-6')}>{children}</div>

        {/* Footer */}
        {footer && <DialogFooter className="shrink-0 border-t p-6">{footer}</DialogFooter>}
      </DialogContent>
    </Dialog>
  )
}
