package com.tribly.service.admin;

import com.tribly.common.TsidUtils;
import com.tribly.common.exception.NotFoundException;
import com.tribly.domain.platform.Domain;
import com.tribly.dto.admin.AdminDomainDto;
import com.tribly.dto.admin.AdminStatsDto;
import com.tribly.dto.common.TriblyPage;
import com.tribly.repository.platform.DomainRepository;
import com.tribly.repository.team.TeamRepository;
import com.tribly.repository.user.UserRepository;
import com.tribly.service.security.annotation.Admin;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class AdminDomainService {

  @Inject DomainRepository domainRepository;

  @Inject TeamRepository teamRepository;

  @Inject UserRepository userRepository;

  @Admin
  public AdminStatsDto getStats() {
    long totalDomains = domainRepository.count("deleted = false");
    long totalTeams = teamRepository.count("deleted = false");
    long totalUsers = userRepository.count("deleted = false");
    return new AdminStatsDto(totalDomains, totalTeams, totalUsers);
  }

  @Admin
  public TriblyPage<AdminDomainDto> listDomains(int page, int size) {
    List<Domain> domains =
        domainRepository.find("deleted = false order by createdAt desc").page(page, size).list();
    long total = domainRepository.count("deleted = false");

    List<AdminDomainDto> items =
        domains.stream()
            .map(
                domain -> {
                  long teamCount =
                      teamRepository.count("domain.id = ?1 and deleted = false", domain.getId());
                  long userCount =
                      userRepository.count("domain.id = ?1 and deleted = false", domain.getId());
                  return AdminDomainDto.from(domain, teamCount, userCount);
                })
            .toList();

    return new TriblyPage<>(items, total);
  }

  @Admin
  public AdminDomainDto getDomain(String domainId) {
    Domain domain = findDomain(domainId);
    long teamCount = teamRepository.count("domain.id = ?1 and deleted = false", domain.getId());
    long userCount = userRepository.count("domain.id = ?1 and deleted = false", domain.getId());
    return AdminDomainDto.from(domain, teamCount, userCount);
  }

  @Admin
  @Transactional
  public AdminDomainDto createDomain(
      String domainName, String name, String baseUrl, boolean singleTeam) {
    Domain domain = new Domain(domainName, name, baseUrl);
    domain.setSingleTeam(singleTeam);
    domainRepository.persist(domain);
    return AdminDomainDto.from(domain, 0, 0);
  }

  @Admin
  @Transactional
  public AdminDomainDto updateDomain(
      String domainId, String name, String baseUrl, boolean singleTeam) {
    Domain domain = findDomain(domainId);
    domain.setName(name);
    domain.setBaseUrl(baseUrl);
    domain.setSingleTeam(singleTeam);
    domainRepository.persist(domain);
    long teamCount = teamRepository.count("domain.id = ?1 and deleted = false", domain.getId());
    long userCount = userRepository.count("domain.id = ?1 and deleted = false", domain.getId());
    return AdminDomainDto.from(domain, teamCount, userCount);
  }

  @Admin
  @Transactional
  public AdminDomainDto toggleDomainActive(String domainId) {
    Domain domain = findDomain(domainId);
    domain.setActive(!domain.isActive());
    domainRepository.persist(domain);
    long teamCount = teamRepository.count("domain.id = ?1 and deleted = false", domain.getId());
    long userCount = userRepository.count("domain.id = ?1 and deleted = false", domain.getId());
    return AdminDomainDto.from(domain, teamCount, userCount);
  }

  private Domain findDomain(String domainId) {
    Long id = TsidUtils.toLong(domainId);
    return domainRepository
        .find("id = ?1 and deleted = false", id)
        .firstResultOptional()
        .orElseThrow(NotFoundException::new);
  }
}
