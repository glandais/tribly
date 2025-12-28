package com.tribly.domain.asset.repository;

import com.tribly.domain.asset.Asset;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AssetRepository implements PanacheRepository<Asset> {}
