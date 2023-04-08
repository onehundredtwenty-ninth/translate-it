package ru.translator.translateit.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.io.IOException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

@JsonTest
@DisplayName("Smoke сериализация / десериализация TranslationResponseDto")
class TranslationResponseDtoSerializationTest {

  @Autowired
  private JacksonTester<TranslationResponseDto> jacksonTester;

  @Test
  @DisplayName("Сериализация TranslationResponseDto")
  void translationResponseDtoSerializationTest() throws IOException {
    var dto = new TranslationResponseDto("word");
    JsonContent<TranslationResponseDto> json = jacksonTester.write(dto);
    SoftAssertions.assertSoftly(softAssertions -> {
      assertThat(json).extractingJsonPathStringValue("$.translatedString").isEqualTo(dto.getTranslatedString());
    });
  }

  @Test
  @DisplayName("Десериализация TranslationResponseDto")
  void translationResponseDtoDeserializationTest() throws IOException {
    var dtoAsString = "{\"translatedString\": \"word\"}";
    var dto = new TranslationResponseDto("word");
    TranslationResponseDto deserializedTranslationResponseDto = jacksonTester.parseObject(dtoAsString);

    assertSoftly(softAssertions ->
        softAssertions.assertThat(deserializedTranslationResponseDto)
            .usingRecursiveComparison()
            .isEqualTo(dto));
  }
}
