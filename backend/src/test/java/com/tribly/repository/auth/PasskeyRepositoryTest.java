package com.tribly.repository.auth;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.AbstractBaseTest;
import com.tribly.domain.auth.Passkey;
import com.tribly.domain.user.User;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class PasskeyRepositoryTest extends AbstractBaseTest {

  @Inject PasskeyRepository passkeyRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private User user;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    user = dataService.createUser("test@example.com", "Test User");
  }

  @Test
  void findByCredentialId_shouldReturnPasskey() {
    byte[] credentialId = "credential-123".getBytes();
    byte[] publicKey = "public-key".getBytes();
    dataService.createPasskey(user, credentialId, publicKey);

    Optional<Passkey> result = passkeyRepository.findByCredentialId(credentialId);

    assertTrue(result.isPresent());
    assertArrayEquals(credentialId, result.get().getCredentialId());
    assertEquals(user.getId(), result.get().getUser().getId());
  }

  @Test
  void findByCredentialId_shouldReturnEmptyForNonexistent() {
    Optional<Passkey> result = passkeyRepository.findByCredentialId("nonexistent".getBytes());

    assertTrue(result.isEmpty());
  }

  @Test
  void findByCredentialId_shouldIgnoreDeletedPasskeys() {
    byte[] credentialId = "deleted-credential".getBytes();
    Passkey passkey = dataService.createPasskey(user, credentialId, "public-key".getBytes());
    dataService.deletePasskey(passkey);

    Optional<Passkey> result = passkeyRepository.findByCredentialId(credentialId);

    assertTrue(result.isEmpty());
  }

  @Test
  void findByUserId_shouldReturnUserPasskeys() {
    dataService.createPasskey(user, "cred-1".getBytes(), "key-1".getBytes());
    dataService.createPasskey(user, "cred-2".getBytes(), "key-2".getBytes());

    List<Passkey> results = passkeyRepository.findByUserId(user.getId());

    assertEquals(2, results.size());
  }

  @Test
  void findByUserId_shouldIgnoreDeletedPasskeys() {
    dataService.createPasskey(user, "active-cred".getBytes(), "key".getBytes());
    Passkey deletedPasskey =
        dataService.createPasskey(user, "deleted-cred".getBytes(), "key".getBytes());
    dataService.deletePasskey(deletedPasskey);

    List<Passkey> results = passkeyRepository.findByUserId(user.getId());

    assertEquals(1, results.size());
    assertArrayEquals("active-cred".getBytes(), results.get(0).getCredentialId());
  }

  @Test
  void findByUserId_shouldReturnEmptyForUserWithNoPasskeys() {
    User otherUser = dataService.createUser("other@example.com", "Other User");

    List<Passkey> results = passkeyRepository.findByUserId(otherUser.getId());

    assertTrue(results.isEmpty());
  }

  @Test
  void findByIdAndUserId_shouldReturnPasskey() {
    Passkey passkey = dataService.createPasskey(user, "cred".getBytes(), "key".getBytes());

    Optional<Passkey> result = passkeyRepository.findByIdAndUserId(passkey.getId(), user.getId());

    assertTrue(result.isPresent());
    assertEquals(passkey.getId(), result.get().getId());
  }

  @Test
  void findByIdAndUserId_shouldReturnEmptyForWrongUser() {
    User otherUser = dataService.createUser("other@example.com", "Other User");
    Passkey passkey = dataService.createPasskey(user, "cred".getBytes(), "key".getBytes());

    Optional<Passkey> result =
        passkeyRepository.findByIdAndUserId(passkey.getId(), otherUser.getId());

    assertTrue(result.isEmpty());
  }

  @Test
  void findByIdAndUserId_shouldIgnoreDeletedPasskeys() {
    Passkey passkey = dataService.createPasskey(user, "cred".getBytes(), "key".getBytes());
    dataService.deletePasskey(passkey);

    Optional<Passkey> result = passkeyRepository.findByIdAndUserId(passkey.getId(), user.getId());

    assertTrue(result.isEmpty());
  }

  @Test
  void countByUserId_shouldReturnCorrectCount() {
    dataService.createPasskey(user, "cred-1".getBytes(), "key-1".getBytes());
    dataService.createPasskey(user, "cred-2".getBytes(), "key-2".getBytes());
    dataService.createPasskey(user, "cred-3".getBytes(), "key-3".getBytes());

    int count = passkeyRepository.countByUserId(user.getId());

    assertEquals(3, count);
  }

  @Test
  void countByUserId_shouldIgnoreDeletedPasskeys() {
    dataService.createPasskey(user, "active-1".getBytes(), "key".getBytes());
    dataService.createPasskey(user, "active-2".getBytes(), "key".getBytes());
    Passkey deletedPasskey =
        dataService.createPasskey(user, "deleted".getBytes(), "key".getBytes());
    dataService.deletePasskey(deletedPasskey);

    int count = passkeyRepository.countByUserId(user.getId());

    assertEquals(2, count);
  }

  @Test
  void countByUserId_shouldReturnZeroForUserWithNoPasskeys() {
    int count = passkeyRepository.countByUserId(user.getId());

    assertEquals(0, count);
  }
}
