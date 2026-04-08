package fr.pedalons.infrastructure.email;

import java.util.List;
import java.util.Map;

public record BrevoEmailRequest(
    List<EmailAddress> to, long templateId, Map<String, Object> params) {

  public record EmailAddress(String email) {}
}
