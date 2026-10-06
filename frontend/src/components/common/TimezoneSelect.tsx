import { useMemo } from 'react'
import { useTranslation } from 'react-i18next'
import { Select } from '@mantine/core'
import { TIMEZONE_OPTIONS } from '@/utils/timezones'
import { zoneCityName } from '@/utils/zoneLabel'

interface TimezoneSelectProps {
  label: string
  description?: string
  value: string | null | undefined
  onChange: (value: string | null) => void
  disabled?: boolean
  error?: React.ReactNode
}

/**
 * A searchable list of IANA zones, each named by its city as the « heure de Tokyo » mention names
 * it (docs/LEDGER_*.md API-60, plan §7), with its identifier beside it to tell apart two cities of
 * the same name. A value the browser does not list — an alias the JDK accepts, set by a migration —
 * is kept at the top, or the field would show empty.
 */
export function TimezoneSelect({
  label,
  description,
  value,
  onChange,
  disabled,
  error,
}: TimezoneSelectProps) {
  const { i18n } = useTranslation()
  const language = i18n.language
  const data = useMemo(() => {
    const zones =
      value && !TIMEZONE_OPTIONS.includes(value) ? [value, ...TIMEZONE_OPTIONS] : TIMEZONE_OPTIONS
    return zones.map((zone) => ({
      value: zone,
      label: `${zoneCityName(zone, language)} · ${zone}`,
    }))
  }, [value, language])

  return (
    <Select
      label={label}
      description={description}
      searchable
      value={value ?? null}
      onChange={onChange}
      disabled={disabled}
      error={error}
      data={data}
      maxDropdownHeight={280}
    />
  )
}
