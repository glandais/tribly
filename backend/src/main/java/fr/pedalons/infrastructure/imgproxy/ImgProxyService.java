package fr.pedalons.infrastructure.imgproxy;

import fr.pedalons.infrastructure.storage.StorageService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class ImgProxyService {

  @Inject @RestClient ImgProxyClient imgProxyClient;

  @Inject StorageService storageService;

  public Response getPhoto(String accept, String key, Integer width, Integer height) {
    String s3Path = storageService.getS3Path(key);
    return imgProxyClient.getImage(accept, width, height, s3Path);
  }

  public InputStream getPhotoContent(String accept, String key, Integer width, Integer height) {
    String s3Path = storageService.getS3Path(key);
    return imgProxyClient.getImageContent(accept, width, height, s3Path);
  }
}
