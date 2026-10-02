package fr.pedalons.service.admin;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.common.exception.ConflictException;
import fr.pedalons.common.exception.NotFoundException;
import fr.pedalons.domain.gps.DomainGpsCredential;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.dto.admin.AdminGpsCredentialDto;
import fr.pedalons.dto.admin.CreateGpsCredentialRequest;
import fr.pedalons.dto.admin.UpdateGpsCredentialRequest;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.enums.GpsOAuthVersion;
import fr.pedalons.enums.GpsServiceType;
import fr.pedalons.infrastructure.security.TokenEncryptionService;
import fr.pedalons.repository.gps.DomainGpsCredentialRepository;
import fr.pedalons.repository.platform.DomainRepository;
import fr.pedalons.service.security.annotation.Admin;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class AdminDomainGpsCredentialService {

  @Inject DomainGpsCredentialRepository credentialRepository;

  @Inject DomainRepository domainRepository;

  @Inject TokenEncryptionService tokenEncryptionService;

  @Admin
  public List<AdminGpsCredentialDto> listCredentials(String domainId) {
    Long id = TsidUtils.toLong(domainId);
    return credentialRepository.find("domain.id = ?1 order by createdAt desc", id).list().stream()
        .map(AdminGpsCredentialDto::from)
        .toList();
  }

  @Admin
  @Transactional
  public AdminGpsCredentialDto createCredential(
      String domainId, CreateGpsCredentialRequest request) {
    Long id = TsidUtils.toLong(domainId);
    Domain domain = findDomain(id);

    // Check uniqueness (domain_id, service_type)
    boolean exists =
        credentialRepository.count("domain.id = ?1 and serviceType = ?2", id, request.serviceType())
            > 0;
    if (exists) {
      throw new ConflictException(ErrorCode.GPS_CREDENTIAL_ALREADY_EXISTS);
    }

    DomainGpsCredential credential =
        new DomainGpsCredential(domain, request.serviceType(), request.clientId());
    credential.setActive(request.active());
    if (request.oauthVersion() != null) {
      credential.setOauthVersion(request.oauthVersion());
    }

    if (request.clientSecret() != null && !request.clientSecret().isBlank()) {
      credential.setClientSecretEncrypted(tokenEncryptionService.encrypt(request.clientSecret()));
    }
    checkOAuthVersion(credential);

    credentialRepository.persist(credential);
    return AdminGpsCredentialDto.from(credential);
  }

  @Admin
  @Transactional
  public AdminGpsCredentialDto updateCredential(
      String domainId, String credentialId, UpdateGpsCredentialRequest request) {
    Long domId = TsidUtils.toLong(domainId);
    Long credId = TsidUtils.toLong(credentialId);

    DomainGpsCredential credential = findCredential(domId, credId);

    credential.setClientId(request.clientId());
    credential.setActive(request.active());
    if (request.oauthVersion() != null) {
      credential.setOauthVersion(request.oauthVersion());
    }

    // Only update secret if provided (non-null and non-empty)
    if (request.clientSecret() != null && !request.clientSecret().isBlank()) {
      credential.setClientSecretEncrypted(tokenEncryptionService.encrypt(request.clientSecret()));
    }
    checkOAuthVersion(credential);

    credentialRepository.persist(credential);
    return AdminGpsCredentialDto.from(credential);
  }

  @Admin
  @Transactional
  public void deleteCredential(String domainId, String credentialId) {
    Long domId = TsidUtils.toLong(domainId);
    Long credId = TsidUtils.toLong(credentialId);

    DomainGpsCredential credential = findCredential(domId, credId);
    credentialRepository.delete(credential);
  }

  /**
   * OAuth 1.0a is Garmin's legacy protocol, the only one its programme still accepts for our
   * applications (docs/LEDGER_*.md API-62); its consumer secret signs every request.
   */
  private static void checkOAuthVersion(DomainGpsCredential credential) {
    if (credential.getOauthVersion() != GpsOAuthVersion.OAUTH1) {
      return;
    }
    if (credential.getServiceType() != GpsServiceType.GARMIN) {
      throw new BusinessException(ErrorCode.GPS_OAUTH_VERSION_NOT_SUPPORTED);
    }
    if (credential.getClientSecretEncrypted() == null) {
      throw new BusinessException(ErrorCode.GPS_CLIENT_SECRET_REQUIRED);
    }
  }

  private Domain findDomain(Long domainId) {
    return domainRepository
        .find("id = ?1 and deleted = false", domainId)
        .firstResultOptional()
        .orElseThrow(NotFoundException::new);
  }

  private DomainGpsCredential findCredential(Long domainId, Long credentialId) {
    return credentialRepository
        .find("id = ?1 and domain.id = ?2", credentialId, domainId)
        .firstResultOptional()
        .orElseThrow(NotFoundException::new);
  }
}
