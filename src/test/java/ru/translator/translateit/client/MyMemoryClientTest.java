package ru.translator.translateit.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.SneakyThrows;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.translator.translateit.dto.MyMemoryResponseData;
import ru.translator.translateit.dto.MyMemoryTranslateResponseDto;

@RestClientTest(MyMemoryClient.class)
class MyMemoryClientTest {

  @Autowired
  private MyMemoryClient client;

  @Autowired
  private MockRestServiceServer server;

  @Autowired
  private ObjectMapper objectMapper;

  private MyMemoryTranslateResponseDto responseDto;

  @BeforeEach
  public void setUp() {
    var responseData = new MyMemoryResponseData();
    responseData.setMatch(1);
    responseDto = new MyMemoryTranslateResponseDto();
    responseDto.setResponseData(responseData);
  }

  static List<Arguments> sendTranslateRequestTest() {
    return List.of(
        Arguments.arguments("слово", "word", "ru|en"),
        Arguments.arguments("word", "слово", "en|ru")
    );
  }

  @ParameterizedTest
  @MethodSource
  void sendTranslateRequestTest(String sourceWord, String translation, String translationParams)
      throws JsonProcessingException {
    responseDto.getResponseData().setTranslatedText(sourceWord);
    var responseDtoAsString = objectMapper.writeValueAsString(responseDto);
    server.expect(requestTo(getEncodedUrl(translation, translationParams)))
        .andRespond(withSuccess(responseDtoAsString, MediaType.APPLICATION_JSON));

    var response = client.sentRequestToTranslator(translation, translationParams);
    assertThat(response.getResponseData().getTranslatedText()).isEqualTo(
        responseDto.getResponseData().getTranslatedText());
  }

  @SneakyThrows
  private URI getEncodedUrl(String word, String translationParams) {
    return new URIBuilder("https://api.mymemory.translated.net")
        .setPath("/get")
        .addParameter("q", word)
        .addParameter("langpair", translationParams)
        .setCharset(StandardCharsets.UTF_8)
        .build();
  }
}
