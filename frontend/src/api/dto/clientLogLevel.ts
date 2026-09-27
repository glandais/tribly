export type ClientLogLevel = (typeof ClientLogLevel)[keyof typeof ClientLogLevel]

export const ClientLogLevel = {
  DEBUG: 'DEBUG',
  INFO: 'INFO',
  WARN: 'WARN',
  ERROR: 'ERROR',
} as const
