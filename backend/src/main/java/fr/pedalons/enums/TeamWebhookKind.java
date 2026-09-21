package fr.pedalons.enums;

import java.net.URI;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * The shape a team webhook's messages take, read from its URL — so there is no format setting to
 * get wrong.
 */
public enum TeamWebhookKind {
  /** A Slack incoming webhook: {@code {"text": …}}. */
  SLACK,
  /** A Discord channel webhook: {@code {"content": …}}, mentions disabled. */
  DISCORD,
  /**
   * A Mattermost incoming webhook, fed its Slack-compatible payload ({@code {"text": …}}, {@code
   * <url|label>} links) — with Markdown's {@code **bold**}, and mentions neutralised: unlike Slack,
   * Mattermost acts on a bare {@code @channel} in the text.
   */
  MATTERMOST,
  /** Anything else: a structured JSON document. */
  GENERIC;

  /**
   * Mattermost is self-hosted, so no host gives it away; its incoming webhooks all live at {@code
   * /hooks/} followed by a 26-character id. A false positive costs little: the endpoint gets a
   * {@code {"text": …}} document, which is also what the generic format carries.
   */
  private static final Pattern MATTERMOST_PATH = Pattern.compile("/hooks/[a-z0-9]{26}/?");

  public static TeamWebhookKind of(URI url) {
    String host = url.getHost() == null ? "" : url.getHost().toLowerCase(Locale.ROOT);
    String path = url.getPath() == null ? "" : url.getPath();
    if (host.equals("hooks.slack.com")) {
      return SLACK;
    }
    if ((host.equals("discord.com")
            || host.endsWith(".discord.com")
            || host.equals("discordapp.com")
            || host.endsWith(".discordapp.com"))
        && path.startsWith("/api/webhooks/")) {
      return DISCORD;
    }
    if (MATTERMOST_PATH.matcher(path).matches()) {
      return MATTERMOST;
    }
    return GENERIC;
  }
}
