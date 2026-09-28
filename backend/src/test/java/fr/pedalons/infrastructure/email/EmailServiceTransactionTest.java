package fr.pedalons.infrastructure.email;

import static org.junit.jupiter.api.Assertions.assertEquals;

import fr.pedalons.AbstractBaseTest;
import io.quarkus.mailer.MockMailbox;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A mail sent from inside a transaction must leave the transaction alone. The send hops to an
 * event loop (see EmailService); carried there by context propagation, the caller's transaction
 * was sometimes still active on that thread at commit, and Narayana rolled the whole thing back
 * (ARJUNA016053) — an invitation answered 500.
 */
@QuarkusTest
class EmailServiceTransactionTest extends AbstractBaseTest {

  @Inject TransactionalMailSender sender;
  @Inject MockMailbox mailbox;

  @BeforeEach
  void setUp() {
    mailbox.clear();
  }

  @Test
  void aMailSentInsideATransactionLetsItCommit() {
    for (int i = 0; i < 200; i++) {
      sender.sendInTransaction("tx-" + i + "@example.com");
    }
    assertEquals(200, mailbox.getTotalMessagesSent());
  }
}
