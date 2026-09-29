import * as zod from 'zod'

/**
 * Files the member's report with the technical context their client attached. It reaches the maintainers as an issue of a private repository, naming the member by id only; tokens and e-mail addresses are redacted from the context and the log.
 * @summary Report a bug or suggest something
 */
export const sendFeedbackBodyMessageMin = 10
export const sendFeedbackBodyMessageMax = 5000

export const sendFeedbackBodyContextAppVersionMax = 50

export const sendFeedbackBodyContextAppVersionRegExp = new RegExp('\\S')
export const sendFeedbackBodyContextBuildNumberMax = 50

export const sendFeedbackBodyContextOsVersionMax = 100

export const sendFeedbackBodyContextDeviceMax = 200

export const sendFeedbackBodyContextUserAgentMax = 500

export const sendFeedbackBodyContextRouteMax = 500

export const sendFeedbackBodyContextLocaleMax = 20

export const sendFeedbackBodyContextTimezoneMax = 64

export const sendFeedbackBodyContextTeamSlugMax = 100

export const sendFeedbackBodyErrorTypeMax = 200

export const sendFeedbackBodyErrorTypeRegExp = new RegExp('\\S')
export const sendFeedbackBodyErrorMessageMax = 2000

export const sendFeedbackBodyErrorStackMax = 16000

export const sendFeedbackBodyLogsItemSourceMax = 50

export const sendFeedbackBodyLogsItemSourceRegExp = new RegExp('\\S')
export const sendFeedbackBodyLogsItemMessageMax = 1000

export const sendFeedbackBodyLogsMax = 200

export const SendFeedbackBody = zod
  .object({
    kind: zod.enum(['BUG', 'SUGGESTION']).describe('Bug or suggestion'),
    message: zod
      .string()
      .min(sendFeedbackBodyMessageMin)
      .max(sendFeedbackBodyMessageMax)
      .optional()
      .describe(
        "What happened, in the member's words. Required for a suggestion; optional for a bug, whose member may not know what went wrong — the context, error and log then speak for them."
      ),
    context: zod
      .object({
        platform: zod.enum(['WEB', 'ANDROID', 'IOS']).describe('The client'),
        appVersion: zod
          .string()
          .max(sendFeedbackBodyContextAppVersionMax)
          .regex(sendFeedbackBodyContextAppVersionRegExp)
          .describe('Version of the client, e.g. 1.0.0 or a git commit'),
        buildNumber: zod
          .string()
          .max(sendFeedbackBodyContextBuildNumberMax)
          .optional()
          .describe('Build number of a mobile client'),
        osVersion: zod
          .string()
          .max(sendFeedbackBodyContextOsVersionMax)
          .optional()
          .describe('OS name and version, e.g. Android 15'),
        device: zod
          .string()
          .max(sendFeedbackBodyContextDeviceMax)
          .optional()
          .describe('Device model'),
        userAgent: zod
          .string()
          .max(sendFeedbackBodyContextUserAgentMax)
          .optional()
          .describe('Browser user agent'),
        route: zod
          .string()
          .max(sendFeedbackBodyContextRouteMax)
          .optional()
          .describe('Path of the current page or screen, without its query string'),
        locale: zod
          .string()
          .max(sendFeedbackBodyContextLocaleMax)
          .optional()
          .describe('UI language, e.g. fr'),
        timezone: zod
          .string()
          .max(sendFeedbackBodyContextTimezoneMax)
          .optional()
          .describe('IANA time zone, e.g. Europe/Paris'),
        teamSlug: zod
          .string()
          .max(sendFeedbackBodyContextTeamSlugMax)
          .optional()
          .describe('Slug of the team being browsed, if any'),
      })
      .describe('Client, device and screen'),
    error: zod
      .object({
        type: zod
          .string()
          .max(sendFeedbackBodyErrorTypeMax)
          .regex(sendFeedbackBodyErrorTypeRegExp)
          .describe('Error class, e.g. TypeError or _TypeError'),
        message: zod.string().max(sendFeedbackBodyErrorMessageMax).describe('Error message'),
        stack: zod
          .string()
          .max(sendFeedbackBodyErrorStackMax)
          .optional()
          .describe('Stack trace, as the client printed it'),
      })
      .optional()
      .describe(
        'The unhandled error the report was opened from, if any. Links the report to the automatic error report of the same error.'
      ),
    logs: zod
      .array(
        zod
          .object({
            ts: zod.iso.datetime({ offset: true }).describe('When it was logged'),
            level: zod.enum(['DEBUG', 'INFO', 'WARN', 'ERROR']).describe('Severity'),
            source: zod
              .string()
              .max(sendFeedbackBodyLogsItemSourceMax)
              .regex(sendFeedbackBodyLogsItemSourceRegExp)
              .describe('What logged it: console, http, navigation, error…'),
            message: zod
              .string()
              .max(sendFeedbackBodyLogsItemMessageMax)
              .describe('The entry, truncated by the client'),
          })
          .describe("One entry of the client's recent log")
      )
      .max(sendFeedbackBodyLogsMax)
      .optional()
      .describe(
        "The client's recent log, oldest first. Absent when the member chose not to attach technical details."
      ),
  })
  .describe('A bug report or a suggestion written by a member')

export const SendFeedbackResponse = zod.void()

/**
 * Sent by a client, without the member's intervention, when it catches an unhandled error. The same error reported by many clients becomes one issue. Always 204 once valid, including past the per-member quota, where it is dropped: a client must never retry nor surface this call's failure.
 * @summary Report an unhandled error
 */
export const reportClientErrorBodyContextAppVersionMax = 50

export const reportClientErrorBodyContextAppVersionRegExp = new RegExp('\\S')
export const reportClientErrorBodyContextBuildNumberMax = 50

export const reportClientErrorBodyContextOsVersionMax = 100

export const reportClientErrorBodyContextDeviceMax = 200

export const reportClientErrorBodyContextUserAgentMax = 500

export const reportClientErrorBodyContextRouteMax = 500

export const reportClientErrorBodyContextLocaleMax = 20

export const reportClientErrorBodyContextTimezoneMax = 64

export const reportClientErrorBodyContextTeamSlugMax = 100

export const reportClientErrorBodyErrorTypeMax = 200

export const reportClientErrorBodyErrorTypeRegExp = new RegExp('\\S')
export const reportClientErrorBodyErrorMessageMax = 2000

export const reportClientErrorBodyErrorStackMax = 16000

export const reportClientErrorBodyLogsItemSourceMax = 50

export const reportClientErrorBodyLogsItemSourceRegExp = new RegExp('\\S')
export const reportClientErrorBodyLogsItemMessageMax = 1000

export const reportClientErrorBodyLogsMax = 50

export const ReportClientErrorBody = zod
  .object({
    context: zod
      .object({
        platform: zod.enum(['WEB', 'ANDROID', 'IOS']).describe('The client'),
        appVersion: zod
          .string()
          .max(reportClientErrorBodyContextAppVersionMax)
          .regex(reportClientErrorBodyContextAppVersionRegExp)
          .describe('Version of the client, e.g. 1.0.0 or a git commit'),
        buildNumber: zod
          .string()
          .max(reportClientErrorBodyContextBuildNumberMax)
          .optional()
          .describe('Build number of a mobile client'),
        osVersion: zod
          .string()
          .max(reportClientErrorBodyContextOsVersionMax)
          .optional()
          .describe('OS name and version, e.g. Android 15'),
        device: zod
          .string()
          .max(reportClientErrorBodyContextDeviceMax)
          .optional()
          .describe('Device model'),
        userAgent: zod
          .string()
          .max(reportClientErrorBodyContextUserAgentMax)
          .optional()
          .describe('Browser user agent'),
        route: zod
          .string()
          .max(reportClientErrorBodyContextRouteMax)
          .optional()
          .describe('Path of the current page or screen, without its query string'),
        locale: zod
          .string()
          .max(reportClientErrorBodyContextLocaleMax)
          .optional()
          .describe('UI language, e.g. fr'),
        timezone: zod
          .string()
          .max(reportClientErrorBodyContextTimezoneMax)
          .optional()
          .describe('IANA time zone, e.g. Europe/Paris'),
        teamSlug: zod
          .string()
          .max(reportClientErrorBodyContextTeamSlugMax)
          .optional()
          .describe('Slug of the team being browsed, if any'),
      })
      .describe('Client, device and screen'),
    error: zod
      .object({
        type: zod
          .string()
          .max(reportClientErrorBodyErrorTypeMax)
          .regex(reportClientErrorBodyErrorTypeRegExp)
          .describe('Error class, e.g. TypeError or _TypeError'),
        message: zod.string().max(reportClientErrorBodyErrorMessageMax).describe('Error message'),
        stack: zod
          .string()
          .max(reportClientErrorBodyErrorStackMax)
          .optional()
          .describe('Stack trace, as the client printed it'),
      })
      .describe('The error'),
    logs: zod
      .array(
        zod
          .object({
            ts: zod.iso.datetime({ offset: true }).describe('When it was logged'),
            level: zod.enum(['DEBUG', 'INFO', 'WARN', 'ERROR']).describe('Severity'),
            source: zod
              .string()
              .max(reportClientErrorBodyLogsItemSourceMax)
              .regex(reportClientErrorBodyLogsItemSourceRegExp)
              .describe('What logged it: console, http, navigation, error…'),
            message: zod
              .string()
              .max(reportClientErrorBodyLogsItemMessageMax)
              .describe('The entry, truncated by the client'),
          })
          .describe("One entry of the client's recent log")
      )
      .max(reportClientErrorBodyLogsMax)
      .optional()
      .describe("The client's recent log, oldest first"),
  })
  .describe('An unhandled error, reported automatically by a client')

export const ReportClientErrorResponse = zod.void()
