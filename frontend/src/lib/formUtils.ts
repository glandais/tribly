import type { FormErrors } from '@mantine/form'
import { schemaResolver } from '@mantine/form'
import type { $ZodType } from 'zod/v4/core'

/**
 * Typed wrapper for schemaResolver that properly matches Mantine's expected validator type.
 */
export function zodFormValidator<T extends object>(schema: $ZodType): (values: T) => FormErrors {
  return schemaResolver(schema, { sync: true }) as (values: T) => FormErrors
}
