package com.tribly.service.auth;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.platform.Domain;
import com.tribly.service.security.DomainResolver;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.mailer.MockMailbox;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AuthEmailServiceTest {

  @Inject AuthEmailService authEmailService;
  @Inject MockMailbox mailbox;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject DomainResolver domainResolver;

  private Domain domain;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    domainResolver.setDomainForTest(domain);
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
  void sendOtpEmail_shouldSendEmail() {
    authEmailService.sendOtpEmail("otp@example.com", "123456");

    var sent = mailbox.getMailsSentTo("otp@example.com");
    assertEquals(1, sent.size());

    var mail = sent.getFirst();
    assertTrue(mail.getSubject().contains("code de connexion"));
    assertTrue(mail.getText().contains("123456"));
    assertTrue(mail.getText().contains("5 minutes"));
  }

  @Test
  void sendVerificationEmail_shouldContainAppName() {
    authEmailService.sendVerificationEmail("test@example.com", "Test", "token");

    var mail = mailbox.getMailsSentTo("test@example.com").getFirst();
    assertTrue(mail.getSubject().contains("Tribly"));
  }
}
