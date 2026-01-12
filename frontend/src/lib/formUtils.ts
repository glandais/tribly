import type { FormErrors } from '@mantine/form'
import type { $ZodType } from 'zod/v4/core'
import { zod4Resolver } from 'mantine-form-zod-resolver'

interface ZodResolverOptions {
  errorPriority?: 'first' | 'last'
}

/**
 * Typed wrapper for zod4Resolver that properly matches Mantine's expected validator type.
 * This resolves the type mismatch between zod4Resolver's return type and useForm's validate option.
 */
export function zodFormValidator<T extends object>(
  schema: $ZodType,
  options?: ZodResolverOptions
): (values: T) => FormErrors {
  return zod4Resolver(schema, options) as (values: T) => FormErrors
}
