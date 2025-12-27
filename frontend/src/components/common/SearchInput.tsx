import { MagnifyingGlassIcon } from '@heroicons/react/24/outline'

export interface SearchInputProps {
  value: string
  onChange: (value: string) => void
  placeholder: string
  label?: string
  id?: string
  className?: string
  fullWidth?: boolean
}

export function SearchInput({
  value,
  onChange,
  placeholder,
  label,
  id = 'search',
  className = '',
  fullWidth = false,
}: SearchInputProps) {
  const wrapperClasses = fullWidth ? 'relative' : 'relative w-full sm:max-w-md'

  return (
    <div className={className}>
      {label && (
        <label htmlFor={id} className="sr-only">
          {label}
        </label>
      )}
      <div className={wrapperClasses}>
        <input
          id={id}
          type="search"
          placeholder={placeholder}
          value={value}
          onChange={(e) => onChange(e.target.value)}
          className="w-full px-4 py-2 pl-10 border border-gray-300 rounded-lg text-gray-900 placeholder-gray-500 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-indigo-500 focus-visible:border-indigo-500"
          aria-label={label || placeholder}
        />
        <MagnifyingGlassIcon
          className="absolute left-3 top-2.5 h-5 w-5 text-gray-400 pointer-events-none"
          aria-hidden="true"
        />
      </div>
    </div>
  )
}
