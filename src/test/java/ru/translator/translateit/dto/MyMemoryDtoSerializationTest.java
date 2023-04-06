package ru.translator.translateit.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.io.IOException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

@JsonTest
class MyMemoryDtoSerializationTest {

  @Autowired
  private JacksonTester<MyMemoryTranslateResponseDto> jacksonTester;
  private MyMemoryTranslateResponseDto myMemoryResponseDto;

  @Test
  void myMemoryTranslateDtoSerializationTest() throws IOException {
    var responseData = new MyMemoryResponseData();
    responseData.setTranslatedText("word");
    responseData.setMatch(1);
    var dto = new MyMemoryTranslateResponseDto();
    dto.setResponseData(responseData);

    JsonContent<MyMemoryTranslateResponseDto> json = jacksonTester.write(dto);
    SoftAssertions.assertSoftly(softAssertions -> {
      assertThat(json).extractingJsonPathStringValue("$.responseData.translatedText")
          .isEqualTo(dto.getResponseData().getTranslatedText());

      assertThat(json).extractingJsonPathNumberValue("$.responseData.match")
          .isEqualTo(dto.getResponseData().getMatch());
    });
  }

  @Test
  void myMemoryTranslateDtoDeserializationTest() throws IOException {
    var dtoAsString = "{\"responseData\": {\"translatedText\": \"word\", \"match\": 1}}";

    var responseData = new MyMemoryResponseData();
    responseData.setTranslatedText("word");
    responseData.setMatch(1);
    var dto = new MyMemoryTranslateResponseDto();
    dto.setResponseData(responseData);

    MyMemoryTranslateResponseDto deserializedTranslationRequestDto = jacksonTester.parseObject(dtoAsString);

    assertSoftly(softAssertions ->
        softAssertions.assertThat(deserializedTranslationRequestDto)
            .usingRecursiveComparison()
            .isEqualTo(dto));
  }
}
