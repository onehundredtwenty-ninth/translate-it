package ru.translator.translateit.client;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.translator.translateit.dto.MyMemoryTranslateResponseDto;

@Component
public class MyMemoryClient extends BaseClient {

  @Autowired
  public MyMemoryClient(RestTemplateBuilder builder) {
    super(
        builder
            .uriTemplateHandler(new DefaultUriBuilderFactory("https://api.mymemory.translated.net"))
            .requestFactory(HttpComponentsClientHttpRequestFactory::new)
            .build());
  }

  public MyMemoryTranslateResponseDto sentRequestToTranslator(String word, String langPair) {
    var response = get("/get?q={q}&langpair={langpair}", Map.of(
            "q", word,
            "langpair", langPair
        ),
        MyMemoryTranslateResponseDto.class);
    return response.getBody();
  }
}
