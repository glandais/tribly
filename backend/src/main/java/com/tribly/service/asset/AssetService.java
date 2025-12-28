package com.tribly.service.asset;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class AssetService {

  @ConfigProperty(name = "gpx.storage.path")
  String storagePath = "/tmp";
}
