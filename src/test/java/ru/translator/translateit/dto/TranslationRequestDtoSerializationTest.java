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
@DisplayName("Smoke сериализация / десериализация TranslationRequestDto")
class TranslationRequestDtoSerializationTest {

  @Autowired
  private JacksonTester<TranslationRequestDto> jacksonTester;

  @Test
  @DisplayName("Сериализация TranslationRequestDto")
  void translationRequestDtoSerializationTest() throws IOException {
    var dto = new TranslationRequestDto("слово", "ru|en");
    JsonContent<TranslationRequestDto> json = jacksonTester.write(dto);
    SoftAssertions.assertSoftly(softAssertions -> {
      assertThat(json).extractingJsonPathStringValue("$.stringToTranslate").isEqualTo(dto.getStringToTranslate());
      assertThat(json).extractingJsonPathStringValue("$.translationParams").isEqualTo(dto.getTranslationParams());
    });
  }

  @Test
  @DisplayName("Десериализация TranslationRequestDto")
  void translationRequestDtoDeserializationTest() throws IOException {
    var dtoAsString = "{\"stringToTranslate\": \"слово\", \"translationParams\": \"ru|en\"}";
    var dto = new TranslationRequestDto("слово", "ru|en");
    TranslationRequestDto deserializedTranslationRequestDto = jacksonTester.parseObject(dtoAsString);

    assertSoftly(softAssertions ->
        softAssertions.assertThat(deserializedTranslationRequestDto)
            .usingRecursiveComparison()
            .isEqualTo(dto));
  }
}
