package ru.translator.translateit.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.io.IOException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

@JsonTest
@DisplayName("Smoke сериализация / десериализация MyMemoryDto")
class MyMemoryDtoSerializationTest {

  @Autowired
  private JacksonTester<MyMemoryTranslateResponseDto> jacksonTester;
  private MyMemoryTranslateResponseDto expectedResponseDto;

  @BeforeEach
  public void prepareExpectedResponse() {
    var responseData = new MyMemoryResponseData();
    responseData.setTranslatedText("word");
    responseData.setMatch(1);
    expectedResponseDto = new MyMemoryTranslateResponseDto();
    expectedResponseDto.setResponseData(responseData);
  }

  @Test
  @DisplayName("Сериализация MyMemoryDto")
  void myMemoryTranslateDtoSerializationTest() throws IOException {
    JsonContent<MyMemoryTranslateResponseDto> json = jacksonTester.write(expectedResponseDto);
    SoftAssertions.assertSoftly(softAssertions -> {
      assertThat(json).extractingJsonPathStringValue("$.responseData.translatedText")
          .isEqualTo(expectedResponseDto.getResponseData().getTranslatedText());

      assertThat(json).extractingJsonPathNumberValue("$.responseData.match")
          .isEqualTo(expectedResponseDto.getResponseData().getMatch());
    });
  }

  @Test
  @DisplayName("Десериализация MyMemoryDto")
  void myMemoryTranslateDtoDeserializationTest() throws IOException {
    var dtoAsString = "{\"responseData\": {\"translatedText\": \"word\", \"match\": 1}}";
    MyMemoryTranslateResponseDto deserializedTranslationRequestDto = jacksonTester.parseObject(dtoAsString);

    assertSoftly(softAssertions ->
        softAssertions.assertThat(deserializedTranslationRequestDto)
            .usingRecursiveComparison()
            .isEqualTo(expectedResponseDto));
  }
}
