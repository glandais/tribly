package fr.pedalons.infrastructure.email;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

public record BrevoEmailRequest(
    List<EmailAddress> to,
    long templateId,
    Map<String, Object> params,
    @JsonInclude(JsonInclude.Include.NON_NULL) @Nullable EmailAddress replyTo) {

  public BrevoEmailRequest(List<EmailAddress> to, long templateId, Map<String, Object> params) {
    this(to, templateId, params, null);
  }

  public record EmailAddress(String email) {}
}
