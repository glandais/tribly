/**
 * Comment creation request
 */
export interface CommentRequest {
  /**
   * Comment content
   * @maxLength 5000
   * @pattern \S
   */
  content: string
  /** Parent comment ID for replies (optional) */
  parentId?: string
}
