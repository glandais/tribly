import { Toaster as Sonner, type ToasterProps } from 'sonner'

const Toaster = ({ ...props }: ToasterProps) => {
  return (
    <Sonner
      className="toaster group"
      position="top-right"
      toastOptions={{
        classNames: {
          toast:
            'group toast group-[.toaster]:bg-white group-[.toaster]:text-gray-900 group-[.toaster]:border-gray-200 group-[.toaster]:shadow-lg',
          description: 'group-[.toast]:text-gray-500',
          actionButton: 'group-[.toast]:bg-indigo-600 group-[.toast]:text-white',
          cancelButton: 'group-[.toast]:bg-gray-100 group-[.toast]:text-gray-500',
          success: 'group-[.toaster]:border-green-200 group-[.toaster]:bg-green-50',
          error: 'group-[.toaster]:border-red-200 group-[.toaster]:bg-red-50',
          warning: 'group-[.toaster]:border-yellow-200 group-[.toaster]:bg-yellow-50',
          info: 'group-[.toaster]:border-blue-200 group-[.toaster]:bg-blue-50',
        },
      }}
      {...props}
    />
  )
}

export { Toaster }
