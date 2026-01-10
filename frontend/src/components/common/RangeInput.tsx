import { NumberInput, Group, Text, Stack } from '@mantine/core'

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
  displayMultiplier?: number
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
}: RangeInputProps) {
  // Convert API value (meters) to display value (km)
  const toDisplay = (val: number | undefined) =>
    val !== undefined ? val * displayMultiplier : undefined

  // Convert display value back to API value
  const fromDisplay = (val: string | number) => {
    if (val === '' || val === undefined) return undefined
    const num = typeof val === 'number' ? val : parseFloat(val)
    return isNaN(num) ? undefined : num / displayMultiplier
  }

  return (
    <Stack gap="xs">
      <Text size="sm" fw={500}>
        {label}{' '}
        {unit && (
          <Text span c="dimmed">
            ({unit})
          </Text>
        )}
      </Text>
      <Group gap="xs">
        <NumberInput
          placeholder={minPlaceholder}
          value={toDisplay(minValue) ?? ''}
          onChange={(val) => onMinChange(fromDisplay(val))}
          step={step}
          min={0}
          w={100}
        />
        <Text c="dimmed">-</Text>
        <NumberInput
          placeholder={maxPlaceholder}
          value={toDisplay(maxValue) ?? ''}
          onChange={(val) => onMaxChange(fromDisplay(val))}
          step={step}
          min={0}
          w={100}
        />
      </Group>
    </Stack>
  )
}
