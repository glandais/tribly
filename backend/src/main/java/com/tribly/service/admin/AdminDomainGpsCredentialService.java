package com.tribly.service.admin;

import com.tribly.common.TsidUtils;
import com.tribly.common.exception.ConflictException;
import com.tribly.common.exception.NotFoundException;
import com.tribly.domain.gps.DomainGpsCredential;
import com.tribly.domain.platform.Domain;
import com.tribly.dto.admin.AdminGpsCredentialDto;
import com.tribly.dto.admin.CreateGpsCredentialRequest;
import com.tribly.dto.admin.UpdateGpsCredentialRequest;
import com.tribly.dto.error.ErrorCode;
import com.tribly.infrastructure.security.TokenEncryptionService;
import com.tribly.repository.gps.DomainGpsCredentialRepository;
import com.tribly.repository.platform.DomainRepository;
import com.tribly.service.security.annotation.Admin;
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

    if (request.clientSecret() != null && !request.clientSecret().isBlank()) {
      credential.setClientSecretEncrypted(tokenEncryptionService.encrypt(request.clientSecret()));
    }

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

    // Only update secret if provided (non-null and non-empty)
    if (request.clientSecret() != null && !request.clientSecret().isBlank()) {
      credential.setClientSecretEncrypted(tokenEncryptionService.encrypt(request.clientSecret()));
    }

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
