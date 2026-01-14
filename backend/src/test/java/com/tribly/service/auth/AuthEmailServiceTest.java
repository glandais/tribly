package com.tribly.service.auth;

import static org.junit.jupiter.api.Assertions.*;

import io.quarkus.mailer.MockMailbox;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AuthEmailServiceTest {

  @Inject AuthEmailService authEmailService;
  @Inject MockMailbox mailbox;

  @BeforeEach
  void setUp() {
    mailbox.clear();
  }

  @Test
  void sendVerificationEmail_shouldSendEmail() {
    authEmailService.sendVerificationEmail("test@example.com", "Test User", "verify-token-123");

    var sent = mailbox.getMailsSentTo("test@example.com");
    assertEquals(1, sent.size());

    var mail = sent.getFirst();
    assertTrue(mail.getSubject().contains("Confirmez votre adresse email"));
    assertTrue(mail.getText().contains("Test User"));
    assertTrue(mail.getText().contains("verify-email?token=verify-token-123"));
    assertTrue(mail.getText().contains("24 heures"));
  }

  @Test
  void sendMagicLinkEmail_shouldSendEmail() {
    authEmailService.sendMagicLinkEmail("magic@example.com", "magic-token-456");

    var sent = mailbox.getMailsSentTo("magic@example.com");
    assertEquals(1, sent.size());

    var mail = sent.getFirst();
    assertTrue(mail.getSubject().contains("Connexion"));
    assertTrue(mail.getText().contains("magic-link/verify?token=magic-token-456"));
    assertTrue(mail.getText().contains("15 minutes"));
  }

  @Test
  void sendVerificationEmail_shouldContainAppName() {
    authEmailService.sendVerificationEmail("test@example.com", "Test", "token");

    var mail = mailbox.getMailsSentTo("test@example.com").getFirst();
    assertTrue(mail.getSubject().contains("Tribly"));
  }
}
