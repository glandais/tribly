package fr.pedalons.service.admin;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.common.exception.ConflictException;
import fr.pedalons.common.exception.NotFoundException;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.platform.DomainAlias;
import fr.pedalons.domain.team.Team;
import fr.pedalons.dto.admin.AdminDomainAliasDto;
import fr.pedalons.dto.admin.CreateDomainAliasRequest;
import fr.pedalons.dto.admin.UpdateDomainAliasRequest;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.repository.platform.DomainAliasRepository;
import fr.pedalons.repository.platform.DomainRepository;
import fr.pedalons.repository.team.TeamRepository;
import fr.pedalons.service.security.annotation.Admin;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class AdminDomainAliasService {

  @Inject DomainRepository domainRepository;

  @Inject DomainAliasRepository domainAliasRepository;

  @Inject TeamRepository teamRepository;

  @Admin
  public List<AdminDomainAliasDto> listAliases(String domainId) {
    Domain domain = findDomain(domainId);
    return domainAliasRepository.listByDomain(domain.getId()).stream()
        .map(AdminDomainAliasDto::from)
        .toList();
  }

  @Admin
  @Transactional
  public AdminDomainAliasDto createAlias(String domainId, CreateDomainAliasRequest request) {
    Domain domain = findDomain(domainId);
    String hostname = normalizeHostname(request.hostname());
    assertHostnameAvailable(hostname);
    Team team = findTeamInDomain(domain, request.teamSlug());

    DomainAlias alias = new DomainAlias(hostname, domain, team, request.name(), request.baseUrl());
    alias.setAndroidFingerprints(request.androidFingerprints());
    domainAliasRepository.persist(alias);
    return AdminDomainAliasDto.from(alias);
  }

  @Admin
  @Transactional
  public AdminDomainAliasDto updateAlias(
      String domainId, String aliasId, UpdateDomainAliasRequest request) {
    Domain domain = findDomain(domainId);
    DomainAlias alias = findAliasInDomain(domain, aliasId);
    Team team = findTeamInDomain(domain, request.teamSlug());

    alias.setPinnedTeam(team);
    alias.setName(request.name());
    alias.setBaseUrl(request.baseUrl());
    alias.setAndroidFingerprints(request.androidFingerprints());
    domainAliasRepository.persist(alias);
    return AdminDomainAliasDto.from(alias);
  }

  @Admin
  @Transactional
  public AdminDomainAliasDto toggleAliasActive(String domainId, String aliasId) {
    Domain domain = findDomain(domainId);
    DomainAlias alias = findAliasInDomain(domain, aliasId);
    alias.setActive(!alias.isActive());
    domainAliasRepository.persist(alias);
    return AdminDomainAliasDto.from(alias);
  }

  @Admin
  @Transactional
  public void deleteAlias(String domainId, String aliasId) {
    Domain domain = findDomain(domainId);
    DomainAlias alias = findAliasInDomain(domain, aliasId);
    alias.setDeleted(true);
    alias.setActive(false);
    domainAliasRepository.persist(alias);
  }

  private void assertHostnameAvailable(String hostname) {
    if (domainRepository.findByDomain(hostname).isPresent()
        || domainAliasRepository.existsByHostname(hostname)) {
      throw new ConflictException(ErrorCode.DOMAIN_ALIAS_HOSTNAME_EXISTS);
    }
  }

  private Team findTeamInDomain(Domain domain, String teamSlug) {
    return teamRepository
        .findBySlugAndDomain(domain.getId(), teamSlug)
        .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));
  }

  private Domain findDomain(String domainId) {
    Long id = TsidUtils.toLong(domainId);
    return domainRepository
        .find("id = ?1 and deleted = false", id)
        .firstResultOptional()
        .orElseThrow(() -> new NotFoundException(ErrorCode.DOMAIN_NOT_FOUND));
  }

  private DomainAlias findAliasInDomain(Domain domain, String aliasId) {
    Long id = TsidUtils.toLong(aliasId);
    DomainAlias alias =
        domainAliasRepository
            .findByIdOptional(id)
            .orElseThrow(() -> new NotFoundException(ErrorCode.DOMAIN_ALIAS_NOT_FOUND));
    if (!alias.getDomain().getId().equals(domain.getId())) {
      throw new NotFoundException(ErrorCode.DOMAIN_ALIAS_NOT_FOUND);
    }
    return alias;
  }

  private String normalizeHostname(String hostname) {
    return hostname.trim().toLowerCase();
  }
}
