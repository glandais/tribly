import * as zod from 'zod'

/**
 * Record interest in being added to a beta program (TestFlight, Play Console, Garmin Connect IQ) once one opens. Submitting the same email again has no effect.
 * @summary Sign up for a beta program
 */
export const signUpForBetaBodyEmailRegExp = new RegExp('\\S')

export const SignUpForBetaBody = zod
  .object({
    email: zod
      .string()
      .regex(signUpForBetaBodyEmailRegExp)
      .describe('Email to contact once a beta is open'),
  })
  .describe('A request to be notified and added to a beta program once one opens')

export const SignUpForBetaResponse = zod.void()
