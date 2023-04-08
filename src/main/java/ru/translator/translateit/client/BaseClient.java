package ru.translator.translateit.client;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

public class BaseClient {

  protected final RestTemplate rest;

  public BaseClient(RestTemplate rest) {
    this.rest = rest;
  }

  protected <T> ResponseEntity<T> get(String path, @Nullable Map<String, Object> parameters, Class<T> responseClass) {
    return makeAndGetSendRequest(path, parameters, responseClass);
  }

  private <T> ResponseEntity<T> makeAndGetSendRequest(String path, @Nullable Map<String, Object> parameters,
      Class<T> responseClass) {
    HttpEntity<?> requestEntity = new HttpEntity<>(null, defaultHeaders());

    try {
      return rest.exchange(path, HttpMethod.GET, requestEntity, responseClass, Objects.requireNonNull(parameters));
    } catch (HttpStatusCodeException e) {
      throw new IllegalStateException(
          "Не удалось получить перевод от стороннего сервиса " + e.getResponseBodyAsString());
    }
  }

  private HttpHeaders defaultHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(List.of(MediaType.APPLICATION_JSON));
    return headers;
  }
}
