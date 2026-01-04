import * as React from 'react'
import { Input } from './input'
import { toDateTimeLocalValue, fromDateTimeLocalValue } from '@/utils/dateFormat'

interface InputDateTimeProps
  extends Omit<React.ComponentProps<'input'>, 'type' | 'value' | 'onChange'> {
  /** ISO datetime string (e.g., "2024-01-04T12:00:00.000Z") */
  value?: string
  /** Called with ISO datetime string */
  onChange?: (value: string | undefined) => void
}

/**
 * DateTime input that accepts and returns ISO format strings.
 * Internally uses datetime-local input with timezone conversion.
 */
function InputDateTime({ value, onChange, ...props }: InputDateTimeProps) {
  // Convert ISO to datetime-local format for display
  const displayValue = value ? toDateTimeLocalValue(value) : undefined

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const localValue = e.target.value
    if (onChange) {
      if (localValue) {
        // Convert datetime-local to ISO format
        const isoString = fromDateTimeLocalValue(localValue).toISOString()
        onChange(isoString)
      } else {
        onChange(undefined)
      }
    }
  }

  return (
    <Input type="datetime-local" value={displayValue} onChange={handleChange} {...props} />
  )
}

export { InputDateTime }
