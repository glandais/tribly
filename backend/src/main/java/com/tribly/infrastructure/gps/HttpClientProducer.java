package com.tribly.infrastructure.gps;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import java.net.http.HttpClient;
import java.time.Duration;

/**
 * CDI producer for Java HttpClient.
 */
@ApplicationScoped
public class HttpClientProducer {

  @Produces
  @ApplicationScoped
  public HttpClient httpClient() {
    return HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(30))
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build();
  }
}
