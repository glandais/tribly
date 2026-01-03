import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'

interface RangeInputProps {
  label: string
  minValue: number | undefined
  maxValue: number | undefined
  onMinChange: (value: number | undefined) => void
  onMaxChange: (value: number | undefined) => void
  minPlaceholder?: string
  maxPlaceholder?: string
  unit?: string
  step?: number
  displayMultiplier?: number // e.g., 0.001 for meters -> km
  className?: string
}

export function RangeInput({
  label,
  minValue,
  maxValue,
  onMinChange,
  onMaxChange,
  minPlaceholder = 'Min',
  maxPlaceholder = 'Max',
  unit,
  step = 1,
  displayMultiplier = 1,
  className = '',
}: RangeInputProps) {
  // Convert API value (meters) to display value (km)
  const toDisplay = (val: number | undefined) =>
    val !== undefined ? val * displayMultiplier : undefined

  // Convert display value back to API value
  const fromDisplay = (val: string) => {
    const num = parseFloat(val)
    return isNaN(num) ? undefined : num / displayMultiplier
  }

  return (
    <div className={className}>
      <Label className="text-sm font-medium text-gray-700 mb-2 block">
        {label} {unit && <span className="text-gray-500">({unit})</span>}
      </Label>
      <div className="flex items-center gap-2">
        <Input
          type="number"
          placeholder={minPlaceholder}
          value={toDisplay(minValue) ?? ''}
          onChange={(e) => onMinChange(fromDisplay(e.target.value))}
          step={step}
          min={0}
          className="w-24"
        />
        <span className="text-gray-400">-</span>
        <Input
          type="number"
          placeholder={maxPlaceholder}
          value={toDisplay(maxValue) ?? ''}
          onChange={(e) => onMaxChange(fromDisplay(e.target.value))}
          step={step}
          min={0}
          className="w-24"
        />
      </div>
    </div>
  )
}
