package fr.pedalons.enums;

/**
 * Where a feedback or an error signature stands with its GitHub issue. The row is stored first and
 * published by {@code FeedbackGithubWorker}: a report must never be lost because GitHub is slow,
 * down, or not configured.
 */
public enum GithubSyncStatus {
  /** Not published yet — including when no GitHub token is configured. */
  PENDING,
  CREATED,
  /** Given up after {@code FeedbackGithubWorker#MAX_ATTEMPTS}. The row stays, for the operator. */
  FAILED
}
