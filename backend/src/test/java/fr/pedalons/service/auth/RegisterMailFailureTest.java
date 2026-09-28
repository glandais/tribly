package fr.pedalons.service.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.common.exception.PedalonsException;
import fr.pedalons.dto.auth.request.RegisterRequest;
import fr.pedalons.repository.user.UserRepository;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** A sign-up whose verification mail does not leave: named for what it is, and nothing created. */
@QuarkusTest
class RegisterMailFailureTest extends AbstractBaseTest {

  @Inject AuthService authService;
  @Inject UserRepository userRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject DomainResolver domainResolver;

  @InjectMock AuthEmailService authEmailService;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domainResolver.setDomainForTest(dataService.getOrCreateDefaultDomain());
    // The SMTP relay timing out (EmailService gives up after 15 s).
    doThrow(new RuntimeException("mail timeout"))
        .when(authEmailService)
        .sendVerificationEmail(anyString(), anyString(), anyString());
  }

  @Test
  void aMailThatDoesNotLeaveIsNamed_andCreatesNoAccount() {
    RegisterRequest request =
        new RegisterRequest("slow-mail@example.com", "Slow Mail", "password123", true);

    PedalonsException refused =
        assertThrows(PedalonsException.class, () -> authService.register(request));

    assertEquals("EMAIL_NOT_SENT", refused.getMessage());
    assertEquals(Response.Status.INTERNAL_SERVER_ERROR, refused.getStatus());
    // Nothing was created — the account only exists once the address is verified — so trying
    // again is a fresh attempt, not an « address already in use ».
    assertEquals(0, userRepository.count("email", "slow-mail@example.com"));
    PedalonsException again =
        assertThrows(PedalonsException.class, () -> authService.register(request));
    assertEquals("EMAIL_NOT_SENT", again.getMessage());
  }
}
