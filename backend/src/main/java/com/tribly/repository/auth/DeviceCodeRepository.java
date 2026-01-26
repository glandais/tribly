package com.tribly.repository.auth;

import com.tribly.domain.auth.DeviceCode;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class DeviceCodeRepository implements PanacheRepository<DeviceCode> {

  public Optional<DeviceCode> findByDeviceCodeHash(String deviceCodeHash) {
    return find("deviceCodeHash", deviceCodeHash).firstResultOptional();
  }

  public Optional<DeviceCode> findByUserCode(String userCode) {
    return find("userCode", userCode.toUpperCase()).firstResultOptional();
  }

  public Optional<DeviceCode> findValidByUserCode(String userCode) {
    return find("userCode = ?1 and expiresAt > CURRENT_TIMESTAMP", userCode.toUpperCase())
        .firstResultOptional();
  }

  public Optional<DeviceCode> findValidByDeviceCodeHash(String deviceCodeHash) {
    return find("deviceCodeHash = ?1 and expiresAt > CURRENT_TIMESTAMP", deviceCodeHash)
        .firstResultOptional();
  }

  public long deleteExpiredCodes() {
    return delete("expiresAt < CURRENT_TIMESTAMP");
  }
}
