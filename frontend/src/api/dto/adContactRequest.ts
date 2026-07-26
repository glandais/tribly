/**
 * A message to relay to the author of an ad
 */
export interface AdContactRequest {
  /**
   * The message body, plain text. Rendered as text in the email: any markup is escaped rather than interpreted.
   * @minLength 10
   * @maxLength 2000
   * @pattern \S
   */
  message: string
}
