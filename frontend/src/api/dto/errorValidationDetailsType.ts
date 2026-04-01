
export type ErrorValidationDetailsType = typeof ErrorValidationDetailsType[keyof typeof ErrorValidationDetailsType];


export const ErrorValidationDetailsType = {
  VALIDATION: 'VALIDATION',
} as const;
