package fr.pedalons.infrastructure.github;

/** A GitHub call that failed — transport error or non-2xx answer. The caller retries later. */
public class GitHubException extends RuntimeException {

  public GitHubException(String message) {
    super(message);
  }

  public GitHubException(String message, Throwable cause) {
    super(message, cause);
  }
}
