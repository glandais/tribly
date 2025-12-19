package com.tribly.infrastructure.cache;

import io.github.glandais.gpx.util.CacheFolderProvider;
import jakarta.inject.Singleton;
import java.io.File;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Singleton
public class CacheFolderProviderImpl implements CacheFolderProvider {

  @ConfigProperty(name = "tribly.data.cache", defaultValue = "cache")
  protected File cacheFolder = new File("cache");

  @Override
  public File getCacheFolder() {
    return cacheFolder;
  }
}
